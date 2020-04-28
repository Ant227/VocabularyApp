package com.example.vocabularyapp;

public class Vocabulary {
    public String word;

    public Vocabulary(){
        //needed for firebase
    }

    public Vocabulary(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
