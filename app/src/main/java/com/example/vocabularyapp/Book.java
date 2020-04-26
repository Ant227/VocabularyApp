package com.example.vocabularyapp;

public class Book {
    public String bookname, wordcount,daycount, bookpic;

    public Book(){
        //needed
    }

    public Book(String book_name, String wordcount, String daycount, String bookpic) {
        this.bookname = bookname;
        this.wordcount = wordcount;
        this.daycount = daycount;
        this.bookpic = bookpic;


    }



    public String getWordcount() {
        return wordcount;
    }

    public void setWordcount(String wordcount) {
        this.wordcount = wordcount;
    }

    public String getDaycount() {
        return daycount;
    }

    public void setDaycount(String daycount) {
        this.daycount = daycount;
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
