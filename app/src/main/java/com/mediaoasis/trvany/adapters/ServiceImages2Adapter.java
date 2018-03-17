package com.mediaoasis.trvany.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.provider.ServiceDetails2Activity;
import com.mediaoasis.trvany.models.Furniture;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.mediaoasis.trvany.activities.provider.ServiceDetails2Activity.ImagesKeys;


/**
 * Created by ASUS on 03/05/2016.
 */
public class ServiceImages2Adapter extends RecyclerView.Adapter<ServiceImages2Adapter.MyViewHolder> {

    Context context;
    Furniture furniture;
    FirebaseDatabase firebaseDatabase;
    String ImagPlaceholderURI = "https://firebasestorage.googleapis.com/v0/b/ossul-9a87f.appspot.com/o/" +
            "PropertiesImages%2Fproperty-placeholder.png?alt=media&token=e416aec4-ee3e-47d9-9ccf-9644906d28c1";
    private ArrayList<String> ImagesList;

    public ServiceImages2Adapter(Context con, ArrayList<String> list, Furniture Prop) {
        context = con;
        furniture = Prop;
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
        final String imgURL = ImagesList.get(position);

        final Animation animAlpha = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);

        Picasso.with(context).load(imgURL).fit().into(holder.img);

        holder.img.setTag(position);
        holder.deleteImg.setTag(position);

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animAlpha.start();
                int i = (int) (view.getTag());
                if (!ImagesList.get(i).equals(ImagPlaceholderURI))
                    showDeleteImageDialog(i);
                else
                    Toast.makeText(context, "You can't delete the placeholder image", Toast.LENGTH_SHORT).show();
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
        final String key = ImagesKeys.get(index);
        String ImgURI = ServiceDetails2Activity.ImagesList.get(index);

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().getRoot().child("Brokers")
                .child(furniture.getBrokerID()).child("Properties").child(furniture.getPropertyID())
                .child("images").child(key);
        reference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DatabaseReference reference1 = firebaseDatabase.getReference().getRoot().child("Properties")
                            .child(furniture.getCountry()).child(furniture.getCity()).child(furniture.getOffer())
                            .child(furniture.getType()).child(furniture.getPropertyID()).child("images").child(key);
                    reference1.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference storageRef = firebaseStorage.getReference();
                            StorageReference desertRef =
                                    storageRef.child("PropertiesImages/" + furniture.getPropertyID() + "/" + key);
                            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                        ImagesKeys.remove(index);
//                                        ImagesKeys.remove(index);

                                    if (ImagesList.size() > 1 && index == 0) {
                                        String SecondImageURI = ImagesList.get(1);
                                        changeMainImage(SecondImageURI);
                                    } else if (ImagesList.size() == 1) {
                                        setMainImageToDefault();
                                    } else {
                                        Toast.makeText(context, "Image Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                        ImagesList.remove(index);
                                        notifyDataSetChanged();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(context, "an error occurred!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });


    }

    private void changeMainImage(final String secondImageURI) {
        DatabaseReference reference = firebaseDatabase.getReference().getRoot().child("Brokers")
                .child(furniture.getBrokerID()).child("Properties").child(furniture.getPropertyID())
                .child("image");
        reference.setValue(secondImageURI).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference reference1 = firebaseDatabase.getReference().getRoot().child("Properties")
                        .child(furniture.getCountry()).child(furniture.getCity()).child(furniture.getOffer())
                        .child(furniture.getType()).child(furniture.getPropertyID()).child("image");
                reference1.setValue(secondImageURI).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Image Deleted Successfully!", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void setMainImageToDefault() {
        final String ImagPlaceholderURI = "https://firebasestorage.googleapis.com/v0/b/ossul-9a87f.appspot.com/o/" +
                "PropertiesImages%2Fproperty-placeholder.png?alt=media&token=e416aec4-ee3e-47d9-9ccf-9644906d28c1";
        changeMainImage(ImagPlaceholderURI);

        DatabaseReference reference = firebaseDatabase.getReference().getRoot().child("Brokers")
                .child(furniture.getBrokerID()).child("Properties").child(furniture.getPropertyID())
                .child("images").child("img0");
        reference.setValue(ImagPlaceholderURI).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference reference1 = firebaseDatabase.getReference().getRoot().child("Properties")
                        .child(furniture.getCountry()).child(furniture.getCity()).child(furniture.getOffer())
                        .child(furniture.getType()).child(furniture.getPropertyID()).child("images").child("img0");
                reference1.setValue(ImagPlaceholderURI).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Image Deleted Successfully!", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
            }
        });
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
