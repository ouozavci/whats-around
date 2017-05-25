package com.example.oguz.whatsaround;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class HizmetlerFragment extends Fragment {

    DatabaseReference mDatabase;
    RadioGroup rg;
    Button submit;
    TextView tvSeekBar;
    SeekBar seekBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hizmetler, container, false);

        rg = (RadioGroup) v.findViewById(R.id.radio_group);
        submit = (Button) v.findViewById(R.id.btnOk);
        tvSeekBar = (TextView) v.findViewById(R.id.tvSeekBar);
        seekBar = (SeekBar) v.findViewById(R.id.seekBar);

        tvSeekBar.setText("Mesafe : "+seekBar.getProgress()+" km");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    JSONArray hizmetler = new JSONArray(dataSnapshot.child("Hizmetler").getValue().toString());
                    createRadioButton(hizmetler);
                }catch (JSONException e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("hizmetler",e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeekBar.setText("Mesafe : "+progress+" km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tvSeekBar.setTextSize(24);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tvSeekBar.setTextSize(18);
            }
        });

        return v;
    }
    private void createRadioButton(final JSONArray array) throws JSONException {
        final RadioButton[] rb = new RadioButton[array.length()];

        for(int i=0; i<array.length(); i++){
            rb[i]  = new RadioButton(getContext());
            rb[i].setId(i);
            rg.addView(rb[i]); //the RadioButtons are added to the radioGroup instead of the layout
            rb[i].setText(array.getString(i));
        }
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               //Toast.makeText(getContext(),rg.getCheckedRadioButtonId()+"",Toast.LENGTH_SHORT).show();
                Fragment fr = new CompanyListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("service_id",rg.getCheckedRadioButtonId());


                if(ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)== PermissionChecker.PERMISSION_GRANTED) {
                    Criteria criteria = new Criteria();
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    String provider = locationManager.getBestProvider(criteria, false);
                    Location location = locationManager.getLastKnownLocation(provider);
                    double lat =  (location.getLatitude());
                    double lng =  (location.getLongitude());

                    bundle.putDouble("lat",lat);
                    bundle.putDouble("lng",lng);
                    bundle.putDouble("distance",seekBar.getProgress());
                }

                fr.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,fr).addToBackStack("user").commit();
            }
        });
    }
}
