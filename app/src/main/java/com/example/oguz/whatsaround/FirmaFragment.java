package com.example.oguz.whatsaround;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
/**
 * Created by oguz on 23.05.2017.
 */

public class FirmaFragment extends Fragment {

    DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_firma,container,false);
        final String comp_uid = getActivity().getIntent().getStringExtra("comp");
        final int service_id = getActivity().getIntent().getIntExtra("service",-1);

        final TextView textView = (TextView) v.findViewById(R.id.txtFirmaName);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("companies").child(service_id+"").child(comp_uid).child("name").getValue().toString();
                    textView.setText(name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }
}
