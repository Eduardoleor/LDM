package dev.eduardoleal.ldm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import dev.eduardoleal.ldm.api.CreateGroup;
import dev.eduardoleal.ldm.api.CreateUser;
import dev.eduardoleal.ldm.api.CreateUserService;
import dev.eduardoleal.ldm.api.CreteGroupService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {

    EditText edtFullName, edtEmail, edtPassword, edtRepeatPassword, edtUserType;
    Button btnRegister;
    TextView txtTitle, txtInputFullName, txtInputEmail, txtInputPassword, txtInputRepeatPassword, txtUserType;
    Switch userSwitch;
    Context context;
    Resources resources;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtFullName = findViewById(R.id.edt_full_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtRepeatPassword = findViewById(R.id.edt_repeat_password);
        btnRegister = findViewById(R.id.btn_action_signup);

        txtTitle = findViewById(R.id.txt_signup_title);
        txtInputFullName = findViewById(R.id.txt_signup_input_fullname);
        txtInputEmail = findViewById(R.id.txt_signup_input_email);
        txtInputPassword = findViewById(R.id.txt_signup_input_password);
        txtInputRepeatPassword = findViewById(R.id.txt_signup_input_repeat_password);
        txtUserType = findViewById(R.id.txtUserType);
        userSwitch = findViewById(R.id.userSwitch);

        String defaultLng = LocaleHelper.getLanguage(SignUp.this);
        context = LocaleHelper.setLocale(SignUp.this, defaultLng);
        resources = context.getResources();
        txtUserType.setText(resources.getString(R.string.switch_user));
        userSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txtUserType.setText(resources.getString(R.string.switch_admin));
                } else {
                    txtUserType.setText(resources.getString(R.string.switch_user));
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String repeatPassword = edtRepeatPassword.getText().toString().trim();

                mAuth = FirebaseAuth.getInstance();

                if (TextUtils.isEmpty(fullName)) {
                    Toast.makeText(SignUp.this, "Debes ingresar tu nombre completo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUp.this, "Debes ingrear un correo electronico valido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password) || password.length() < 6) {
                    Toast.makeText(SignUp.this, "Debes ingresar una contraseña válida", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(repeatPassword) || !password.equals(repeatPassword)) {
                    Toast.makeText(SignUp.this, "La contraseña ingresa no coincide", Toast.LENGTH_SHORT).show();
                    return;
                }

                onRegisterUser();
            }
        });

        setUILng();
    }

    private void onRegisterUser() {
        String email, password;
        email = edtEmail.getText().toString().trim();
        password = edtPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification();
                    onSaveUser(user);
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(SignUp.this, "El correo electronico se encuentra en uso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUp.this, "Existe un error, intenta más tarde.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void onSaveUser(FirebaseUser user) {
        String id, fullName, email, userType;
        id = user.getUid();
        fullName = edtFullName.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        userType = userSwitch.isChecked() ? "admin" : "client";
        Map<String, Object> dataUser = new HashMap<>();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        if (userType.equals("client")) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.mesibo.com").addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();

            CreateUserService userService = retrofit.create(CreateUserService.class);
            Call<CreateUser> call = userService.getCreateUser("useradd", email, "dev.eduardoleal.ldm", fullName, 525600, 1, "goofbllxmyvg1jwl9apvh8fji2nkqdyw621uba2wsqx2zay8j8fe1wxx32gb50zj");
            call.enqueue(new Callback<CreateUser>() {
                @Override
                public void onResponse(Call<CreateUser> call, Response<CreateUser> response) {
                    CreateUser meetUser = response.body();
                    String meetToken = meetUser.getUser().getToken();

                    if (meetToken.length() > 0) {
                        dataUser.put("id", id);
                        dataUser.put("name", fullName);
                        dataUser.put("email", email);
                        dataUser.put("userType", userType);
                        dataUser.put("tokenMeeting", meetToken);
                        dataUser.put("groupMeeting", null);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users").document(id).set(dataUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                builder.setTitle("Mensaje");
                                builder.setMessage("Usuario creado con éxito, válida tu cuenta desde tu correo electronico.");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onBackPressed();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "There is an error saving user information.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(SignUp.this, "Existe un error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CreateUser> call, Throwable t) {
                    Toast.makeText(SignUp.this, "ERROR:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.mesibo.com").addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();

            CreateUserService userService = retrofit.create(CreateUserService.class);
            Call<CreateUser> call = userService.getCreateUser("useradd", email, "dev.eduardoleal.ldm", fullName, 525600, 1, "goofbllxmyvg1jwl9apvh8fji2nkqdyw621uba2wsqx2zay8j8fe1wxx32gb50zj");
            call.enqueue(new Callback<CreateUser>() {
                @Override
                public void onResponse(Call<CreateUser> call, Response<CreateUser> response) {
                    CreateUser meetUser = response.body();
                    String meetToken = meetUser.getUser().getToken();

                    if (meetToken.length() > 0) {
                        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.mesibo.com").addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
                        CreteGroupService groupService = retrofit.create(CreteGroupService.class);
                        Call<CreateGroup> callGroup = groupService.getCreateGroup("groupadd", 0, fullName + Math.random(), 525600, 0, 1, "goofbllxmyvg1jwl9apvh8fji2nkqdyw621uba2wsqx2zay8j8fe1wxx32gb50zj");

                        callGroup.enqueue(new Callback<CreateGroup>() {
                            @Override
                            public void onResponse(Call<CreateGroup> callGroup, Response<CreateGroup> responseGroup) {
                                CreateGroup groupUser = responseGroup.body();
                                Integer groupId = groupUser.getGroup().getGid();

                                if (groupId > 0) {
                                    dataUser.put("id", id);
                                    dataUser.put("name", fullName);
                                    dataUser.put("email", email);
                                    dataUser.put("userType", userType);
                                    dataUser.put("tokenMeeting", meetToken);
                                    dataUser.put("groupMeeting", groupId);

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("users").document(id).set(dataUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                            builder.setTitle("Mensaje");
                                            builder.setMessage("Usuario creado con éxito, válida tu cuenta desde tu correo electronico.");
                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    onBackPressed();
                                                }
                                            });

                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "There is an error saving user information.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    Toast.makeText(SignUp.this, "Existe un error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<CreateGroup> call, Throwable t) {
                                Toast.makeText(SignUp.this, "ERROR:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(SignUp.this, "Existe un error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CreateUser> call, Throwable t) {
                    Toast.makeText(SignUp.this, "ERROR:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setUILng() {
        String defaultLng = LocaleHelper.getLanguage(SignUp.this);
        context = LocaleHelper.setLocale(SignUp.this, defaultLng);
        resources = context.getResources();

        txtTitle.setText(resources.getString(R.string.signup_title));
        txtInputFullName.setText(resources.getString(R.string.signup_input_fullname));
        txtInputEmail.setText(resources.getString(R.string.signup_input_email));
        txtInputPassword.setText(resources.getString(R.string.signup_password));
        txtInputRepeatPassword.setText(resources.getString(R.string.signup_repeat_password));
        btnRegister.setText(resources.getString(R.string.signup_button_signup));
    }
}