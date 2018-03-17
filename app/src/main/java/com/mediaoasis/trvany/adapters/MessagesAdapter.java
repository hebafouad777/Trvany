package com.mediaoasis.trvany.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.provider.Main2Activity;
import com.mediaoasis.trvany.activities.user.MainActivity;
import com.mediaoasis.trvany.models.Conversation;
import com.mediaoasis.trvany.models.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ASUS on 03/05/2016.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    Context context;
    String CurrentUserID;
    Conversation conversation;
    private ArrayList<Message> messagesList;

    public MessagesAdapter(Context con, ArrayList<Message> list, Conversation conv) {
        context = con;
        this.messagesList = list;
        this.conversation = conv;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserID = firebaseAuth.getCurrentUser().getUid();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.linearLayoutFrom.setVisibility(View.INVISIBLE);
        holder.linearLayoutTo.setVisibility(View.INVISIBLE);

        final Message message = messagesList.get(position);

        if (message.getSenderId().equals(CurrentUserID)) {
            holder.linearLayoutFrom.setVisibility(View.VISIBLE);

            try {
                Picasso.with(context).load(MainActivity.CurrentUser.getPhotoURI()).fit()
                        .placeholder(R.drawable.profile_placeholder).into(holder.imgMe);
                Picasso.with(context).load(conversation.getBrokerImage()).fit()
                        .placeholder(R.drawable.profile_placeholder).into(holder.imgHim);
            } catch (NullPointerException NullExp) {
                Picasso.with(context).load(Main2Activity.currentProvider.getImage()).fit()
                        .placeholder(R.drawable.profile_placeholder).into(holder.imgMe);
                Picasso.with(context).load(conversation.getUserImage()).fit()
                        .placeholder(R.drawable.profile_placeholder).into(holder.imgHim);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (message.getType().equals("text")) {
                holder.image.setVisibility(View.GONE);
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(message.getText());
            } else if (message.getType().equals("image")) {
                holder.title.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                Picasso.with(context).load(message.getText()).fit().into(holder.image);
            } else if (message.getType().equals("location")) {
                holder.title.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                holder.image.setImageResource(R.drawable.map);
            }
        } else {
            holder.linearLayoutTo.setVisibility(View.VISIBLE);

            if (message.getType().equals("text")) {
                holder.image2.setVisibility(View.GONE);
                holder.title2.setVisibility(View.VISIBLE);
                holder.title2.setText(message.getText());
            } else if (message.getType().equals("image")) {
                holder.title2.setVisibility(View.GONE);
                holder.image2.setVisibility(View.VISIBLE);
                Picasso.with(context).load(message.getText()).fit().into(holder.image2);
            } else if (message.getType().equals("location")) {
                holder.title2.setVisibility(View.GONE);
                holder.image2.setVisibility(View.VISIBLE);
                holder.image2.setImageResource(R.drawable.map);
            }
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getType().equals("image"))
                    showImageDialog(message.getText());
                else if (message.getType().equals("location"))
                    showLocationOnGoogleMaps(message.getText());
            }
        });


        holder.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getType().equals("image"))
                    showImageDialog(message.getText());
                else if (message.getType().equals("location"))
                    showLocationOnGoogleMaps(message.getText());
            }
        });

    }

    private void showImageDialog(String URL) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_image);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.imageDialog);
        Picasso.with(context).load(URL).into(imageView);

        Button close = (Button) dialog.findViewById(R.id.buttonCloseImageDialog);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showLocationOnGoogleMaps(String Loc) {
        String[] locs = Loc.split(",");
        double latitude = Double.valueOf(locs[0]);
        double longitude = Double.valueOf(locs[1]);

        String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (Location Shared)";
//        String url = "http://maps.google.com/maps?daddr="+address;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        context.startActivity(intent);


//        GPSTracker gpsTracker = new GPSTracker(context);
//        if(gpsTracker.canGetLocation()) {
//            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                    Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
//            context.startActivity(intent);
//        }else
//            Toast.makeText(context, "Can't get Location right now", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        CardView linearLayout;
        LinearLayout linearLayoutFrom, linearLayoutTo;
        //        private SupportMapFragment map, map2;
        CircleImageView imgHim, imgMe;
        private TextView title, title2;
        private ImageView image, image2;
//        private GoogleMap googleMap;

        public MyViewHolder(View view) {
            super(view);
//            linearLayout = (CardView) view.findViewById(R.id.linearLayoutItemFood);
            linearLayoutFrom = (LinearLayout) view.findViewById(R.id.linearFromMe);
            linearLayoutTo = (LinearLayout) view.findViewById(R.id.linearFromHim);
            title = (TextView) view.findViewById(R.id.textView2);
            title2 = (TextView) view.findViewById(R.id.textView3);

            image = (ImageView) view.findViewById(R.id.imgView2);
            image2 = (ImageView) view.findViewById(R.id.imgView3);

            imgHim = (CircleImageView) view.findViewById(R.id.imgHim);
            imgMe = (CircleImageView) view.findViewById(R.id.imgMe);

//            map = (SupportMapFragment) (SupportMapFragment) context.getSupportFragmentManager()
//                    .findFragmentById(R.id.mapView2);
//            map2 = (SupportMapFragment)  context.getSupportFragmentManager()
//                    .findFragmentById(R.id.mapView3);

//            img = (CircleImageView) view.findViewById(R.id.circleItemUserImage);
        }
    }
}
