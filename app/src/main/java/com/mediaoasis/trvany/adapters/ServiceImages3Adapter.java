package com.mediaoasis.trvany.adapters;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.mediaoasis.trvany.R;

import java.util.List;

/**
 * Created by ASUS on 03/05/2016.
 */
public class ServiceImages3Adapter extends RecyclerView.Adapter<ServiceImages3Adapter.MyViewHolder> {

    Context context;
    String PropertyID;
    private List<Uri> ImagesList;

    public ServiceImages3Adapter(Context con, List<Uri> list) {
        context = con;
//        PropertyID = PropID;
        this.ImagesList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_edit, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Uri imgBitmap = ImagesList.get(position);

        final Animation animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);

        holder.img.setImageURI(imgBitmap);

        holder.img.setTag(position);
        holder.deleteImg.setTag(position);

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animAlpha.start();
                int i = (int) (view.getTag());
                showDeleteImageDialog(i);
            }
        });

    }

    void showDeleteImageDialog(final int index) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_delete_image);

        Button delete_btn = (Button) dialog.findViewById(R.id.btnDialogDeleteImg);
        Button cancel_btn = (Button) dialog.findViewById(R.id.btnDialogDeleteCancel);

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCurrentImage(index);
                dialog.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deleteCurrentImage(final int index) {
        ImagesList.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ImagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        LinearLayout linearLayout;
        private Button deleteImg;
        private ImageView img;

        public MyViewHolder(View view) {
            super(view);
//            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutItemProperty);
            deleteImg = (Button) view.findViewById(R.id.itemImgDelete);
            img = (ImageView) view.findViewById(R.id.itemImg);
        }
    }
}
