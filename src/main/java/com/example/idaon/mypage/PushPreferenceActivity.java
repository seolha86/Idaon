package com.example.idaon.mypage;

import android.os.Bundle;
import androidx.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;

import com.example.idaon.R;

public class PushPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.push);
    }
}