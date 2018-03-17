package com.mediaoasis.trvany.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.ChatActivity;
import com.mediaoasis.trvany.models.Conversation;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ASUS on 03/05/2016.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    Context context;
    int isBroker = 0;
    private List<Conversation> conversationList;

    public ConversationAdapter(Context con, List<Conversation> list, int isB) {
        context = con;
        isBroker = isB;
        this.conversationList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Conversation conversation = conversationList.get(position);
        holder.linearLayout.setTag(position);
        final Animation animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);

        if (isBroker == 0) {
            holder.title.setText(conversation.getBrokerName());
            Picasso.with(context).load(conversation.getBrokerImage()).fit()
                    .placeholder(R.drawable.profile_placeholder).into(holder.img);
        } else if (isBroker == 1) {
            holder.title.setText(conversation.getUserName());
            Picasso.with(context).load(conversation.getUserImage()).fit()
                    .placeholder(R.drawable.profile_placeholder).into(holder.img);
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("confID", conversation.getConversationID());
                intent.putExtra("isBroker", isBroker);
                context.startActivity(intent);
//
            }
        });


    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        CardView linearLayout;
        LinearLayout linearLayout;
        private TextView title;
        private CircleImageView img;

        public MyViewHolder(View view) {
            super(view);
//            linearLayout = (CardView) view.findViewById(R.id.linearLayoutItemFood);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearItemConf);
            title = (TextView) view.findViewById(R.id.txtConvItemName);
            img = (CircleImageView) view.findViewById(R.id.imgItemConf);
        }
    }
}
