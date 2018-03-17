package com.mediaoasis.trvany.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.models.Rating;
import com.mediaoasis.trvany.view.CustomRatingBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ASUS on 03/05/2016.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {

    Context context;
    int country, city;
    private ArrayList<Rating> ReviewsList;

    public ReviewsAdapter(Context con, ArrayList<Rating> list) {
        context = con;
        this.ReviewsList = list;
        this.city = city;
        this.country = country;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Rating review = ReviewsList.get(position);
        holder.username_tv.setText(review.getUserName());
        holder.review_tv.setText(review.getReview());
        holder.ratingBar.setScore(review.getRate());

        Picasso.with(context).load(review.getUserImage()).fit()
                .placeholder(R.drawable.profile_placeholder).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return ReviewsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        LinearLayout linearLayout;
        private TextView username_tv, review_tv;
        private CustomRatingBar ratingBar;
        private CircleImageView img;

        public MyViewHolder(View view) {
            super(view);
//            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutItemProperty);
            username_tv = (TextView) view.findViewById(R.id.itemReviewUsername);
            review_tv = (TextView) view.findViewById(R.id.itemReview);
            ratingBar = (CustomRatingBar) view.findViewById(R.id.itemRatingBar);
            ratingBar.setHalfStars(true);
            ratingBar.setClickable(false);
            ratingBar.setEnabled(false);
            ratingBar.setScrollToSelect(false);

            img = (CircleImageView) view.findViewById(R.id.itemReviewImg);
        }
    }
}
