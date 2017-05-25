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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by oguz on 23.05.2017.
 */

public class SignupFragment extends Fragment {

    Button btnOk;
    EditText txtName, txtSurname, txtEmail, txtPassword, txtPassword2;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Bağlanıyor...");
        progressDialog.setCancelable(false);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnOk = (Button) v.findViewById(R.id.btnOk);
        txtName = (EditText) v.findViewById(R.id.txtName);
        txtSurname = (EditText) v.findViewById(R.id.txtSurname);
        txtEmail = (EditText) v.findViewById(R.id.txtEmail);
        txtPassword = (EditText) v.findViewById(R.id.txtPassword);
        txtPassword2 = (EditText) v.findViewById(R.id.txtPassword2);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name = txtName.getText().toString();
                String surname = txtSurname.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                String password2 = txtPassword2.getText().toString();

                if (name.equals("") || surname.equals("") || email.equals("") || password.equals("") || password2.equals("")) {
                    Toast.makeText(getContext(), "Bütün alanları doldurun!", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(password2)) {
                        signup(email, password, name, surname);
                    } else {
                        Toast.makeText(getContext(), "Şifreler birbiri ile uyuşmuyor!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return v;
    }

    public void signup(final String email, String pass, final String name, final String surname) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signup", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Set the user type
                        /*    mDatabase.child("user_type").child(user.getUid()).setValue("user");

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name + " " + surname)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("signup", "User profile updated.");
                                            }
                                        }
                                    });*/
                            mDatabase.child("users").child(user.getUid().toString()).child("name").setValue(name);
                            mDatabase.child("users").child(user.getUid().toString()).child("surname").setValue(surname);
                            mDatabase.child("users").child(user.getUid().toString()).child("email").setValue(email);
                            updateUI(user);
                        } else {
                            Log.w("signup", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user) {
        if (user == null) {
            LoginFirstFragment fr = new LoginFirstFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fr).addToBackStack("login").commit();
        } else {
            //Kullanıcıya özel sayfayı yükle
            Intent i = new Intent(getActivity(), UserActivity.class);
            i.putExtra("uid",user.getUid());
            startActivity(i);
        }
    }
}
