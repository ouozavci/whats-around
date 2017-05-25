package com.example.oguz.whatsaround;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;

/**
 * Created by oguz on 23.05.2017.
 */

public class FirmaLoginFirstFragment extends Fragment {

    int PLACE_PICKER_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_firma_login,container,false);



        Button btnSignup = (Button) v.findViewById(R.id.btnSingup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try{
                    Intent i = builder.build(getActivity());
                    startActivityForResult(i, PLACE_PICKER_REQUEST);
                }catch (Exception e){
                    Log.e("maps",e.getMessage());
                }


            }
        });
        Button btnLogin = (Button) v.findViewById(R.id.btnGiris);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fr = new FirmaLoginFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container,fr).addToBackStack("login").commit();

            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(getActivity(), data);

                Bundle bundle = new Bundle();
                bundle.putDouble("lat",place.getLatLng().latitude);
                bundle.putDouble("lng",place.getLatLng().longitude);

                Fragment fr  = new FirmaSignupFragment();
                fr.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container,fr).addToBackStack("login").commit();
            }
        }
    }
}
