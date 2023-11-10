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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class SearchActivity extends AppCompatActivity {

    private List<Book> allBook = new ArrayList<>();
    private List<Book> filterBook = new ArrayList<>();
    private List<Book> searhList = new ArrayList<>();
    private ListView listView;
    private final String KEY = "Book";
    private DatabaseReference bookDataBase;
    private final Intent i = new Intent();
    private SharedPreferences SaveFavorite;
    private List<Book> favoritesBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_search));


        ImageView image_back = findViewById(R.id.image_back);
        EditText edittext_search = findViewById(R.id.edittext_search);

        SaveFavorite = getSharedPreferences("favoritesBook", Activity.MODE_PRIVATE);
        String jsonFavorite = SaveFavorite.getString("favoritesBook", "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Book>>() {}.getType();
        favoritesBook = gson.fromJson(jsonFavorite, type);
        if(favoritesBook==null){favoritesBook=new ArrayList<>();}

        listView = findViewById(R.id.listView);
        bookDataBase = FirebaseDatabase.getInstance().getReference(KEY);
        getFromDataBase();

        image_back.setOnClickListener(view->{
            i.setClass(getApplicationContext(), HomeActivity.class);
            startActivity(i);
            finish();
        });

        edittext_search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                findBook(edittext_search.getText().toString());
                CustomBookAdapter adapter = new CustomBookAdapter(SearchActivity.this, searhList);
                listView.setAdapter(adapter);
            }
            @Override public void afterTextChanged(Editable editable) {}
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            createBookDialog(searhList.get(position));
        });


    }

    private void createBookDialog(Book b){
        Dialog dialog = new Dialog(SearchActivity.this);
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

    private void setupListView() {
        searhList = allBook;
        CustomBookAdapter adapter = new CustomBookAdapter(this, searhList);
        listView.setAdapter(adapter);
    }


    private void findBook(String search){
        filterBook=new ArrayList<>();
        for(Book b : allBook){
            if((stringChecking(search,b.getName())) || stringChecking(search,b.getAuthor())){
                filterBook.add(b);
            }
        }
        searhList = filterBook;
    }

    private boolean stringChecking(String s1, String s2){
        return s2.startsWith(s1) || s2.endsWith(s1) || s2.contains(s1);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        i.setClass(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();
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