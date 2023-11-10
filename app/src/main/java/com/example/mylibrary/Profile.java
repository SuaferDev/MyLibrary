package com.example.mylibrary;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile {

    public static Map<String, String> getData(final List<Book> issuedBook, String userEmail){
        Map<String, String> map = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String surname = snapshot.child("surname").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String libraryCard = String.valueOf(snapshot.child("libraryCard").getValue(Integer.class));
                    map.put("name",name);
                    map.put("surname",surname);
                    map.put("date",date);
                    map.put("libraryCard",libraryCard);
                    map.put("userId",snapshot.child("id").getValue(String.class));

                    DataSnapshot d = snapshot.child("issuedBook");
                    for (DataSnapshot dB : d.getChildren()) {
                        Book book = dB.getValue(Book.class);
                        if (book != null) {
                            issuedBook.add(book);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Ошибка: " + databaseError.getMessage());
            }
        });
        return map;
    }


    public static Bitmap createQRCode(Book b, String userId){
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(b.getId() + "/" + userId, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


}
