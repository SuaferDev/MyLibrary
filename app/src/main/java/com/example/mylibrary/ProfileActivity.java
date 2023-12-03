package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class ProfileActivity extends AppCompatActivity {

    private final Intent i = new Intent();
    private SharedPreferences SaveFavorite;
    private TextView text_name, text_library_card, text_check, text_date;
    private ListView listView;
    private List<Book> favoritesBook;
    private List<Book> issuedBook = new ArrayList<>();
    private CustomBookAdapter adapter;
    private CustomIssuedBookAdapter adapterIssued;
    private int status = 2;
    private String userEmail;
    private Animation mFadeInAnimation;
    private DatabaseReference bookDataBase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_search));

        userEmail = UserData.getInstance().getLogin();

        text_name = findViewById(R.id.text_name);
        text_library_card = findViewById(R.id.text_library_card);
        text_date = findViewById(R.id.text_date);
        text_check = findViewById(R.id.text_check);
        TextView text_mybook = findViewById(R.id.text_mybook);
        TextView text_want = findViewById(R.id.text_want);
        ImageView image_back = findViewById(R.id.image_back);
        ImageView image_setting = findViewById(R.id.image_setting);
        listView = findViewById(R.id.listView);
        bookDataBase = FirebaseDatabase.getInstance().getReference("Book");

        SaveFavorite = getSharedPreferences("favoritesBook", Activity.MODE_PRIVATE);
        String jsonFavorite = SaveFavorite.getString("favoritesBook", "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Book>>() {}.getType();
        favoritesBook = gson.fromJson(jsonFavorite, type);
        if(favoritesBook==null){favoritesBook=new ArrayList<>();}


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String surname = snapshot.child("surname").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    int libraryCard = snapshot.child("libraryCard").getValue(Integer.class);
                    userId = snapshot.child("id").getValue(String.class);
                    text_name.setText(String.valueOf(name+" "+ surname));
                    text_date.setText(String.valueOf(date));
                    text_library_card.setText(String.valueOf(libraryCard));
                }
                //Toast.makeText(ProfileActivity.this,userId,Toast.LENGTH_LONG).show();
                getFromDataBase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,"Не удалось получить информацию", Toast.LENGTH_LONG).show();
                System.out.println("Ошибка: " + databaseError.getMessage());
            }
        });






        text_want.setOnClickListener(view ->{
            text_want.setBackgroundResource(R.drawable.rounded_background_black); text_want.setTextColor(0xFFFFFFFF);
            text_mybook.setTextColor(0xFF000000); text_mybook.setBackgroundColor(0x00000000);
            status = 1;

            adapter = new CustomBookAdapter(this, favoritesBook);
            mFadeInAnimation = AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.fade_id);
            mFadeInAnimation.setAnimationListener(animationFadeInListener);
            listView.startAnimation(mFadeInAnimation);
            listView.setAdapter(adapter);
            if(favoritesBook.isEmpty()){text_check.setText("Избранных книг нет");}else{text_check.setText("");}
        });

        text_mybook.setOnClickListener(view ->{
            text_mybook.setBackgroundResource(R.drawable.rounded_background_black); text_mybook.setTextColor(0xFFFFFFFF);
            text_want.setTextColor(0xFF000000); text_want.setBackgroundColor(0x00000000);

            adapterIssued = new CustomIssuedBookAdapter(this, issuedBook);
            mFadeInAnimation = AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.fade_id);
            mFadeInAnimation.setAnimationListener(animationFadeInListener);
            listView.startAnimation(mFadeInAnimation);
            listView.setAdapter(adapterIssued);
            if(issuedBook.isEmpty()){text_check.setText("Выданных книг нет");}else{text_check.setText("");}
            status = 2;
        });

        image_back.setOnClickListener(view ->{
            i.setClass(getApplicationContext(), HomeActivity.class);
            startActivity(i);
        });

        image_setting.setOnClickListener(view ->{
            i.setClass(getApplicationContext(), WorkActivity.class);
            startActivity(i);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(status==1 && !favoritesBook.isEmpty()){
                createFavoriteBookDialog(favoritesBook.get(position));
            }else{
                if(!issuedBook.isEmpty()){
                    createIssuedBookDialog(issuedBook.get(position));
                }
            }
        });
    }

    private void getFromDataBase() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                issuedBook = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    if (book != null && Objects.equals(book.getStatus(), userId)) {
                        issuedBook.add(book);
                    }
                }
                CustomIssuedBookAdapter adapter = new CustomIssuedBookAdapter(ProfileActivity.this, issuedBook);
                mFadeInAnimation = AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.fade_id);
                mFadeInAnimation.setAnimationListener(animationFadeInListener);
                listView.startAnimation(mFadeInAnimation);
                listView.setAdapter(adapter);


                if(issuedBook.isEmpty()){
                    text_check.setText("Выданных книг нет");
                }else{
                    text_check.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", error.getMessage());}
        };
        bookDataBase.addValueEventListener(valueEventListener);
    }

    private void createFavoriteBookDialog(Book b){
        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.custom_dialog_book);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView text_name = dialog.findViewById(R.id.text_name);
        TextView text_author = dialog.findViewById(R.id.text_author);
        TextView text_description = dialog.findViewById(R.id.text_description);
        LinearLayout liner_get_button = dialog.findViewById(R.id.liner_get_button);
        ImageView image_close = dialog.findViewById(R.id.image_close);
        ImageView image_add = dialog.findViewById(R.id.image_add);

        text_name.setText(b.getName());
        text_author.setText(b.getAuthor());
        text_description.setText(b.getDescription());

        image_add.setOnClickListener(view ->{
            for(int i=0; i<favoritesBook.size(); i++){
                if(favoritesBook.get(i).getId()==b.getId()){
                    favoritesBook.remove(i);
                    adapterIssued = new CustomIssuedBookAdapter(this, issuedBook);
                    listView.setAdapter(adapterIssued);
                    if(issuedBook.isEmpty()){text_check.setText("Выданных книг нет");}else{text_check.setText("");}
                    status = 2;
                    dialog.dismiss();
                    break;
                }
            }
        });

        image_close.setOnClickListener(view ->{dialog.dismiss();});

        liner_get_button.setOnClickListener(view -> {createQRDialog(b);});
        dialog.show();
    }

    private void createIssuedBookDialog(Book b){
        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.custom_dialog_return_book);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView text_name = dialog.findViewById(R.id.text_name);
        TextView text_author = dialog.findViewById(R.id.text_author);
        TextView text_date = dialog.findViewById(R.id.text_date);
        ImageView image_close = dialog.findViewById(R.id.image_close);
        ImageView image_qr_code = dialog.findViewById(R.id.image_qr_code);

        text_name.setText(b.getName());
        text_author.setText(b.getAuthor());
        text_date.setText(b.getDate());

        text_name.setText(b.getName());
        text_author.setText(b.getAuthor());

        Bitmap bmp = Profile.createQRCode(b, userId);
        if(bmp!=null){image_qr_code.setImageBitmap(bmp);
        }else{Toast.makeText(ProfileActivity.this, "Не удалось создать QR Код", Toast.LENGTH_LONG).show();}

        image_close.setOnClickListener(view ->{dialog.dismiss();});

        dialog.show();
    }

    private void createQRDialog(Book b){
        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.custom_dialog_qr_generator);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView text_name = dialog.findViewById(R.id.text_name);
        TextView text_author = dialog.findViewById(R.id.text_author);
        ImageView image_close = dialog.findViewById(R.id.image_close);
        ImageView image_qr_code = dialog.findViewById(R.id.image_qr_code);

        text_name.setText(b.getName());
        text_author.setText(b.getAuthor());

        Bitmap bmp = Profile.createQRCode(b, userId);
        if(bmp!=null){image_qr_code.setImageBitmap(bmp);
        }else{Toast.makeText(ProfileActivity.this, "Не удалось создать QR Код", Toast.LENGTH_LONG).show();}

        image_close.setOnClickListener(view ->{dialog.dismiss();});

        dialog.show();
    }

    Animation.AnimationListener animationFadeInListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @Override
        public void onAnimationStart(Animation animation) {}
    };

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editorFavorite = SaveFavorite.edit();
        Gson gsonMap = new Gson();
        String jsonFavorites = gsonMap.toJson(favoritesBook);
        editorFavorite.putString("favoritesBook", jsonFavorites);
        editorFavorite.apply();
    }
}