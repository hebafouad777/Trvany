package com.mediaoasis.trvany.fragments.user;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.MainActivity;
import com.mediaoasis.trvany.adapters.OrdersAdapter;
import com.mediaoasis.trvany.models.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nasr on 1/9/2017.
 */

public class OrdersFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    List<Order> ActiveOrders;
    OrdersAdapter ordersAdapter;
    RecyclerView orders_rv;
    LinearLayoutManager mLayoutManager;
    TextView numOfAppointments_tv;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActiveOrders = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = (ProgressBar) getActivity().findViewById(R.id.progBar);
        numOfAppointments_tv = (TextView) getActivity().findViewById(R.id.txtNumOfAppointments);
        orders_rv = (RecyclerView) getActivity().findViewById(R.id.recyclerViewOrders);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orders_rv.setLayoutManager(mLayoutManager);



        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (MainActivity.isOrderLoaded) {
                    ActiveOrders.clear();

                    for (int i = 0; i < MainActivity.AllOrders.size(); i++) {
                        if (MainActivity.AllOrders.get(i).getStatus().equals("edited by broker")
                                || MainActivity.AllOrders.get(i).getStatus().equals("edited by user")
                                || MainActivity.AllOrders.get(i).getStatus().equals("approved")
                                || MainActivity.AllOrders.get(i).getStatus().equals("on request")) {
                            ActiveOrders.add(MainActivity.AllOrders.get(i));
                        }
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    ordersAdapter = new OrdersAdapter(getActivity(), ActiveOrders, false);
                    orders_rv.setAdapter(ordersAdapter);
                    numOfAppointments_tv.setText(ActiveOrders.size() + " " + getString(R.string.requests_found));
                } else {
                    this.start();
                }
            }
        }.start();

//
//        DatabaseReference ordersRef = firebaseDatabase.getReference().child("User").child(firebaseUser.getUid()).child("Orders");
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
//                ordersAdapter = new OrdersAdapter(getActivity(), AllOrders, false);
//                orders_rv.setAdapter(ordersAdapter);
//                numOfAppointments_tv.setText(AllOrders.size() + " Appointments Found!");
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
//        if (MainActivity.isOrderLoaded)
//            ordersAdapter.notifyDataSetChanged();
    }
}
