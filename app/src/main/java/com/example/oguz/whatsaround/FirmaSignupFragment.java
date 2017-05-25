package com.example.oguz.whatsaround;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oguz on 23.05.2017.
 */

public class FirmaSignupFragment extends Fragment {

    EditText txtName, txtPhone, txtEmail, txtPass, txtPass2;
    Spinner spinner;
    Button btnOk;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_firma_signup, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtName = (EditText) v.findViewById(R.id.txtFirmadı);
        txtPhone = (EditText) v.findViewById(R.id.txtTel);
        txtEmail = (EditText) v.findViewById(R.id.txtEmail);
        txtPass = (EditText) v.findViewById(R.id.txtSifre);
        txtPass2 = (EditText) v.findViewById(R.id.txtSifre2);
        spinner = (Spinner) v.findViewById(R.id.spinner);
        btnOk = (Button) v.findViewById(R.id.btnOk);

        final List<String> spinnerList = new ArrayList<>();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> result = new ArrayList<String>();
                try {
                    JSONArray hizmetler = new JSONArray(dataSnapshot.child("Hizmetler").getValue().toString());
                    for(int i = 0;i<hizmetler.length();i++){
                        spinnerList.add(hizmetler.get(i).toString());
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerList);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    }
                }catch (JSONException e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString();
                String phone = txtPhone.getText().toString();
                String email = txtEmail.getText().toString();
                String pass = txtPass.getText().toString();
                String pass2 = txtPass2.getText().toString();
                int serviceId = spinner.getSelectedItemPosition();

                if (name.equals("") || phone.equals("") || email.equals("") || pass.equals("") || pass2.equals("")) {
                    Toast.makeText(getContext(), "Tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                } else {
                    if(pass.equals(pass2)){
                        Company comp = new Company(name,phone,email,serviceId);
                        signup(email,pass,comp);
                    }else{
                        Toast.makeText(getContext(), "Şifreler uyuşmuyor!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return v;
    }
    public void signup(String email, String pass, final Company comp){
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signup", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            mDatabase.child("companies").child(user.getUid()).child("name").setValue(comp.getName());
                            mDatabase.child("companies").child(user.getUid()).child("phone").setValue(comp.getPhone());
                            mDatabase.child("companies").child(user.getUid()).child("email").setValue(comp.getEmail());
                            mDatabase.child("companies").child(user.getUid()).child("serviceId").setValue(comp.getServiceId());


                            double lat = getArguments().getDouble("lat");
                            double lng = getArguments().getDouble("lng");

                            mDatabase.child("company_list").child(comp.getServiceId()+"").child(user.getUid()).setValue(lat+"-"+lng);

                            updateUI(user,user.getUid(),comp.getServiceId());
                        } else {
                            Log.w("signup", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void updateUI(FirebaseUser user,String uid,int serviceId) {
        if (user == null) {
            LoginFirstFragment fr = new LoginFirstFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fr).addToBackStack("login").commit();
        } else {
            //Kullanıcıya özel sayfayı yükle
            Intent i = new Intent(getActivity(), FirmaActivity.class);
            i.putExtra("uid",user.getUid());
            startActivity(i);
        }
    }
}
