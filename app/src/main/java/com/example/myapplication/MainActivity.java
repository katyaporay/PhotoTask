package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.Fragments.LoginFragment;
import com.example.myapplication.Fragments.PhotoFragment;
import com.example.myapplication.Fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private Toolbar toolbar;
    private FrameLayout fragmentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getPreferences(0);
        initFragment();
        toolbar = findViewById(R.id.toolbar);
        fragmentFrame = findViewById(R.id.fragment_frame);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_small);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuitem){
        if (menuitem.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    private void initFragment(){
        Fragment fragment;
        if(pref.getBoolean(Constants.IS_LOGGED_IN,false)){
            fragment = new ProfileFragment();
        }else {
            fragment = new LoginFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        fragmentFrame.getId();
        Fragment fr = getSupportFragmentManager().getFragments().get(0);
        if (fr.getClass() == (new PhotoFragment()).getClass()){
            Fragment fragment = new ProfileFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_frame,fragment);
            ft.commit();
        }
    }
}
