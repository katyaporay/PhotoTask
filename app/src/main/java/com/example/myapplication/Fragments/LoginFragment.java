package com.example.myapplication.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.Constants;
import com.example.myapplication.Models.ServerRequest;
import com.example.myapplication.Models.ServerResponse;
import com.example.myapplication.Models.User;
import com.example.myapplication.R;
import com.example.myapplication.RequestInterface;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private Button btn_login;
    private EditText et_email,et_password;
    private TextView tv_register;
    private TextView goToResetPassword;
    private ProgressBar progress;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view){

        pref = getActivity().getPreferences(0);

        btn_login = view.findViewById(R.id.btn_login);
        tv_register = view.findViewById(R.id.tv_register);
        goToResetPassword = view.findViewById(R.id.goToResetPassword);
        et_email = view.findViewById(R.id.et_email);
        et_password = view.findViewById(R.id.et_password);

        progress = view.findViewById(R.id.progress);

        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        goToResetPassword.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.goToResetPassword:
                goToResetPassword();
                break;

            case R.id.tv_register:
                goToRegister();
                break;

            case R.id.btn_login:
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()) {

                    progress.setVisibility(View.VISIBLE);
                    loginProcess(email,password);

                } else {
                    Snackbar.make(getView(), R.string.fields_empty, Snackbar.LENGTH_LONG).show();
                }
                break;

        }
    }
    private void loginProcess(String email,String password){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.putString(Constants.EMAIL,resp.getUser().getEmail());
                    editor.putString(Constants.NAME,resp.getUser().getName());
                    editor.putString(Constants.UNIQUE_ID,resp.getUser().getUnique_id());
                    editor.apply();
                    goToProfile();

                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

            }
        });
    }

    private void goToRegister(){
        /*
        Показываем фрагмент для регистрации пользователя
*/

    }

    private void goToProfile(){
        Fragment profile = new ProfileFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,profile);
        ft.commit();
    }

    private void goToResetPassword(){
/*
             Показываем фрагмент для сброса пароля
  */
    }
}