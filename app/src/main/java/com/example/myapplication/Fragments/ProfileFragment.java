package com.example.myapplication.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private TextView tv_name,tv_email;
    private SharedPreferences pref;
    private Button btn_change_password,btn_logout;
    private ExtendedFloatingActionButton btn_photo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pref = getActivity().getPreferences(0);
        tv_name.setText(getString(R.string.welcome) + ": " +pref.getString(Constants.NAME,""));
        tv_email.setText(pref.getString(Constants.EMAIL,""));

    }

    private void initViews(View view) {
        tv_name = view.findViewById(R.id.tv_name);
        tv_email = view.findViewById(R.id.tv_email);
        btn_change_password = view.findViewById(R.id.btn_chg_password);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_change_password.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_photo = view.findViewById(R.id.photo_button);
        btn_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_logout) {
            logout();
        }
        else if (v.getId() == R.id.photo_button){
            goToPhoto();
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.IS_LOGGED_IN,false);
        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.NAME,"");
        editor.putString(Constants.UNIQUE_ID,"");
        editor.apply();
        goToLogin();
    }

    private void goToPhoto(){
        PhotoFragment photo = new PhotoFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, photo);
        ft.commit();
    }
    private void goToLogin(){
        LoginFragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,login);
        ft.commit();
    }

}
