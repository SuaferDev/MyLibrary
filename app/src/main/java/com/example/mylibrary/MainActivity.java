package com.example.mylibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Intent i = new Intent();
    private FirebaseAuth auth;

    private SharedPreferences SaveLogin, SavePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        TextView textEnteres = findViewById(R.id.textEnteres);
        TextView textCreateAccaunt = findViewById(R.id.textCreateAccaunt);
        auth = FirebaseAuth.getInstance();

        SaveLogin = getSharedPreferences("login", Activity.MODE_PRIVATE);
        SavePassword =getSharedPreferences("password", Activity.MODE_PRIVATE);

        String login = SaveLogin.getString("login", ""); String password = SavePassword.getString("password", "");

        if (!login.isEmpty() && login!=null && password!=null && !password.isEmpty()) {
            loginUser(login, password);
        }

        textEnteres.setOnClickListener(view ->{
            i.setClass(getApplicationContext(), EnteresActivity.class);
            startActivity(i);
            finish();
        });

        textCreateAccaunt.setOnClickListener(view ->{
            i.setClass(getApplicationContext(), RegistrationActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void loginUser(String email, String password){
        auth.signInWithEmailAndPassword(email , password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                UserData.getInstance().setLogin(email);
                Toast.makeText(MainActivity.this, "Мы рады Вас видеть", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this , HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(e -> {
        });
    }


}