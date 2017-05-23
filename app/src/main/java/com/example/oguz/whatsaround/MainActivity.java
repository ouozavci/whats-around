package com.example.oguz.whatsaround;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user){
        if(user == null){
            LoginFirstFragment fr = new LoginFirstFragment();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,fr).addToBackStack("login").commit();
        }
        else {
            Intent i = new Intent(MainActivity.this, UserActivity.class);
            i.putExtra("email",user.getEmail());
            startActivity(i);
        }
    }

}
