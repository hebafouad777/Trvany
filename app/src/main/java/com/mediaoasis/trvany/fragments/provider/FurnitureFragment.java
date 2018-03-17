package com.mediaoasis.trvany.fragments.provider;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.provider.AddServiceActivity;
import com.mediaoasis.trvany.activities.provider.Main2Activity;
import com.mediaoasis.trvany.adapters.ServicesAdapter;
import com.mediaoasis.trvany.models.Furniture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nasr on 1/9/2017.
 */

public class FurnitureFragment extends Fragment {

    TextView propertiesCount_tv;
    FloatingActionButton floatingActionButton;
    List<Furniture> filteredFurnitures;
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ServicesAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_properties, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        filteredFurnitures = new ArrayList<>();

        propertiesCount_tv = (TextView) getActivity().findViewById(R.id.textPropertiesCount);
        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.FABaddProperty);
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBarProps);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerViewBrokerProperties);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ServicesAdapter(getActivity(), filteredFurnitures, true);
        mRecyclerView.setAdapter(mAdapter);


        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference propertiesRef = firebaseDatabase.getReference().getRoot().child("Providers")
                .child(Main2Activity.currentProvider.getBrokerID()).child("Furniture");
        propertiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                filteredFurnitures.clear();
                Furniture furniture;
                for (DataSnapshot child : children) {
                    furniture = child.getValue(Furniture.class);
                    furniture.setPropertyID(child.getKey());
                    filteredFurnitures.add(furniture);
                }
                progressBar.setVisibility(View.GONE);
                mAdapter = new ServicesAdapter(getActivity(), filteredFurnitures, true);
                mRecyclerView.setAdapter(mAdapter);
                propertiesCount_tv.setText(filteredFurnitures.size() + " " + getString(R.string.properties_found));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddServiceActivity.class);
                intent.putExtra("isNew", true);
                startActivity(intent);
            }
        });
    }
}
