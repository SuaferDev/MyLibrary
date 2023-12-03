package com.example.mylibrary;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Work {

    public int getBook(String userId, Book b){
        try{
            FirebaseDatabase databaseBook = FirebaseDatabase.getInstance();
            DatabaseReference myRefBook = databaseBook.getReference("Book");
            myRefBook.child(String.valueOf(b.getId())).child("date").setValue("-");
            myRefBook.child(String.valueOf(b.getId())).child("status").setValue("0");

        }catch (Exception e){
            return -1;
        }
        return 0;
    }

    public int giveBook(Book b, String userId){
        try{
            if(b!=null){
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yy");
                String dateString = dateFormat.format(date);
                String[] dateParts = dateString.split(" ");
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1])+1;
                int year = Integer.parseInt(dateParts[2]);
                String d, m, y;
                if(day>=10){d = String.valueOf(day);
                }else{d = "0"+day;}

                if(month>=10){m = String.valueOf(month);
                }else{m = "0"+month;}

                if(year>=10){y = String.valueOf(year);
                }else{y = "0"+year;}

                FirebaseDatabase databaseBook = FirebaseDatabase.getInstance();
                DatabaseReference myRefBook = databaseBook.getReference("Book");
                myRefBook.child(String.valueOf(b.getId())).child("date").setValue(d+"/"+m+"/"+y);
                myRefBook.child(String.valueOf(b.getId())).child("status").setValue(userId);
                return 1;
            }

        }catch (Exception e){
            return -1;
        }
        return 0;
    }
    public int addBook(String name, String author, String genres, String description, int lastId){
        if(!name.isEmpty() && !author.isEmpty() && !genres.isEmpty() && !description.isEmpty()){
            genres = genres.replaceAll(" ", "");
            HashMap<Object , Object> map = new HashMap<>();
            map.put("name" , name);
            map.put("author" , author);
            map.put("genres" , genres);
            map.put("description", description);
            map.put("status","0");
            map.put("date","-");
            map.put("id" , lastId+1);
            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
            try{
                mRootRef.child("Book").child(String.valueOf(lastId+1)).setValue(map);
                return 1;
            }catch (Exception e){
                return -1;
            }
        }
        return 0;
    }
}
