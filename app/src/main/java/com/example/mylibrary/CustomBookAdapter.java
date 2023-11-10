package com.example.mylibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CustomBookAdapter extends ArrayAdapter {

    private final List<Book> arr;
    private Context mContext;

    public CustomBookAdapter(Context context, List<Book> arr) {
        super(context, R.layout.custom_element_book, arr);
        this.arr = arr;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_element_book, parent, false);
        }
        TextView text_name = view.findViewById(R.id.text_name);
        TextView text_author = view.findViewById(R.id.text_author);
        LinearLayout linear = view.findViewById(R.id.linear);
        text_name.setText(arr.get(position).getName());
        text_author.setText(arr.get(position).getAuthor());

        return view;
    }
}