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
import com.mediaoasis.trvany.activities.user.ServiceDetailsActivity;
import com.mediaoasis.trvany.models.Furniture;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ASUS on 03/05/2016.
 */
public class ServicesByProviderAdapter extends RecyclerView.Adapter<ServicesByProviderAdapter.MyViewHolder> {

    Context context;
    int country, city;
    private List<Furniture> PropertiesList;

    public ServicesByProviderAdapter(Context con, List<Furniture> list) {
        context = con;
        this.PropertiesList = list;
        this.city = city;
        this.country = country;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_by_broker, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Furniture furniture = PropertiesList.get(position);
        holder.title.setText(furniture.getTitle());

        holder.linearLayout.setTag(position);

        final Animation animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);

        Picasso.with(context).load(furniture.getImage()).fit().into(holder.PropertyImg);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Intent intent = new Intent(context, ServiceDetailsActivity.class);
                intent.putExtra("furniture", PropertiesList.get((int) holder.linearLayout.getTag()));
                context.startActivity(intent);
//
            }
        });
    }

    @Override
    public int getItemCount() {
        return PropertiesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        private TextView title;
        private ImageView PropertyImg;

        public MyViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearItemOtherProperty);
            title = (TextView) view.findViewById(R.id.textViewItemOtherPropertiesTitle);

            PropertyImg = (ImageView) view.findViewById(R.id.imageViewItemOtherPropertiesImg);
        }
    }
}
