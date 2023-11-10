package com.example.mylibrary;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WorkActivity extends AppCompatActivity {

    private  TextView text_name, text_book;
    private Book b;
    private List<Book> allBooks;
    private DatabaseReference DataBase;
    private int lastId=0;
    private String userId;
    private List<Book> issuedBook;
    private ListView listView;
    private Animation mFadeInAnimation;
    private TextView text_check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_home));

        text_check = findViewById(R.id.text_check);
        LinearLayout linear_add_book = findViewById(R.id.linear_add_book);
        LinearLayout linear_give_book = findViewById(R.id.linear_give_book);
        LinearLayout linear_get_reader = findViewById(R.id.linear_get_reader);
        listView = findViewById(R.id.listView);


        linear_give_book.setOnClickListener(view -> {
            createGiveBookDialog();
        });

        linear_add_book.setOnClickListener(view -> {
            createAddBookDialog();
        });

        linear_get_reader.setOnClickListener(view -> {
            createGetBookDialog();
        });

        DataBase = FirebaseDatabase.getInstance().getReference("Book");
        getFromDataBase();

    }

    private void getFromDataBase() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allBooks = new ArrayList<>();
                issuedBook = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Book book = ds.getValue(Book.class);
                    if(book!=null){
                        allBooks.add(book);
                        if(!Objects.equals(book.getStatus(), "0")) {
                            issuedBook.add(book);
                        }
                    }
                }
                lastId = allBooks.get(allBooks.size()-1).getId();
                Toast.makeText(WorkActivity.this, String.valueOf(allBooks.size()), Toast.LENGTH_LONG).show();
                CustomIssuedBookAdapter adapterIssued = new CustomIssuedBookAdapter(WorkActivity.this, issuedBook);
                mFadeInAnimation = AnimationUtils.loadAnimation(WorkActivity.this, R.anim.fade_id);
                mFadeInAnimation.setAnimationListener(animationFadeInListener);
                listView.startAnimation(mFadeInAnimation);
                listView.setAdapter(adapterIssued);
                if(issuedBook.isEmpty()){text_check.setText("Все книги на месте");}else{text_check.setText("");}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WorkActivity.this, "Не удалось подключиться к сети", Toast.LENGTH_LONG).show();
                Log.d("TAG", error.getMessage());
            }
        };
        DataBase.addValueEventListener(valueEventListener);
    }

    private void createAddBookDialog() {
        Dialog dialog = new Dialog(WorkActivity.this);
        dialog.setContentView(R.layout.cutom_dialog_add_book);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        ImageView image_close = dialog.findViewById(R.id.image_close);
        LinearLayout liner_add_button = dialog.findViewById(R.id.liner_add_button);
        EditText edittext_name = dialog.findViewById(R.id.edittext_name);
        EditText edittext_author = dialog.findViewById(R.id.edittext_author);
        EditText edittext_genres = dialog.findViewById(R.id.edittext_genres);
        EditText edittext_description = dialog.findViewById(R.id.edittext_description);

        Toast.makeText(WorkActivity.this, String.valueOf(lastId), Toast.LENGTH_LONG).show();

        image_close.setOnClickListener(view ->{
            dialog.dismiss();
        });

        liner_add_button.setOnClickListener(view ->{
            if(Work.addBook(edittext_name.getText().toString(), edittext_author.getText().toString(),edittext_genres.getText().toString(), edittext_description.getText().toString(), lastId)==1){
                Toast.makeText(WorkActivity.this, "Книга добавлена", Toast.LENGTH_LONG).show();
                lastId+=1;
            }else{
                Toast.makeText(WorkActivity.this, "Ошибка добавления", Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
            /*
            if(!edittext_name.getText().toString().isEmpty() && !edittext_author.getText().toString().isEmpty() && !edittext_genres.getText().toString().isEmpty() && !edittext_description.getText().toString().isEmpty() && lastId!=0){
                String name = edittext_name.getText().toString();
                String author = edittext_author.getText().toString();
                String genres = edittext_genres.getText().toString().replaceAll(" ", "");
                String description = edittext_description.getText().toString();
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

                HashMap<Object , Object> map = new HashMap<>();
                map.put("name" , name);
                map.put("author" , author);
                map.put("genres" , genres);
                map.put("description", description);
                map.put("status","0");
                map.put("date","");
                map.put("id" , lastId+1);
                try{
                    mRootRef.child("Book").child(String.valueOf(lastId+1)).setValue(map);
                    lastId+=1;
                    Toast.makeText(WorkActivity.this, "Книга добавлена", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }catch (Exception e){
                    Toast.makeText(WorkActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(WorkActivity.this, "Ошибка с данными", Toast.LENGTH_LONG).show();
            }

             */
        });

        dialog.show();
    }

    private void createGiveBookDialog() {
        Dialog dialog = new Dialog(WorkActivity.this);
        dialog.setContentView(R.layout.custom_dialog_give_book);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        ImageView image_close = dialog.findViewById(R.id.image_close);
        text_name = dialog.findViewById(R.id.text_name);
        text_book = dialog.findViewById(R.id.text_book);
        LinearLayout liner_get_button = dialog.findViewById(R.id.liner_get_button);

        image_close.setOnClickListener(view ->{
            dialog.dismiss();
        });

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();

        liner_get_button.setOnClickListener(view ->{
            if(Work.giveBook(b, userId)==1){
                Toast.makeText(WorkActivity.this,"Выдача книги зафиксирована",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(WorkActivity.this,"Ошибка фиксации выдачи",Toast.LENGTH_LONG).show();
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void createGetBookDialog() {
        Dialog dialog = new Dialog(WorkActivity.this);
        dialog.setContentView(R.layout.custom_dialog_get_book);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        ImageView image_close = dialog.findViewById(R.id.image_close);
        text_name = dialog.findViewById(R.id.text_name);
        text_book = dialog.findViewById(R.id.text_book);
        LinearLayout liner_get_button = dialog.findViewById(R.id.liner_get_button);

        image_close.setOnClickListener(view ->{
            dialog.dismiss();
        });

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();

        liner_get_button.setOnClickListener(view ->{
                Work.getBook(userId, b);
                dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String s = result.getContents();
                Toast.makeText(WorkActivity.this, s, Toast.LENGTH_LONG).show();
                String[] arrValue = s.split("/");
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.orderByChild("id").equalTo(arrValue[1]).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.child("name").getValue(String.class);
                            String surname = snapshot.child("surname").getValue(String.class);
                            userId = arrValue[1];
                            text_name.setText(String.valueOf(name+" "+ surname));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("Ошибка: " + databaseError.getMessage());
                    }
                });
                DatabaseReference databaseReferenceBook = FirebaseDatabase.getInstance().getReference("Book");
                databaseReferenceBook.orderByChild("id").equalTo(Integer.parseInt(arrValue[0])).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            b = snapshot.getValue(Book.class);
                            if(b!=null){
                                text_book.setText(b.getName());
                            }else{
                                Toast.makeText(WorkActivity.this, "Книга не найдена", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("Ошибка: " + databaseError.getMessage());
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    Animation.AnimationListener animationFadeInListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @Override
        public void onAnimationStart(Animation animation) {}
    };
}