package com.example.oguz.whatsaround;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by oguz on 23.05.2017.
 */

public class LoginFragment extends Fragment {
    Button btnOk;
    EditText txtEmail,txtPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login,container,false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Bağlanıyor...");
        progressDialog.setCancelable(false);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        btnOk = (Button) v.findViewById(R.id.btnOk);
        txtEmail = (EditText) v.findViewById(R.id.txtEmail);
        txtPassword = (EditText) v.findViewById(R.id.txtPassword);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String email = txtEmail.getText().toString();
                String pass = txtPassword.getText().toString();

                if(email.equals("") || pass.equals("")){
                    Toast.makeText(getContext(),"Bütün alanları doldurun!",Toast.LENGTH_SHORT).show();
                }else{
                    signin(email,pass);
                }
            }
        });
        return v;
    }
    public void signin(String email,String pass){
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Log.d("login", "signInWithEmail:success");
                    final FirebaseUser user = mAuth.getCurrentUser();
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                           if(!dataSnapshot.child(user.getUid()).exists()){
                                Toast.makeText(getContext(),"Kullanıcı bulunamadı! Firma girişi yapmayı deneyin.",Toast.LENGTH_SHORT).show();
                            }else{
                               updateUI(user);
                           }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                   // updateUI(user);
                }
                else{
                    Log.w("login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(getContext(), task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
    public void updateUI(FirebaseUser user){
        if(user == null){
            LoginFirstFragment fr = new LoginFirstFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,fr).addToBackStack("login").commit();
        }
        else {
            //Kullanıcıya özel sayfayı yükle
            Intent i = new Intent(getActivity(), UserActivity.class);
            i.putExtra("email",user.getEmail());
            startActivity(i);
        }
    }
}
