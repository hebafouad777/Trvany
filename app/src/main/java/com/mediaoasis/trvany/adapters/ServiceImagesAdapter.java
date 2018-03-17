package com.mediaoasis.trvany.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.provider.ServiceDetails2Activity;
import com.mediaoasis.trvany.activities.user.ServiceDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ASUS on 03/05/2016.
 */
public class ServiceImagesAdapter extends RecyclerView.Adapter<ServiceImagesAdapter.MyViewHolder> {

    Context context;
    boolean isBroker = false;
    private ArrayList<String> ImagesList;

    public ServiceImagesAdapter(Context con, ArrayList<String> list, boolean broker) {
        context = con;
        this.ImagesList = list;
        this.isBroker = broker;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String imgURL = ImagesList.get(position);

        final Animation animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);

        holder.img.setTag(position);
        Picasso.with(context).load(imgURL).into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animAlpha.start();
                if (!isBroker) {
                    int tag = (int) view.getTag();
                    String SelectedImageURL = ImagesList.get(tag);
                    Picasso.with(context).load(SelectedImageURL).fit().into(ServiceDetailsActivity.img_iv);
                } else {
                    int tag = (int) view.getTag();
                    String SelectedImageURL = ImagesList.get(tag);
                    Picasso.with(context).load(SelectedImageURL).fit().into(ServiceDetails2Activity.img_iv);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return ImagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        LinearLayout linearLayout;
        private TextView username_tv, review_tv;
        private RatingBar ratingBar;
        private ImageView img;

        public MyViewHolder(View view) {
            super(view);
//            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutItemProperty);

            img = (ImageView) view.findViewById(R.id.itemImg);
        }
    }
}
