package com.example.moviewbrowserort.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.moviewbrowserort.R;
import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    private TextView txtDetailTitle , txt_releaseDate , txt_rating , txt_overview;
    private LinearLayout ll_imgBack;
    String date,rating,path,overview ,title , id ;
    private ImageView img_poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().hide();

        initValuesForDetail();

        if(!id.equals("null")){
            txt_overview.setText("Synopsis: "+ " " +overview);
            txt_releaseDate.setText(date);
            txt_rating.setText(rating);
            txtDetailTitle.setText("Movie Name: "+ " " +title);

            Picasso.get().load("https://image.tmdb.org/t/p/w500/" + path).fit().placeholder(R.drawable.pngwing).into(img_poster);

        }

    }

    private void initValuesForDetail(){

        Intent intent=getIntent();
        date=intent.getStringExtra("date");
        rating=intent.getStringExtra("rating");
        path=intent.getStringExtra("imgPath");
        overview=intent.getStringExtra("synopsis");
        title=intent.getStringExtra("title");
        id=intent.getStringExtra("id");

        txt_overview = findViewById(R.id.txt_overview);
        img_poster = findViewById(R.id.img_poster);
        txt_releaseDate = findViewById(R.id.txt_releaseDate);
        txt_rating = findViewById(R.id.txt_rating);
        txtDetailTitle = findViewById(R.id.txtDetailTitle);
        ll_imgBack = findViewById(R.id.ll_imgBack);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
