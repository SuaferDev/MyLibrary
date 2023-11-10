package com.example.mylibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class EnteresActivity extends AppCompatActivity {

    private final Intent i = new Intent();
    private FirebaseAuth auth;

    private EditText edittext_login, edittext_password;
    private ImageView image_enteres;
    private TextView text_enteres;
    private boolean remember_me = false;
    private boolean employ = false;
    private RotateAnimation rotateAnimation;
    private SharedPreferences SaveLogin, SavePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enteres);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        SaveLogin = getSharedPreferences("login", Activity.MODE_PRIVATE);
        SavePassword =getSharedPreferences("password", Activity.MODE_PRIVATE);

        ImageView image_back =  findViewById(R.id.image_back);
        ImageView image_remember_me =  findViewById(R.id.image_remember_me);
        LinearLayout linear_employ = findViewById(R.id.linear_employ);
        ImageView image_employ = findViewById(R.id.image_employ);
        image_enteres = findViewById(R.id.image_enteres);
        text_enteres = findViewById(R.id.text_enteres);
        LinearLayout linear_continue = findViewById(R.id.linear_continue);
        LinearLayout linear_remember_me = findViewById(R.id.linear_remember_me);
        edittext_login = findViewById(R.id.edittext_login);
        edittext_password = findViewById(R.id.edittext_password);
        auth = FirebaseAuth.getInstance();

        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE); //

        image_back.setOnClickListener(view ->{
            i.setClass(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        });



        linear_remember_me.setOnClickListener(view ->{
            if(remember_me){
                image_remember_me.setImageResource(R.drawable.icon_check_box);
                remember_me = false;
            }else{
                remember_me = true;
                image_remember_me.setImageResource(R.drawable.icon_check_true);
            }
        });

        linear_continue.setOnClickListener(view -> {
            if(!edittext_login.getText().toString().isEmpty() && !edittext_password.getText().toString().isEmpty()){
                loginUser(edittext_login.getText().toString() , edittext_password.getText().toString());
            }else{
                setError();
            }
        });

        linear_employ.setOnClickListener(view ->{
            if(employ){
                image_employ.setImageResource(R.drawable.icon_check_box);
                employ = false;
            }else{
                employ = true;
                image_employ.setImageResource(R.drawable.icon_check_true);
            }
        });

        edittext_login.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edittext_login.setBackgroundResource(R.drawable.edite_text_backround);
            }
            @Override public void afterTextChanged(Editable editable) {}
        });

        edittext_password.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edittext_password.setBackgroundResource(R.drawable.edite_text_backround);
            }
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    private void setError(){
        edittext_login.setBackgroundResource(R.drawable.edite_text_error_background);
        edittext_password.setBackgroundResource(R.drawable.edite_text_error_background);
    }


    private void loginUser(String email, String password){
        text_enteres.setText("Входим...");
        text_enteres.setTextColor(0x80131313);
        text_enteres.setBackgroundResource(R.drawable.enteres_active);
        image_enteres.setImageResource(R.drawable.icon_loaging);
        image_enteres.startAnimation(rotateAnimation);
        auth.signInWithEmailAndPassword(email , password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                UserData.getInstance().setLogin(email);
                if(remember_me){
                    SaveLogin.edit().putString("login", email).apply();
                    SavePassword.edit().putString("password", password).apply();
                }
                if(employ){
                    Toast.makeText(EnteresActivity.this, "Мы рады Вас видеть", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EnteresActivity.this , WorkActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{

                }

            }
        }).addOnFailureListener(e -> {
            edittext_password.setText("");
            text_enteres.setText("ПРОДОЛЖИТЬ");
            text_enteres.setTextColor(0xFF131313);
            text_enteres.setBackgroundResource(R.drawable.edite_text_backround);
            image_enteres.setImageResource(R.drawable.icon_back_gray);
            image_enteres.clearAnimation();
            setError();
        });
    }
}