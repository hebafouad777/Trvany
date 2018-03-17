package com.mediaoasis.trvany.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.OrderDetailsActivity;
import com.mediaoasis.trvany.models.Order;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ASUS on 03/05/2016.
 */
public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    Context context;
    boolean isHistory = false;
    private List<Order> OrdersList;

    public OrdersAdapter(Context con, List<Order> list, boolean isHis) {
        context = con;
        this.OrdersList = list;
        isHistory = isHis;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Order order = OrdersList.get(position);
        holder.name.setText(order.getBrokerName());
        holder.title.setText(order.getBrokerTitle());

        if (!order.getPickupName().equals(""))
            holder.location.setText(order.getPickupName());
        else if (!order.getPickupAddress().equals(""))
            holder.location.setText(order.getPickupAddress());
        else
            holder.location.setText(order.getPickupLatitude() + "," + order.getPickupLongitude());

        holder.date.setText(order.getDate());
        holder.time.setText(order.getTime());

//        if (order.getStatus().equals("edited by user") || order.getStatus().equals("edited by broker")) {
//            holder.status.setText(context.getString(R.string.pending_approval));
//            holder.status.setTextColor(context.getResources().getColor(R.color.orange));
//        } else {
//            holder.status.setText(order.getStatus());
//        }

        if (order.getStatus().equals("edited by user")) {
            holder.status.setText(context.getResources().getString(R.string.pending_approval));
            holder.status.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (order.getStatus().equals("edited by broker")) {
            holder.status.setText(R.string.waiting_your_approval);
            holder.status.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (order.getStatus().equals("on request")) {
            holder.status.setText(context.getResources().getString(R.string.pending_approval));
            holder.status.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (order.getStatus().equals("approved")) {
            holder.status.setText(R.string.approved);
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        } else if (order.getStatus().equals("done")) {
            holder.status.setText(R.string.done);
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        } else if (order.getStatus().equals("canceled by user")) {
            holder.status.setText(R.string.you_canceled_order);
            holder.status.setTextColor(context.getResources().getColor(R.color.gray_txt_body));
        } else if (order.getStatus().equals("canceled by broker")) {
            holder.status.setText(R.string.canceled_by_broker);
            holder.status.setTextColor(context.getResources().getColor(R.color.gray_txt_body));
        } else {
            holder.status.setText(order.getStatus());
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        }

        holder.linearLayout.setTag(position);

        final Animation animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);

        Picasso.with(context).load(order.getBrokerImage()).fit().into(holder.Img);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("order", OrdersList.get((int) holder.linearLayout.getTag()));
                intent.putExtra("isHistory", isHistory);
                context.startActivity(intent);
//
            }
        });
    }

    @Override
    public int getItemCount() {
        return OrdersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        private TextView location, name, date, time, status, title;
        private ImageView Img;

        public MyViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearItemOrder);
            name = (TextView) view.findViewById(R.id.itemOrderName);
            location = (TextView) view.findViewById(R.id.itemOrderLocation);
            date = (TextView) view.findViewById(R.id.itemOrderDate);
            time = (TextView) view.findViewById(R.id.itemOrderTime);
            status = (TextView) view.findViewById(R.id.itemOrderStatus);
            title = (TextView) view.findViewById(R.id.itemOrderTitle);

            Img = (CircleImageView) view.findViewById(R.id.itemOrderBrokerImg);
        }
    }
}
