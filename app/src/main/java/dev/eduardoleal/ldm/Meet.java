package dev.eduardoleal.ldm;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboSelfProfile;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.messaging.MesiboUI;

import java.util.ArrayList;
import java.util.List;

import dev.eduardoleal.ldm.api.Post;
import dev.eduardoleal.ldm.api.PostService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Meet extends AppCompatActivity implements Mesibo.ConnectionListener,
        Mesibo.MessageListener,
        Mesibo.ProfileListener, Mesibo.GroupListener {

    DemoUser mUser1 = new DemoUser("f5e48f3dde25ddb8e8fad57549d46774b8912be995aca3fe663ef6c1wa44d19627fe", "Admin LDM", "admin.ldm@ldm.com");
    DemoUser mUser2 = new DemoUser("5150409c9f4d47812a57dce595a43950c80b1c9d748159f3cbde333ef6c3xaee9e3c8304", "Client LDM", "client.ldm@ldm.com");
    DemoUser mRemoteUser;
    MesiboProfile mProfile;
    Mesibo.ReadDbSession mReadSession;
    View mLoginButton1;
    Button btnGetUsers;
    ListView list;
    ArrayList<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);

        mLoginButton1 = findViewById(R.id.login1);
        btnGetUsers = findViewById(R.id.btn_getusers);

        ArrayAdapter arrayAdapter = new ArrayAdapter(Meet.this, android.R.layout.simple_list_item_1, titles);
        list = findViewById(R.id.list);
        list.setAdapter(arrayAdapter);

        btnGetUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://jsonplaceholder.typicode.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                PostService postService = retrofit.create(PostService.class);
                Call<List<Post>> call = postService.getPost();

                call.enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        for(Post post : response.body()) {
                            titles.add(post.getTitle());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        Toast.makeText(Meet.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void mesiboInit(DemoUser user, DemoUser remoteUser) {
        Mesibo api = Mesibo.getInstance();
        api.init(getApplicationContext());

        Mesibo.addListener(this);
        Mesibo.setAccessToken(user.token);
        Mesibo.setDatabase("mydb", 0);
        Mesibo.start();

        mRemoteUser = remoteUser;
        mProfile = Mesibo.getProfile(remoteUser.address);


        // disable login buttons
        mLoginButton1.setEnabled(false);

        // Read receipts are enabled only when App is set to be in foreground
        Mesibo.setAppInForeground(this, 0, true);
        mReadSession = mProfile.createReadSession(this);
        mReadSession.enableReadReceipt(true);
        mReadSession.read(100);

        /* initialize call with custom title */
        MesiboCall.getInstance().init(this);
        MesiboCall.CallProperties cp = MesiboCall.getInstance().createCallProperties(true);
        cp.ui.title = "First App";
        MesiboCall.getInstance().setDefaultUiProperties(cp.ui);
    }

    public void onLoginUser1(View view) {
        mesiboInit(mUser1, mUser2);
    }

    public void onLoginUser2(View view) {
        mesiboInit(mUser2, mUser1);
    }

    public void onSendMessage(View view) {
        if (!isLoggedIn()) return;
    }

    public void onLaunchMessagingUi(View view) {
        if (!isLoggedIn()) return;
        MesiboUI.launchMessageView(this, mProfile.getAddress(), 0);
    }

    public void onAudioCall(View view) {
        if (!isLoggedIn()) return;
        MesiboCall.getInstance().callUi(this, mProfile.getAddress(), false);
    }

    public void onVideoCall(View view) {
        if (!isLoggedIn()) return;
        MesiboCall.getInstance().callUi(this, mProfile.getAddress(), true);
    }

    public void onUpdateProfile(View view) {
        if (!isLoggedIn()) return;
        MesiboSelfProfile profile = Mesibo.getSelfProfile();
        if (null == profile) return;
        profile.setStatus("I am using mesibo");
        profile.save();
    }

    public void onCreateGroup(View view) {
        if (!isLoggedIn()) return;
        MesiboGroupProfile.GroupSettings settings = new MesiboGroupProfile.GroupSettings();
        settings.name = "My Group";
        settings.flags = 0;
        Mesibo.createGroup(settings, this);
    }

    public void addGroupMembers(MesiboProfile profile) {
        if (!isLoggedIn()) return;
        MesiboGroupProfile gp = profile.getGroupProfile();
        String[] members = {mRemoteUser.address};
        MesiboGroupProfile.MemberPermissions mp = new MesiboGroupProfile.MemberPermissions();
        mp.flags = MesiboGroupProfile.MEMBERFLAG_ALL;
        mp.adminFlags = 0;
        gp.addMembers(members, mp);

    }

    boolean isLoggedIn() {
        if (Mesibo.STATUS_ONLINE == Mesibo.getConnectionStatus()) return true;
        toast("Login with a valid token first");
        return false;
    }

    void toast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    @Override
    public void Mesibo_onConnectionStatus(int status) {
    }

    @Override
    public boolean Mesibo_onMessage(Mesibo.MessageParams messageParams, byte[] data) {
        try {
            String message = new String(data, "UTF-8");
            toast("You have got a message: " + message);
        } catch (Exception e) {
        }

        return true;
    }

    @Override
    public void Mesibo_onMessageStatus(Mesibo.MessageParams messageParams) {
    }

    @Override
    public void Mesibo_onActivity(Mesibo.MessageParams messageParams, int i) {

    }

    @Override
    public void Mesibo_onLocation(Mesibo.MessageParams messageParams, Mesibo.Location location) {

    }

    @Override
    public void Mesibo_onFile(Mesibo.MessageParams messageParams, Mesibo.FileInfo fileInfo) {

    }

    /* Mesibo Group Listener */
    @Override
    public void Mesibo_onGroupCreated(MesiboProfile profile) {
        toast("New Group Created: " + profile.getName());
        addGroupMembers(profile);
    }

    @Override
    public void Mesibo_onGroupJoined(MesiboProfile profile) {

    }

    @Override
    public void Mesibo_onGroupLeft(MesiboProfile profile) {

    }

    @Override
    public void Mesibo_onGroupMembers(MesiboProfile profile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupMembersJoined(MesiboProfile profile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupMembersRemoved(MesiboProfile profile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupSettings(MesiboProfile mesiboProfile, MesiboGroupProfile.GroupSettings groupSettings, MesiboGroupProfile.MemberPermissions memberPermissions, MesiboGroupProfile.GroupPin[] groupPins) {

    }

    @Override
    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long l) {

    }

    /* Mesibo Profile Listener */
    @Override
    public void Mesibo_onProfileUpdated(MesiboProfile profile) {
        toast(profile.getName() + " has updated profile");
    }

    @Override
    public boolean Mesibo_onGetProfile(MesiboProfile profile) {
        return false;
    }

    class DemoUser {
        public String token;
        public String name;
        public String address;

        DemoUser(String token, String name, String address) {
            this.token = token;
            this.name = name;
            this.address = address;
        }
    }
}