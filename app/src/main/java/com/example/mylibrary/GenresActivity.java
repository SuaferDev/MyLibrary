package com.example.mylibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class GenresActivity extends AppCompatActivity {

    private List<Book> allBook = new ArrayList<>();
    private List<Book> filterBook = new ArrayList<>();
    private List<Book> searhList = new ArrayList<>();
    private ListView listView;
    private final String KEY = "Book";
    private DatabaseReference bookDataBase;
    private final Intent i = new Intent();
    private List<String> genresList = new ArrayList<>();
    private SharedPreferences SaveFavorite;
    private List<Book> favoritesBook;
    private TextView text_prose, text_history, text_roman, text_horror, text_fantasy, text_rasskaz, text_biografy;
    private CustomBookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        ImageView image_back = findViewById(R.id.image_back);
        listView = findViewById(R.id.listView);

        SaveFavorite = getSharedPreferences("favoritesBook", Activity.MODE_PRIVATE);
        String jsonFavorite = SaveFavorite.getString("favoritesBook", "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Book>>() {}.getType();
        favoritesBook = gson.fromJson(jsonFavorite, type);
        if(favoritesBook==null){favoritesBook=new ArrayList<>();}

        text_prose = findViewById(R.id.text_prose);
        text_history = findViewById(R.id.text_history);
        text_roman = findViewById(R.id.text_roman);
        text_horror = findViewById(R.id.text_horror);
        text_fantasy = findViewById(R.id.text_fantasy);
        text_rasskaz = findViewById(R.id.text_rasskaz);
        text_biografy = findViewById(R.id.text_biografy);

        text_prose.setOnClickListener(view ->{
            addGenres(text_prose,"проза"); setGenres();
            adapter = new CustomBookAdapter(this, searhList);
            listView.setAdapter(adapter);
        });
        text_history.setOnClickListener(view ->{
            addGenres(text_history,"исторический жанр"); setGenres();
            adapter = new CustomBookAdapter(this, searhList);
            listView.setAdapter(adapter);
        });
        text_roman.setOnClickListener(view ->{
            addGenres(text_roman,"роман"); setGenres();
            adapter = new CustomBookAdapter(this, searhList);
            listView.setAdapter(adapter);
        });
        text_horror.setOnClickListener(view ->{
            addGenres(text_horror,"ужасы"); setGenres();
            adapter = new CustomBookAdapter(this, searhList);
            listView.setAdapter(adapter);
        });
        text_fantasy.setOnClickListener(view ->{
            addGenres(text_fantasy,"фантастика"); setGenres();
            adapter = new CustomBookAdapter(this, searhList);
            listView.setAdapter(adapter);
        });
        text_rasskaz.setOnClickListener(view ->{
            addGenres(text_rasskaz,"рассказ"); setGenres();
            adapter = new CustomBookAdapter(this, searhList);
            listView.setAdapter(adapter);
        });
        text_biografy.setOnClickListener(view ->{
            addGenres(text_biografy,"автобиография"); setGenres();
            adapter = new CustomBookAdapter(this, searhList);
            listView.setAdapter(adapter);
        });


        listView = findViewById(R.id.listView);
        bookDataBase = FirebaseDatabase.getInstance().getReference(KEY);
        getFromDataBase();

        image_back.setOnClickListener(view->{
            i.setClass(getApplicationContext(), HomeActivity.class);
            startActivity(i);
            finish();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            createBookDialog(searhList.get(position));
        });
    }

    private void createBookDialog(Book b){
        Dialog dialog = new Dialog(GenresActivity.this);
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
    private void getFromDataBase() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allBook = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    if (book != null) {
                        allBook.add(book);
                    }
                }
                setupListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", error.getMessage());
            }
        };
        bookDataBase.addValueEventListener(valueEventListener);
    }

    private void setGenres(){
        if(genresList.isEmpty()){
            searhList = allBook;
        }else{
            filterBook=new ArrayList<>();
            for(String genres : genresList){
                findBook(genres);
            }
            searhList = filterBook;
        }
    }

    private void findBook(String genres){
        for(Book b : allBook){
            for(String s : b.getGenres().split(",")){
                if(genres.equals(s) && checkBook(b.getId())){
                    filterBook.add(b);
                    break;
                }
            }
        }
    }
    private boolean checkBook(int id){
        for(Book b : filterBook){
            if(b.getId()==id){
                return false;
            }
        }
        return true;
    }


    private void setupListView() {
        searhList = allBook;
        CustomBookAdapter adapter = new CustomBookAdapter(this, searhList);
        listView.setAdapter(adapter);
    }

    private void addGenres(TextView textView, String s){
        boolean check = true;
        for(String genres : genresList){
            if(genres.equals(s)){
                textView.setTextColor(0xFF000000);
                textView.setBackgroundResource(R.drawable.corner_background_black);
                genresList.remove(genres);
                check = false;
                break;
            }
        }
        if(check){
            genresList.add(s);
            textView.setBackgroundResource(R.drawable.chose_genres);
            textView.setTextColor(0xFFFFFFFF);

        }
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