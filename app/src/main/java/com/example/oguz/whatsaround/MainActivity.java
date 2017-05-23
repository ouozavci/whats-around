package com.example.oguz.whatsaround;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginFirstFragment fr = new LoginFirstFragment();
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,fr).addToBackStack("login_fragment").commit();

    }
}
