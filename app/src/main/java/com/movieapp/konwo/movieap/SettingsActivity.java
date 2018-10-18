package com.movieapp.konwo.movieap;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

//    public static class SettingsFragment extends PreferenceFragment{
//        @Override
//        public void onCreate(final Bundle savedInstanceState){
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.preferences);
//        }
//    }

}
