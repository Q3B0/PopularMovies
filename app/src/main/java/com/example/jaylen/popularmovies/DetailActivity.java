package com.example.jaylen.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        this.setTitle(intent.getStringExtra("TITLE"));
        ((TextView) findViewById(R.id.txttitle)).setText("Title:" + "\n" + intent.getStringExtra("TITLE") + "/" + intent.getStringExtra("O_TITLE"));
        ((TextView)findViewById(R.id.txtdate)).setText("Release Date:" + "\n" + intent.getStringExtra("DATE"));
        ((TextView)findViewById(R.id.txtrate)).setText("Vote average:" + "\n" + intent.getStringExtra("RATE"));
        Picasso.with(this).load(intent.getStringExtra("POSTER")).into((ImageView)findViewById(R.id.imgPoster));
        ((TextView)findViewById(R.id.txtOverview)).setText("Overview:" + "\n" + intent.getStringExtra("OVERVIEW"));

    }


}
