package com.example.jaylen.popularmovies;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.setTitle("Settings");
        if(savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .add(R.id.content,new PreferenceFagment())
                    .commit();
        }
    }
}
