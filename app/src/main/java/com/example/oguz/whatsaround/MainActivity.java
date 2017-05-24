package com.example.oguz.whatsaround;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(final FirebaseUser user){
        if(user == null){
            LoginFirstFragment fr = new LoginFirstFragment();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,fr).addToBackStack("login").commit();
        }
        else {
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot userRef = dataSnapshot.child("users").child(user.getUid());
                    DataSnapshot compRef = dataSnapshot.child("companies").child(user.getUid());
                    if(userRef.exists()){
                        Intent i = new Intent(MainActivity.this, UserActivity.class);
                        i.putExtra("uid",user.getUid().toString());
                        startActivity(i);
                    }
                    else if(compRef.exists()){
                        Intent i = new Intent(MainActivity.this, FirmaActivity.class);
                        i.putExtra("uid",user.getUid().toString());
                        startActivity(i);
                    }else{
                        Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

}
