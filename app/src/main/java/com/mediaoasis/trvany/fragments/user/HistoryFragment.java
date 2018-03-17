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

import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.MainActivity;
import com.mediaoasis.trvany.adapters.HistoryAdapter;
import com.mediaoasis.trvany.models.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nasr on 1/9/2017.
 */

public class HistoryFragment extends Fragment {

    HistoryAdapter historyAdapter;
    RecyclerView orders_rv;
    LinearLayoutManager mLayoutManager;
    TextView numOfAppointments_tv;
    ProgressBar progressBar;

    List<Order> InActiveOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
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
        mLayoutManager.setReverseLayout(true);
        orders_rv.setLayoutManager(mLayoutManager);

        InActiveOrders = new ArrayList<>();

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (MainActivity.isOrderLoaded) {
                    InActiveOrders.clear();

                    for (int i = 0; i < MainActivity.AllOrders.size(); i++) {
                        if (MainActivity.AllOrders.get(i).getStatus().contains("canceled")
                                || MainActivity.AllOrders.get(i).getStatus().equals("done")) {
                            InActiveOrders.add(MainActivity.AllOrders.get(i));
                        }
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    historyAdapter = new HistoryAdapter(getActivity(), InActiveOrders);
                    orders_rv.setAdapter(historyAdapter);
                    if (InActiveOrders.size() == 0)
                        numOfAppointments_tv.setText(getString(R.string.no_history_yet));
                    else
                        numOfAppointments_tv.setText(InActiveOrders.size() + " " + getString(R.string.history_found));
                } else {
                    this.start();
                }
            }
        }.start();

    }
}
