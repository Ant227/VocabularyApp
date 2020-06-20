package com.example.vocabularyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.github.barteksc.pdfviewer.PDFView;

public class ReadBookActivity extends AppCompatActivity {

    private PDFView pdfView;
    private ProgressBar progressBar;
    private String pdfUrl;
    private  PdfStream pdfStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        pdfUrl = getIntent().getExtras().get("pdf").toString();

        pdfView = findViewById(R.id.read_book_pdfView);
        progressBar = findViewById(R.id.read_book_progress_circle);

        pdfStream  = new PdfStream(pdfView,progressBar);
        pdfStream.execute(pdfUrl);


    }
}
