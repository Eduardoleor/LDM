package dev.eduardoleal.ldm;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnSignUp;
    ImageButton btnLng;
    TextView txtTitle, txtSubtitle;

    Context context;
    Resources resources;
    int lang_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.btn_signin);
        btnSignUp = findViewById(R.id.btn_signup);
        btnLng = findViewById(R.id.btn_lng);
        txtTitle = findViewById(R.id.txt_main_title);
        txtSubtitle = findViewById(R.id.txt_main_subtitle);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        btnLng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeLngDialog();
            }
        });

        setUILng();
    }

    private void onChangeLngDialog() {
        final String[] Language = {resources.getString(R.string.dialog_option_en), resources.getString(R.string.dialog_option_es), resources.getString(R.string.dialog_option_pt)};
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setTitle(resources.getString(R.string.dialog_lng_title)).setSingleChoiceItems(Language, lang_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Language[i].equals(resources.getString(R.string.dialog_option_en))) {
                    context = LocaleHelper.setLocale(MainActivity.this, "en");
                    resources = context.getResources();
                    lang_selected = 0;

                    txtTitle.setText(resources.getString(R.string.main_title));
                    txtSubtitle.setText(resources.getString(R.string.main_subtitle));
                    btnSignIn.setText(resources.getString(R.string.main_btn_signin));
                    btnSignUp.setText(resources.getString(R.string.main_btn_signup));

                    dialogInterface.dismiss();
                }
                if (Language[i].equals(resources.getString(R.string.dialog_option_es))) {
                    context = LocaleHelper.setLocale(MainActivity.this, "es");
                    resources = context.getResources();
                    lang_selected = 1;

                    txtTitle.setText(resources.getString(R.string.main_title));
                    txtSubtitle.setText(resources.getString(R.string.main_subtitle));
                    btnSignIn.setText(resources.getString(R.string.main_btn_signin));
                    btnSignUp.setText(resources.getString(R.string.main_btn_signup));

                    dialogInterface.dismiss();
                }
                if (Language[i].equals(resources.getString(R.string.dialog_option_pt))) {
                    context = LocaleHelper.setLocale(MainActivity.this, "pt");
                    resources = context.getResources();
                    lang_selected = 2;

                    txtTitle.setText(resources.getString(R.string.main_title));
                    txtSubtitle.setText(resources.getString(R.string.main_subtitle));
                    btnSignIn.setText(resources.getString(R.string.main_btn_signin));
                    btnSignUp.setText(resources.getString(R.string.main_btn_signup));

                    dialogInterface.dismiss();
                }
            }
        }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.create().show();
    }

    private void setUILng(){
        String defaultLng = LocaleHelper.getLanguage(MainActivity.this);
        context = LocaleHelper.setLocale(MainActivity.this, defaultLng);
        resources = context.getResources();

        txtTitle.setText(resources.getString(R.string.main_title));
        txtSubtitle.setText(resources.getString(R.string.main_subtitle));
        btnSignIn.setText(resources.getString(R.string.main_btn_signin));
        btnSignUp.setText(resources.getString(R.string.main_btn_signup));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            Intent i = new Intent(MainActivity.this, Home.class);
            startActivity(i);
        }
    }
}
