package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private final Intent i = new Intent();
    private SharedPreferences SaveFavorite;
    public static List<Book> favoritesBook, allBooks;
    private DatabaseReference DataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        TextView text_location = findViewById(R.id.text_location);

        LinearLayout layout_location = findViewById(R.id.layout_location);
        LinearLayout layout_search = findViewById(R.id.layout_search);
        LinearLayout linear_search_genres = findViewById(R.id.linear_search_genres);
        LinearLayout linear_random = findViewById(R.id.linear_random);
        ImageView image_copy = findViewById(R.id.image_copy);
        TextView text_avatar = findViewById(R.id.text_avatar);

        SaveFavorite = getSharedPreferences("favoritesBook", Activity.MODE_PRIVATE);
        String jsonFavorite = SaveFavorite.getString("favoritesBook", "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Book>>() {}.getType();
        favoritesBook = gson.fromJson(jsonFavorite, type);
        if(favoritesBook==null){favoritesBook=new ArrayList<>();}

        DataBase = FirebaseDatabase.getInstance().getReference("Book");
        getFromDataBase();

        text_avatar.setOnClickListener(view ->{
            i.setClass(getApplicationContext(), ProfileActivity.class);
            SharedPreferences.Editor editorFavorite = SaveFavorite.edit();
            Gson gsonMap = new Gson();
            String jsonFavorites = gsonMap.toJson(favoritesBook);
            editorFavorite.putString("favoritesBook", jsonFavorites);
            editorFavorite.apply();
            startActivity(i);
        });

        linear_search_genres.setOnClickListener(view ->{
            i.setClass(getApplicationContext(), GenresActivity.class);
            startActivity(i);
        });


        layout_search.setOnClickListener(view ->{
            i.setClass(getApplicationContext(), SearchActivity.class);
            startActivity(i);
        });

        layout_location.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Текст", text_location.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Адрес скопирован", Toast.LENGTH_SHORT).show();
            Animation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(400);
            image_copy.setImageResource(R.drawable.icon_copy);
            image_copy.startAnimation(rotateAnimation);
            rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {image_copy.setImageResource(R.drawable.icon_ok);}
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        linear_random.setOnClickListener(view -> {
            getRandomBook();
        });


    }

    private void getFromDataBase() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allBooks = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    if (book != null) {
                        allBooks.add(book);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Не удалось подключиться к сети", Toast.LENGTH_LONG).show();
                Log.d("TAG", error.getMessage());
            }
        };
        DataBase.addValueEventListener(valueEventListener);
    }

    private void getRandomBook(){
        Random random = new Random();
        int randomIndex = random.nextInt(allBooks.size());
        Book randomBook = allBooks.get(randomIndex);
        createBookDialog(randomBook);
    }

    private void createBookDialog(Book b){
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.custom_dialog_book);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView text_name = dialog.findViewById(R.id.text_name);
        TextView text_author = dialog.findViewById(R.id.text_author);
        TextView text_description = dialog.findViewById(R.id.text_description);
        ImageView image_close = dialog.findViewById(R.id.image_close);
        ImageView image_add = dialog.findViewById(R.id.image_add);


        text_name.setText(b.getName());
        text_author.setText(b.getAuthor());
        text_description.setText(b.getDescription());

        int n = -1;
        boolean checkBook = false;
        for(int i =0; i<favoritesBook.size(); i++){
            if(favoritesBook.get(i).getId() == b.getId()){
                image_add.setImageResource(R.drawable.icon_favorite_added);
                checkBook = true; n = i;
                break;
            }
        }
        boolean finalCheckBook = checkBook;
        int finalN = n;
        image_add.setOnClickListener(view -> {
            if(finalCheckBook){
                image_add.setImageResource(R.drawable.icon_favorite);
                favoritesBook.remove(finalN);
            }else{
                image_add.setImageResource(R.drawable.icon_favorite_added);
                favoritesBook.add(b);
            }
        });


        image_close.setOnClickListener(view ->{
            dialog.dismiss();
        });


        dialog.show();
    }

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