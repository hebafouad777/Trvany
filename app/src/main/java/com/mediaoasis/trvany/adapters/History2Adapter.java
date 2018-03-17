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
import com.mediaoasis.trvany.activities.provider.RequestDetailsActivity;
import com.mediaoasis.trvany.models.Order;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ASUS on 03/05/2016.
 */
public class History2Adapter extends RecyclerView.Adapter<History2Adapter.MyViewHolder> {

    Context context;
    int country, city;
    private List<Order> OrdersList;

    public History2Adapter(Context con, List<Order> list) {
        context = con;
        this.OrdersList = list;
        this.city = city;
        this.country = country;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Order order = OrdersList.get(position);
        holder.name.setText(order.getUserName());
        holder.title.setText(order.getPropertyTitle());

//        if (!order.getPickupName().equals(""))
//            holder.location.setText(order.getPickupName());
//        else if (!order.getPickupAddress().equals(""))
//            holder.location.setText(order.getPickupAddress());
//        else
//            holder.location.setText(order.getPickupLatitude() + "," + order.getPickupLongitude());

        holder.date.setText(order.getDate());
        holder.time.setText(order.getTime());


        if (order.getStatus().equals("edited by user")) {
            holder.status.setText(R.string.waiting_your_approval);
            holder.status.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (order.getStatus().equals("edited by broker")) {
            holder.status.setText(context.getResources().getString(R.string.pending_approval));
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
            holder.status.setText(R.string.canceled_by_user);
            holder.status.setTextColor(context.getResources().getColor(R.color.gray_txt_body));
        } else if (order.getStatus().equals("canceled by broker")) {
            holder.status.setText(R.string.you_canceled_order);
            holder.status.setTextColor(context.getResources().getColor(R.color.gray_txt_body));
        } else {
            holder.status.setText(order.getStatus());
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        }


        holder.linearLayout.setTag(position);

        final Animation animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);

        Picasso.with(context).load(order.getUserImage()).fit().into(holder.Img);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Intent intent = new Intent(context, RequestDetailsActivity.class);
                intent.putExtra("order", OrdersList.get((int) holder.linearLayout.getTag()));
                intent.putExtra("isHistory", true);
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
        private TextView name, date, time, status, title;
        private ImageView Img;

        public MyViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearItemOrder);
            name = (TextView) view.findViewById(R.id.itemAppointmentClient);
//            location = (TextView) view.findViewById(R.id.itemAppointmentLocation);
            date = (TextView) view.findViewById(R.id.itemAppointmentDate);
            time = (TextView) view.findViewById(R.id.itemAppointmentTime);
            status = (TextView) view.findViewById(R.id.itemAppointmentStatus);
            title = (TextView) view.findViewById(R.id.itemAppointmentTitle);

            Img = (CircleImageView) view.findViewById(R.id.imgItemAppointment);
        }
    }
}
