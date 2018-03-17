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
import com.mediaoasis.trvany.adapters.ConversationAdapter;

/**
 * Created by Nasr on 1/9/2017.
 */

public class MessagesFragment extends Fragment {

    ProgressBar progressBar;
    ConversationAdapter conversationAdapter;
    TextView num_tv;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressChat);
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerViewConfs);
        num_tv = (TextView) getActivity().findViewById(R.id.txtNumOfAppointments);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);


        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                if (MainActivity.isChatLoaded) {
                    progressBar.setVisibility(View.INVISIBLE);
                    conversationAdapter = new ConversationAdapter(getActivity(), MainActivity.AllConversations, 0);
                    mRecyclerView.setAdapter(conversationAdapter);

                    if (MainActivity.AllConversations.size() > 0)
                        num_tv.setText(MainActivity.AllConversations.size() + " " + getString(R.string.conversations_found));
                    else
                        num_tv.setText(getString(R.string.no_conversations));

                } else
                    this.start();
            }
        }.start();

    }
}
