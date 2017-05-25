package com.example.oguz.whatsaround;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
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

import static android.content.Context.LOCATION_SERVICE;


public class HizmetlerFragment extends Fragment implements LocationListener {

    DatabaseReference mDatabase;
    RadioGroup rg;
    Button submit;
    TextView tvSeekBar;
    SeekBar seekBar;
    ProgressDialog progressDialog;
    LocationManager locationManager;
    Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hizmetler, container, false);

        mContext = getContext();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Bağlanıyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        rg = (RadioGroup) v.findViewById(R.id.radio_group);
        submit = (Button) v.findViewById(R.id.btnOk);
        tvSeekBar = (TextView) v.findViewById(R.id.tvSeekBar);
        seekBar = (SeekBar) v.findViewById(R.id.seekBar);

        tvSeekBar.setText("Mesafe : " + seekBar.getProgress() + " km");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                try {
                    JSONArray hizmetler = new JSONArray(dataSnapshot.child("Hizmetler").getValue().toString());
                    createRadioButton(hizmetler);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("hizmetler", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeekBar.setText("Mesafe : " + progress + " km");
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

        for (int i = 0; i < array.length(); i++) {
            rb[i] = new RadioButton(getContext());
            rb[i].setId(i);
            if (i == 0) rb[i].setSelected(true);
            rg.addView(rb[i]); //the RadioButtons are added to the radioGroup instead of the layout
            rb[i].setText(array.getString(i));
        }
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getContext(),rg.getCheckedRadioButtonId()+"",Toast.LENGTH_SHORT).show();
                Fragment fr = new CompanyListFragment();
                Bundle bundle = new Bundle();
                if (rg.getCheckedRadioButtonId() < 0) {
                    Toast.makeText(getContext(), "Lütfen bir hizmet seçiniz!", Toast.LENGTH_SHORT).show();
                } else {
                    bundle.putInt("service_id", rg.getCheckedRadioButtonId());


                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                        Criteria criteria = new Criteria();
                        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                        String provider = locationManager.getBestProvider(criteria, false);

                        Location location = getLocation();
                        if (location != null) {
                            double lat = (location.getLatitude());
                            double lng = (location.getLongitude());

                            bundle.putDouble("lat", lat);
                            bundle.putDouble("lng", lng);
                            bundle.putDouble("distance", seekBar.getProgress());

                            fr.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fr).addToBackStack("user").commit();
                        } else {
                            Toast.makeText(getContext(), "Konumunuz alınamadı! Lütfen gps'i aktif edin ve tekrar deneyin.", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            }
        });
    }

    public Location getLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {

            Location location = null;
            Double latitude;
            Double longitude;
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                1000,
                                1, HizmetlerFragment.this);
                        Log.d("Network", "Network Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                     1, 1, this);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
