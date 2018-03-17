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
import com.mediaoasis.trvany.activities.provider.ServiceDetails2Activity;
import com.mediaoasis.trvany.activities.user.ServiceDetailsActivity;
import com.mediaoasis.trvany.models.Furniture;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ASUS on 03/05/2016.
 */
public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.MyViewHolder> {

    Context context;
    boolean isBroker;
    private List<Furniture> PropertiesList;

    public ServicesAdapter(Context con, List<Furniture> list, boolean broker) {
        context = con;
        this.PropertiesList = list;
        isBroker = broker;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_property, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Furniture furniture = PropertiesList.get(position);
        holder.title.setText(furniture.getTitle());
        holder.description.setText(furniture.getDescription());
        holder.linearLayout.setTag(position);

        final Animation animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);

        Picasso.with(context).load(furniture.getImage()).into(holder.PropertyImg);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if(!isBroker) {
                    Intent intent = new Intent(context, ServiceDetailsActivity.class);
                    intent.putExtra("furniture", PropertiesList.get((int) holder.linearLayout.getTag()));
                    context.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(context, ServiceDetails2Activity.class);
                    intent.putExtra("furniture", PropertiesList.get((int) holder.linearLayout.getTag()));
                    context.startActivity(intent);
                }
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
        private TextView title, description;
        private ImageView PropertyImg;

        public MyViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutItemProperty);
            title = (TextView) view.findViewById(R.id.textViewItemSearchPropertyTitle);
            description = (TextView) view.findViewById(R.id.textViewItemSearchPropertyName);

            PropertyImg = (ImageView) view.findViewById(R.id.circleItemProperty);
        }
    }
}
