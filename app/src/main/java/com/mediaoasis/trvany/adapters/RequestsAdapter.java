package com.mediaoasis.trvany.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.models.Order;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ASUS on 03/05/2016.
 */
public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyViewHolder> {

    Context context;
    int country, city;
    private List<Order> OrdersList;

    public RequestsAdapter(Context con, List<Order> list) {
        context = con;
        this.OrdersList = list;
        this.city = city;
        this.country = country;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Order order = OrdersList.get(position);
        holder.name.setText(order.getUserName());
        holder.time.setText(order.getTime());
        holder.date.setText(order.getDate());
        holder.status.setText(order.getStatus());
        Picasso.with(context).load(order.getUserImage()).fit().into(holder.Img);

    }

    @Override
    public int getItemCount() {
        return OrdersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        private TextView name, status, time, date;
        private CircleImageView Img;

        public MyViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearItemOrder);
            name = (TextView) view.findViewById(R.id.itemOrderName);
//            location = (TextView) view.findViewById(R.id.itemAppointmentLocation);
            date = (TextView) view.findViewById(R.id.itemOrderDate);
            time = (TextView) view.findViewById(R.id.itemOrderTime);
            status = (TextView) view.findViewById(R.id.itemOrderStatus);
//            title = (TextView) view.findViewById(R.id.itemAppointmentTitle);

            Img = (CircleImageView) view.findViewById(R.id.itemOrderBrokerImg);
        }
    }
}
