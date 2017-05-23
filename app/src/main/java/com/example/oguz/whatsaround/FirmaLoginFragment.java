package com.example.oguz.whatsaround;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by oguz on 23.05.2017.
 */

public class FirmaLoginFragment extends Fragment {

    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_firma_login_first,container,false);

        mAuth = FirebaseAuth.getInstance();

        Button btnOk = (Button) v.findViewById(R.id.btnGiris);
        final EditText txtEmail = (EditText) v.findViewById(R.id.txtEmail);
        final EditText txtPass = (EditText) v.findViewById(R.id.txtPassword);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String pass = txtPass.getText().toString();

                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(getActivity(),new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        
                    }
                });

            }
        });

        return v;
    }
}
