package dev.eduardoleal.ldm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    Button btnMeeting, btnSignOut;
    TextView txtTitle, txtOptions;
    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnMeeting = findViewById(R.id.btn_meet);
        btnSignOut = findViewById(R.id.btn_signout);
        txtTitle = findViewById(R.id.txt_home_title);
        txtOptions = findViewById(R.id.txt_home_options);

        btnMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Meet.class);
                startActivity(i);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        setUILng();
    }

    private void setUILng(){
        String defaultLng = LocaleHelper.getLanguage(Home.this);
        context = LocaleHelper.setLocale(Home.this, defaultLng);
        resources = context.getResources();

        txtTitle.setText(resources.getString(R.string.home_title));
        txtOptions.setHint(resources.getString(R.string.home_instructions));
        btnMeeting.setHint(resources.getString(R.string.home_btn_meet));
        btnSignOut.setText(resources.getString(R.string.home_btn_logout));
    }

}