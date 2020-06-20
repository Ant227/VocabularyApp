package com.example.vocabularyapp;

public class Book {
    public String bookname, wordcount,author, bookpic;

    public Book(){
        //needed
    }

    public Book(String bookname, String wordcount, String daycount, String bookpic) {
        this.bookname = bookname;
        this.wordcount = wordcount;
        this.author = daycount;
        this.bookpic = bookpic;


    }



    public String getWordcount() {
        return wordcount;
    }

    public void setWordcount(String wordcount) {
        this.wordcount = wordcount;
    }

    public String getAuthor() { return author; }

    public void setAuthor(String daycount) {
        this.author = daycount;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getBookpic() {
        return bookpic;
    }

    public void setBookpic(String bookpic) {
        this.bookpic = bookpic;
    }
}
