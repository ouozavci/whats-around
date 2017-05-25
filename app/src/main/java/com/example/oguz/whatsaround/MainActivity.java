package com.example.oguz.whatsaround;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

    final int PERMISSION_LOC = 0;
    final int PERMISSION_CALL = 1;
    final int PERMISSION_SMS = 2;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Bağlanıyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        boolean permission = true;
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permission = false;
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS}, PERMISSION_LOC);
            }
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                permission = false;
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
            }
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                permission = false;
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SMS);
            }
        }
        if(permission) {

            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_LOC:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    finish();
                    startActivity(getIntent());
                }
                else {
                    Toast.makeText(getApplicationContext(),"Uygulamayı kullanmak için bütün izinleri sağlamalısınız!",Toast.LENGTH_SHORT).show();
                    this.finishAffinity();
                }
           //     break;
          //  case PERMISSION_CALL:
                if(grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    finish();
                    startActivity(getIntent());
                }
                else {
                    Toast.makeText(getApplicationContext(),"Uygulamayı kullanmak için bütün izinleri sağlamalısınız!",Toast.LENGTH_SHORT).show();
                    this.finishAffinity();
                }
           //     break;
          //  case PERMISSION_SMS:
                if(grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    finish();
                    startActivity(getIntent());
                }
                else {
                    Toast.makeText(getApplicationContext(),"Uygulamayı kullanmak için bütün izinleri sağlamalısınız!",Toast.LENGTH_SHORT).show();
                    this.finishAffinity();
                }
        }
        return;
    }
    public void updateUI(final FirebaseUser user){
        if(user == null){
            progressDialog.dismiss();
            LoginFirstFragment fr = new LoginFirstFragment();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,fr).commit();
        }
        else {
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    DataSnapshot userRef = dataSnapshot.child("users").child(user.getUid());
                    DataSnapshot compRef = dataSnapshot.child("companies").child(user.getUid());
                    if(userRef.exists()){
                        Intent i = new Intent(MainActivity.this, UserActivity.class);
                        i.putExtra("uid",user.getUid().toString());
                        startActivity(i);
                        finish();
                    }
                    else if(compRef.exists()){
                        Intent i = new Intent(MainActivity.this, FirmaActivity.class);
                        i.putExtra("uid",user.getUid().toString());
                        startActivity(i);
                        finish();
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
