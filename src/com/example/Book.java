package com.example;

import java.io.Serializable;

public class Book implements Serializable {
    private static final long serialVersionUID = 1234;
    private String title;

    public Book(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public String printBook(){
        return "NAMA BUKUNYA INI" + this.title + "BGST";
    }
}
