package com.example.savushkin_practice_no2.Data.Util;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class
Element {
    public String title;
    public String value = null;
    public ArrayList<Element> innerElements = new ArrayList<>();

    public Element(String title, String value){
        this.title = title;
        this.value = value;
    }
    public  Element(String title){
        this.title = title;
    }

    public Element(@NonNull Element elemE) {
        this.title = elemE.title;
        this.value = elemE.value;
        if(elemE.innerElements != null) {
            this.innerElements.addAll(elemE.innerElements);
        }
    }
}
