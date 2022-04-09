package dev.eduardoleal.ldm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnSignIn;
    private FirebaseAuth mAuth;
    TextView txtTitle;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSignIn = findViewById(R.id.btn_action_signin);
        txtTitle = findViewById(R.id.txt_signin_title);

        mAuth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    Toast.makeText(SignIn.this, "Para continuar es necesario ingresar un correo electronico válido y una contraseña", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    startActivity(intent);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                                    builder.setTitle("Mensaje");
                                    builder.setMessage("Tu usuario no se encuentra activo");
                                    builder.setPositiveButton("Cancelar", null);
                                    builder.setNegativeButton("Reenviar correo", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignIn.this, "El correo de verficiación fue enviado con éxito, revisa tu correo electronico.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(SignIn.this, "Existe un error, intenta más tarde.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(SignIn.this, "Longitud de contraseña no válida", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(SignIn.this, "Usuario o contraseña invalido", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(SignIn.this, "Existe un error con el usuario", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Log.e("FIREBASE ERROR", e.toString());
                                    Toast.makeText(SignIn.this, "Existe un error, intenta mas tarde.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });

        setUILng();
    }

    private void setUILng(){
        String defaultLng = LocaleHelper.getLanguage(SignIn.this);
        context = LocaleHelper.setLocale(SignIn.this, defaultLng);
        resources = context.getResources();

        txtTitle.setText(resources.getString(R.string.signin_title));
        edtEmail.setHint(resources.getString(R.string.signin_input_email));
        edtPassword.setHint(resources.getString(R.string.signin_input_password));
        btnSignIn.setText(resources.getString(R.string.signin_btn_signin));
    }

}