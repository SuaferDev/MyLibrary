package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private final Intent i = new Intent();
    private EditText edittext_name,edittext_surname,edittext_last_name,edittext_login,edittext_password;
    private ImageView image_confirm;
    private boolean booleanAcsess = false;
    private FirebaseAuth auth;
    private ImageView image_enteres;
    private TextView text_enteres;
    private DatabaseReference mRootRef;
    private RotateAnimation rotateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE); //

        LinearLayout linear_continue = findViewById(R.id.linear_continue);
        LinearLayout linear_spam = findViewById(R.id.linear_spam);
        LinearLayout linear_acsses = findViewById(R.id.linear_acsses);

        ImageView image_back = findViewById(R.id.image_back);
        image_enteres = findViewById(R.id.image_enteres);
        text_enteres = findViewById(R.id.text_enteres);
        image_confirm = findViewById(R.id.image_confirm);
        edittext_name = findViewById(R.id.edittext_name);
        edittext_surname = findViewById(R.id.edittext_surname);
        edittext_last_name = findViewById(R.id.edittext_last_name);
        edittext_login = findViewById(R.id.edittext_login);
        edittext_password = findViewById(R.id.edittext_password);

        auth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        image_back.setOnClickListener(view ->{
            i.setClass(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        });


        linear_acsses.setOnClickListener(view ->{
            if(booleanAcsess){
                image_confirm.setImageResource(R.drawable.icon_check_box);
                booleanAcsess = false;
            }else{
                booleanAcsess = true;
                image_confirm.setImageResource(R.drawable.icon_check_true);
            }
        });


        edittext_name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {edittext_name.setBackgroundResource(R.drawable.edite_text_backround);}
            @Override public void afterTextChanged(Editable editable) {}
        });
        edittext_surname.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {edittext_surname.setBackgroundResource(R.drawable.edite_text_backround);}
            @Override public void afterTextChanged(Editable editable) {}
        });
        edittext_last_name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {edittext_last_name.setBackgroundResource(R.drawable.edite_text_backround);}
            @Override public void afterTextChanged(Editable editable) {}
        });
        edittext_login.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {edittext_login.setBackgroundResource(R.drawable.edite_text_backround);}
            @Override public void afterTextChanged(Editable editable) {}
        });
        edittext_password.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {edittext_password.setBackgroundResource(R.drawable.edite_text_backround);}
            @Override public void afterTextChanged(Editable editable) {}
        });

        linear_continue.setOnClickListener(view ->{
            if(checkField()){
                registerUser(edittext_name.getText().toString(), edittext_surname.getText().toString(), edittext_last_name.getText().toString(), edittext_login.getText().toString(),edittext_password.getText().toString());
            }
        });
    }


    private boolean checkField(){
        boolean check = true;
        if(edittext_name.getText().toString().isEmpty()){
            edittext_name.setBackgroundResource(R.drawable.edite_text_error_background);check = false;
        }

        if(edittext_surname.getText().toString().isEmpty()){
            edittext_surname.setBackgroundResource(R.drawable.edite_text_error_background);check = false;
        }

        if(edittext_last_name.getText().toString().isEmpty()){
            edittext_last_name.setBackgroundResource(R.drawable.edite_text_error_background);check = false;
        }

        if(edittext_login.getText().toString().isEmpty()){
            edittext_login.setBackgroundResource(R.drawable.edite_text_error_background);check = false;
        }

        if(edittext_password.getText().toString().isEmpty()){
            edittext_password.setBackgroundResource(R.drawable.edite_text_error_background);check = false;
        }

        if(!booleanAcsess){
            image_confirm.setImageResource(R.drawable.icon_check_box_red);
        }

        return check;
    }

    private void registerUser(final String name, final String surname, final String lastname, final String email, String password) {
        auth.createUserWithEmailAndPassword(email , password).addOnSuccessListener(authResult -> {

            text_enteres.setText("Создаем аккаунт...");
            text_enteres.setTextColor(0x80131313);
            text_enteres.setBackgroundResource(R.drawable.enteres_active);
            image_enteres.setImageResource(R.drawable.icon_loaging);
            image_enteres.startAnimation(rotateAnimation);

            HashMap<Object , Object> map = new HashMap<>();
            map.put("name" , name);
            map.put("surname" , surname);
            map.put("lastname" , lastname);
            map.put("email", email);
            map.put("password", password);
            map.put("date","01/01/25");
            map.put("libraryCard", 1234567890);
            map.put("id" , Objects.requireNonNull(auth.getCurrentUser()).getUid());

            mRootRef.child("Users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegistrationActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrationActivity.this , HomeActivity.class);
                        intent.putExtra("user_email", email);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }).addOnFailureListener(e ->{
                    edittext_password.setText("");
                    text_enteres.setText("ПРОДОЛЖИТЬ");
                    text_enteres.setTextColor(0xFF131313);
                    text_enteres.setBackgroundResource(R.drawable.edite_text_backround);
                    image_enteres.setImageResource(R.drawable.icon_back_gray);
                    image_enteres.clearAnimation();
                    Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}