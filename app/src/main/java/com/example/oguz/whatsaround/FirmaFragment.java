package com.example.oguz.whatsaround;

import android.app.ProgressDialog;
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
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_firma,container,false);
        final String uid = getActivity().getIntent().getStringExtra("uid");

        if(uid==null) {
            Toast.makeText(getContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
            return null;
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Bağlanıyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final TextView textView = (TextView) v.findViewById(R.id.txtFirmaName);
        final TextView tvPhone = (TextView) v.findViewById(R.id.tvPhone);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if(dataSnapshot.child("companies").child(uid).exists()){
                    String name = dataSnapshot.child("companies").child(uid).child("name").getValue().toString();
                    String email = dataSnapshot.child("companies").child(uid).child("email").getValue().toString();
                    String phone = dataSnapshot.child("companies").child(uid).child("phone").getValue().toString();
                    String service_id = dataSnapshot.child("companies").child(uid).child("serviceId").getValue().toString();

                    String latlng = dataSnapshot.child("company_list").child(service_id).child(uid).getValue().toString();

                    textView.setText(name.replaceAll("@"," "));
                    tvPhone.setText(phone);

                    String strLat = latlng.split("-")[0];

                    Double lat = Double.valueOf(latlng.split("-")[0]);
                    Double lng = Double.valueOf(latlng.split("-")[1]);

                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat",lat);
                    bundle.putDouble("lng",lng);
                    bundle.putString("name",name);

                    Fragment fr = new MapFragment();
                    fr.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.map_container,fr).commit();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }
}
