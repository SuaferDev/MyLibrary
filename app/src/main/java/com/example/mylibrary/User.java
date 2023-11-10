package com.example.mylibrary;

import java.util.ArrayList;
import java.util.List;

public class User {

    private final int id;
    private final String name;
    private final String surname;
    private final String login;
    private final String password;
    private final String date;
    private final int libraryCard;
    private final List<Integer> listBook;

    public User() {
        this.id = 0;
        this.name = "name";
        this.surname = "surname";
        this.login = "login";
        this.password = "password";
        this.date = "00/00";
        this.libraryCard = 1000000000;
        this.listBook = new ArrayList<>();
    }

    public User(int id, String name, String surname, String login, String password, String date, int libraryCard, List<Integer> listBook) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password = password;
        this.listBook = listBook;
        this.date = date;
        this.libraryCard = libraryCard;
    }
}
