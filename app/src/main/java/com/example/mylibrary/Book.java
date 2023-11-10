package com.example.mylibrary;

public class Book {
    private final int id;
    private final String name;
    private final String author;
    private final String description;
    private final String genres;
    private String status;
    private String date;

    public Book(){
        this.id = 0;
        this.name = "";
        this.author = "";
        this.description = "";
        this.genres = "";
        this.status = "";
        this.date = "";
    }
    public Book(int id, String name, String author, String description, String genres, String status, String date){
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.genres = genres;
        this.status = status;
        this.date = date;
    }
    public int getId(){return id;}
    public String getName(){return name;}
    public String getAuthor(){return author;}
    public String getDescription(){return description;}
    public String getGenres(){return genres;}
    public String getStatus() {return status;}
    public String getDate() {return date;}

    public void setDate(String d) {date=d;}
    public void setStatus(String s) {status=s;}

}