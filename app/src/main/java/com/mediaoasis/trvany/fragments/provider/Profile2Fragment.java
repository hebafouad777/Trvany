package com.mediaoasis.trvany.fragments.provider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.provider.Main2Activity;
import com.mediaoasis.trvany.adapters.SpinnerAdapter;
import com.mediaoasis.trvany.utils.ConnectionDetector;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nasr on 1/9/2017.
 */

public class Profile2Fragment extends Fragment {
    public static boolean isPicFromCamera2 = false;
    public static int PICK_IMAGE_REQUEST = 234;
    String[] perms = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int CAM_REQUREST = 2;
    private static final int GALLERY_REQUEST_CODE = 1;
    public static CircleImageView img;
    public static Uri ProfileURL, DownloadURI;
    Button save_btn, changePassword_btn;
    EditText name_et, phone_et, email_et, address_et;
    Spinner title_sp;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Bitmap bitmap;
    String Title;

    int TitleIndex = 0;
    List<String> Titles;
    SpinnerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile2, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        save_btn = (Button) getActivity().findViewById(R.id.buttonSaveChanges);
        changePassword_btn = (Button) getActivity().findViewById(R.id.buttonResetPassword);

        img = (CircleImageView) getActivity().findViewById(R.id.img_profile);
        title_sp = (Spinner) getActivity().findViewById(R.id.spinnerBrokerTitle);

        name_et = (EditText) getActivity().findViewById(R.id.editTextName);
        phone_et = (EditText) getActivity().findViewById(R.id.editTextPhone);
        email_et = (EditText) getActivity().findViewById(R.id.editTextEmail);
        address_et = (EditText) getActivity().findViewById(R.id.editTextAddress);

        email_et.setEnabled(false);
        email_et.setText(firebaseUser.getEmail());
        name_et.setText(Main2Activity.currentProvider.getName());
        phone_et.setText(Main2Activity.currentProvider.getPhone());
        address_et.setText(Main2Activity.currentProvider.getAddress());
        Picasso.with(getActivity()).load(Main2Activity.currentProvider.getImage()).into(img);


        Titles = new ArrayList<>();
        Titles.add(getActivity().getString(R.string.real_state_broker));
        Titles.add(getActivity().getString(R.string.freelancer_broker));
        Titles.add(getActivity().getString(R.string.property_owner));

        Title = Main2Activity.currentProvider.getTitle();
        for (int i = 0; i < Titles.size(); i++)
            if (Titles.get(i).equals(Title)) {
                TitleIndex = i;
                break;
            }

        adapter = new SpinnerAdapter(getActivity(), android.R.id.text1, Titles);
        title_sp.setAdapter(adapter);
        title_sp.setSelection(TitleIndex);

        title_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Title = Titles.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageTypeDialog();
            }
        });


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please Wait....");
                progressDialog.setTitle("Update Profile");
                progressDialog.show();

                final String name, phone, address, title = Title;
                name = name_et.getText().toString();
                phone = phone_et.getText().toString();
                address = address_et.getText().toString();
//                broker.setTitle(Title);
//                broker.setImage(user.getImage());

                final DatabaseReference brokerRef = databaseReference.child("Providers").child(firebaseUser.getUid());
                brokerRef.child("name").setValue
                        (name, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                Main2Activity.currentProvider.setName(name);

                                brokerRef.child("phone").setValue
                                        (phone, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError,
                                                                   DatabaseReference databaseReference) {

                                                Main2Activity.currentProvider.setPhone(phone);

                                                brokerRef.child("address").setValue
                                                        (address, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError,
                                                                                   DatabaseReference databaseReference) {

                                                                Main2Activity.currentProvider.setAddress(address);

                                                                brokerRef.child("title").setValue
                                                                        (title, new DatabaseReference.CompletionListener() {
                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError,
                                                                                                   DatabaseReference databaseReference) {

                                                                                Main2Activity.currentProvider.setTitle(Title);


                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(getActivity(), "Data Saved Successfully!",
                                                                                        Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        });

                                                            }
                                                        });


                                            }
                                        });


                            }
                        });

            }
        });

        changePassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                show_dialog();
            }
        });

    }

    private void showImageTypeDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_type);

        LinearLayout cameraLL = (LinearLayout) dialog.findViewById(R.id.LinearImageCamera);
        LinearLayout galleryLL = (LinearLayout) dialog.findViewById(R.id.LinearGallery);
        LinearLayout cancelLL = (LinearLayout) dialog.findViewById(R.id.LinearCancel);

        cameraLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                showCamera();
                dialog.dismiss();
            }
        });

        galleryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                showFileChooser();
                dialog.dismiss();
            }
        });

        cancelLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    void showCamera() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPicFromCamera2 = true;
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAM_REQUREST);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(getActivity(), getString(R.string.location_permission_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                requestPermissions(perms, CAM_REQUREST);
            } else {
                isPicFromCamera2 = true;
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAM_REQUREST);
            }
            isPicFromCamera2 = true;
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAM_REQUREST);
        }

    }


    private void show_dialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reset_password);
//        dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        final EditText textOld = (EditText) dialog.findViewById(R.id.edittextOldPassword);
        final EditText textNew = (EditText) dialog.findViewById(R.id.edittextNewPassword);
        final EditText textConfirmNew = (EditText) dialog.findViewById(R.id.edittextConfirmNewPassword);
//        text.setText(Email);

        Button dialogButton = (Button) dialog.findViewById(R.id.buttonForgetPasswordSubmit);
        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.buttonForgetPasswordCancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnectingToInternet(getActivity())) {
                    if (!textOld.getText().toString().equals("") && !textNew.getText().toString().equals("")
                            && !textConfirmNew.getText().toString().equals("")) {
                        if (textNew.getText().toString().equals(textConfirmNew.getText().toString())) {
                            String newPassword = textNew.getText().toString();

                            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage("Sending....");
                            progressDialog.setTitle("Change Password");
                            progressDialog.show();

                            firebaseUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "User password Updated",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });


                            dialog.dismiss();
                        } else
                            textConfirmNew.setError("Value not same as new password");
                    } else {
                        if (textOld.getText().toString().equals(""))
                            textOld.setError("Required!");
                        else if (textNew.getText().toString().equals(""))
                            textNew.setError("Required!");
                        else if (textConfirmNew.getText().toString().equals(""))
                            textConfirmNew.setError("Required!");
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet_con), Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select an image.."), PICK_IMAGE_REQUEST);
    }
}
