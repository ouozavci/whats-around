package com.example.oguz.whatsaround;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by oguz on 24.05.2017.
 */

public class CompanyListFragment extends Fragment {

    ListView listView;
    DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_company_list, container, false);
        final int service_id = getArguments().getInt("service_id");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    JSONObject obj = new JSONObject(dataSnapshot.child("company_list").child(service_id + "").getValue().toString());
                    Iterator<String> keys = obj.keys();
                    JSONArray companiesJson = new JSONArray();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        companiesJson.put(key);
                    }
                   //JSONArray companiesJson = new JSONArray(dataSnapshot.child("company_list").child(service_id + "").getValue().toString());

                            List<Company> companies = new ArrayList<Company>();
                    for (int i = 0; i < companiesJson.length(); i++) {
                        JSONObject compJson = new JSONObject(dataSnapshot.child("companies").child(companiesJson.getString(i)).getValue().toString().replaceAll(" ",""));
                        Company company =
                                new Company(compJson.getString("name"),
                                        compJson.getString("phone"),
                                        compJson.getString("email"), compJson.getInt("serviceId"));

                        companies.add(company);
                    }
                    CompanyListViewAdapter adapter = new CompanyListViewAdapter(getContext(), companies);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("json",e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView = (ListView) v.findViewById(R.id.company_listView);
        return v;
    }
}