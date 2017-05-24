package com.example.oguz.whatsaround;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by oguz on 23.05.2017.
 */

public class FirmaActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firma);

        mAuth = FirebaseAuth.getInstance();

        Fragment fr = new FirmaFragment();
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,fr).addToBackStack("firma").commit();

        Button btnSignout = (Button) findViewById(R.id.btnSignout);
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(FirmaActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

    }
}
