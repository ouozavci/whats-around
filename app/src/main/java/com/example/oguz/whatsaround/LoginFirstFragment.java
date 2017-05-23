package com.example.oguz.whatsaround;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by oguz on 23.05.2017.
 */

public class LoginFirstFragment extends Fragment {

    Button btnLogin,btnSignup,btnComp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_first,container,false);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnSignup = (Button) v.findViewById(R.id.btnSingup);
        btnComp = (Button) v.findViewById(R.id.btnComp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fr = new LoginFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,fr).addToBackStack("login").commit();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fr = new SignupFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,fr).addToBackStack("login").commit();
            }
        });

        btnComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firma ekranını çağır.
            }
        });
        return v;
    }
}
