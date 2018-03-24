package com.mediaoasis.trvany.fragments.provider;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.provider.Main2Activity;
import com.mediaoasis.trvany.adapters.AppointmentsAdapter;
import com.mediaoasis.trvany.models.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nasr on 1/9/2017.
 */

public class AppointmentsFragment extends Fragment {

    AppointmentsAdapter appointmentsAdapter;
    RecyclerView orders_rv;
    LinearLayoutManager mLayoutManager;
    TextView numOfAppointments_tv;
    ProgressBar progressBar;

    List<Order> ActiveOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar = (ProgressBar) getActivity().findViewById(R.id.progBar);
        numOfAppointments_tv = (TextView) getActivity().findViewById(R.id.txtNumOfAppointments);
        orders_rv = (RecyclerView) getActivity().findViewById(R.id.recyclerViewOrders);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
       // mLayoutManager.setReverseLayout(true);
        orders_rv.setLayoutManager(mLayoutManager);

        ActiveOrders = new ArrayList<>();

        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (Main2Activity.isOrderLoaded) {
                    ActiveOrders.clear();

                    for (int i = 0; i < Main2Activity.AllOrders.size(); i++) {
                        if (Main2Activity.AllOrders.get(i).getStatus().equals("edited by broker")
                                || Main2Activity.AllOrders.get(i).getStatus().equals("edited by user")
                                || Main2Activity.AllOrders.get(i).getStatus().equals("approved")
                                || Main2Activity.AllOrders.get(i).getStatus().equals("on request")) {
                            ActiveOrders.add(Main2Activity.AllOrders.get(i));
                        }
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    appointmentsAdapter = new AppointmentsAdapter(getActivity(), ActiveOrders);
                    orders_rv.setAdapter(appointmentsAdapter);
                    numOfAppointments_tv.setText(ActiveOrders.size() + " " + getString(R.string.requests_found));
                } else {
                    this.start();
                }
            }
        }.start();

//        DatabaseReference ordersRef = firebaseDatabase.getReference().child("Brokers")
//                .child(firebaseUser.getUid()).child("Orders");
//        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//                Order order;
//                for (DataSnapshot child : children) {
//                    order = child.getValue(Order.class);
//                    order.setOrderID(child.getKey());
//                    AllOrders.add(order);
//                }
//                appointmentsAdapter = new AppointmentsAdapter(getActivity(), AllOrders);
//                orders_rv.setAdapter(appointmentsAdapter);
//                numOfAppointments_tv.setText(AllOrders.size() + " Requests Found!");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
//        if (Main2Activity.isOrderLoaded)
//        appointmentsAdapter.notifyDataSetChanged();
    }
}
