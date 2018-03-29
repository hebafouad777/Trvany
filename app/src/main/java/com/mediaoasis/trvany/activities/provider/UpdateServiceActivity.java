package com.mediaoasis.trvany.activities.provider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.PickupLocationActivity;
import com.mediaoasis.trvany.adapters.ServiceImages2Adapter;
import com.mediaoasis.trvany.models.Area;
import com.mediaoasis.trvany.models.Furniture;
import com.mediaoasis.trvany.utils.SharedPref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.mediaoasis.trvany.activities.provider.AddServiceActivity.showLocation;
import static com.mediaoasis.trvany.activities.provider.Main2Activity.currentProvider;
import static com.mediaoasis.trvany.activities.provider.ServiceDetails2Activity.ImagesKeys;
import static com.mediaoasis.trvany.activities.provider.ServiceDetails2Activity.ImagesList;
import static com.mediaoasis.trvany.activities.provider.ServiceDetails2Activity.PropertyOrders;


public class UpdateServiceActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 123;
    private static final int CAM_REQUREST = 2;
    public static String LocationName = "", LocationAddress = "";
    public static LatLng LocationLatLng = new LatLng(0, 0);
    ProgressDialog progressDialog;
    String[] perms = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    Furniture currentFurniture;

    List<Area> Areas;
    String CityName, AreaName;

    TextView locationName_tv, locationAddress_tv, txtPriceLable;
    EditText title_et, description_et, price_et;
    Button updateProperty_btn, deleteProperty_btn;
    LinearLayout location_ll, addImage_btn;
    RadioGroup areas_rg;

    ServiceImages2Adapter mAdapter;
    RecyclerView mRecyclerView;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    List<RadioButton> radioButtons;
    String ImagPlaceholderURI = "https://firebasestorage.googleapis.com/v0/b/ossul-9a87f.appspot.com/o/" +
            "ServicesImages%2Fproperty-placeholder.png?alt=media&token=e416aec4-ee3e-47d9-9ccf-9644906d28c1";
    private LinearLayoutManager mLayoutManager;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_service);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        Areas = new ArrayList<>();
        sharedPref = new SharedPref(UpdateServiceActivity.this);

        Intent intent = getIntent();
        if (intent != null && intent.getParcelableExtra("property") != null)
            currentFurniture = intent.getParcelableExtra("property");
        else {
            Toast.makeText(this, getResources().getString(R.string.error_happened), Toast.LENGTH_SHORT).show();
            finish();
        }

        locationAddress_tv = (TextView) findViewById(R.id.txtPropertyLocationAddress);
        locationName_tv = (TextView) findViewById(R.id.txtPropertyLocationName);
        txtPriceLable = (TextView) findViewById(R.id.txtPriceLable);

        title_et = (EditText) findViewById(R.id.txtPropertyTitle);
        description_et = (EditText) findViewById(R.id.txtPropertyDescription);
        price_et = (EditText) findViewById(R.id.txtPropertyPrice);

        addImage_btn = (LinearLayout) findViewById(R.id.buttonPropertyAddImage);
        location_ll = (LinearLayout) findViewById(R.id.linearLocation);

        deleteProperty_btn = (Button) findViewById(R.id.textDeleteProperty);
        updateProperty_btn = (Button) findViewById(R.id.buttonPropertySaveEdit);

        areas_rg = (RadioGroup) findViewById(R.id.radioGroupAreas);
        progressBar = (ProgressBar) findViewById(R.id.prog);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewGallery);
        mLayoutManager = new LinearLayoutManager(UpdateServiceActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        title_et.setText(currentFurniture.getTitle());
        description_et.setText(currentFurniture.getDescription());
        price_et.setText(currentFurniture.getPrice() + "");

        mAdapter = new ServiceImages2Adapter(UpdateServiceActivity.this, ImagesList, currentFurniture);
        mRecyclerView.setAdapter(mAdapter);

        LocationAddress = currentFurniture.getAddress();
        LocationLatLng = new LatLng(currentFurniture.getLatitude(), currentFurniture.getLongitude());
        locationAddress_tv.setText(LocationAddress);

        location_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateServiceActivity.this, PickupLocationActivity.class);
                intent.putExtra("UpdateService", true);
                startActivity(intent);
            }
        });

        CityName = currentFurniture.getCity();
        getAreas();

        addImage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageTypeDialog();
//                Toast.makeText(AddServiceActivity.this, "There's a bug in this feature," +
//                        "working on it..", Toast.LENGTH_LONG).show();
            }
        });

        updateProperty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] isUpdateFinished = {false};
//                final ArrayList<String> ImagesList2, ImagesKeys2;
//
//                ImagesList2 = ImagesList;
//                ImagesKeys2 = ImagesKeys;
                progressDialog = new ProgressDialog(UpdateServiceActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setTitle(getString(R.string.save_changes));
                progressDialog.show();
                currentFurniture.setTitle(title_et.getText().toString());
                currentFurniture.setDescription(description_et.getText().toString());
                currentFurniture.setPrice(Integer.valueOf(price_et.getText().toString()));
                currentFurniture.setAddress(LocationAddress);
                currentFurniture.setLatitude(LocationLatLng.latitude);
                currentFurniture.setLongitude(LocationLatLng.longitude);
                currentFurniture.setArea(AreaName);

                databaseReference = firebaseDatabase.getReference().getRoot().child("Providers")
                        .child(currentProvider.getBrokerID()).child("Furniture").child(currentFurniture.getPropertyID());

                databaseReference.child("title").setValue(title_et.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child("description").setValue(description_et.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                databaseReference.child("price").setValue(Integer.valueOf(price_et.getText().toString()))
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                databaseReference.child("address").setValue(LocationAddress)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                databaseReference.child("latitude").setValue(LocationLatLng.latitude)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                databaseReference.child("longitude").setValue(LocationLatLng.longitude)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                databaseReference.child("area").setValue(AreaName)
                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                if (!isUpdateFinished[0])
                                                                                                                                    isUpdateFinished[0] = true;
                                                                                                                                else {
                                                                                                                                    progressDialog.dismiss();
                                                                                                                                    Toast.makeText(UpdateServiceActivity.this
                                                                                                                                            , R.string.data_saved, Toast.LENGTH_SHORT).show();
                                                                                                                                   /* ServiceDetails2Activity.location_tv.setText(locationName_tv.getText()+" "+locationAddress_tv.getText(););
                                                                                                                                    ServiceDetails2Activity.description_tv.setText(description_et.getText());
                                                                                                                                    ServiceDetails2Activity.title_tv.setText(title_et.getText());*/
                                                                                                                                    finish();
                                                                                                                                }
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
                                            }
                                        });
                            }
                        });


                final DatabaseReference propertyRef = firebaseDatabase.getReference().getRoot()
                        .child("Furniture")
                        .child(currentFurniture.getCountry()).child(currentFurniture.getCity())
                        .child(currentFurniture.getOffer()).child(currentFurniture.getType())
                        .child(currentFurniture.getPropertyID());

                propertyRef.child("title").setValue(title_et.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                propertyRef.child("description").setValue(description_et.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                propertyRef.child("price").setValue(Integer.valueOf(price_et.getText().toString()))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                propertyRef.child("address").setValue(LocationAddress)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                propertyRef.child("latitude").setValue(LocationLatLng.latitude)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                propertyRef.child("longitude").setValue(LocationLatLng.longitude)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                propertyRef.child("area").setValue(AreaName)
                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                if (!isUpdateFinished[0])
                                                                                                                                    isUpdateFinished[0] = true;
                                                                                                                                else {
                                                                                                                                    progressDialog.dismiss();
                                                                                                                                    Toast.makeText(UpdateServiceActivity.this
                                                                                                                                            , R.string.data_saved, Toast.LENGTH_SHORT).show();
                                                                                                                                    finish();

                                                                                                                                }
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
                                            }
                                        });
                            }
                        });

//                databaseReference.setValue(currentFurniture).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            for (int i = 0; i < ImagesList.size(); i++)
//                                databaseReference.child("images").child(ImagesKeys2.get(i)).setValue(ImagesList2.get(i))
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (!isUpdateFinished[0])
//                                                    isUpdateFinished[0] = true;
//                                                else {
//                                                    finish();
//                                                }
//                                            }
//                                        });
//                        }
//
//                    }
//                });

//                propertyRef.setValue(currentFurniture).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful())
//                            for (int i = 0; i < ImagesList.size(); i++)
//                                propertyRef.child("images").child(ImagesKeys2.get(i)).setValue(ImagesList2.get(i))
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (!isUpdateFinished[0])
//                                                    isUpdateFinished[0] = true;
//                                                else {
//                                                    finish();
//                                                }
//                                            }
//                                        });
//                    }
//                });

            }
        });

        deleteProperty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PropertyOrders.size() == 0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateServiceActivity.this);
                    alertDialogBuilder.setTitle("Delete Furniture");
                    alertDialogBuilder
                            .setMessage(R.string.delete_property_question)
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteProperty();
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateServiceActivity.this);
                    alertDialogBuilder.setTitle(R.string.delete_property);
                    alertDialogBuilder
                            .setMessage(getString(R.string.you_have) + PropertyOrders.size() +
                                    getString(R.string.requests_from_clients) + getString(R.string.cancel_requests))
                            .setCancelable(false)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    deleteProperty();
//                                    dialog.cancel();
//                                }
//                            })
                            .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

    }

    void getAreas() {
        DatabaseReference areaRef = firebaseDatabase.getReference().getRoot().child("Areas")
                .child(CityName);
        areaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Areas.clear();

                Area allAreas = new Area();
                allAreas.setName("All Areas");
                allAreas.setNameAr("كل المناطق");
                allAreas.setNameTr("Bütün Alanlar");
                Areas.add(allAreas);

                radioButtons = new ArrayList<>();
                areas_rg.removeAllViews();

                AreaName = currentFurniture.getArea();

                RadioButton radioB = new RadioButton(UpdateServiceActivity.this);
                radioB.setId(0);
                radioB.setButtonDrawable(R.drawable.custom_btn_radio);
                radioB.setCompoundDrawablePadding(50);
                radioB.setText(getResources().getString(R.string.all_areas));
                radioB.setChecked(true);
                areas_rg.addView(radioB);
                radioButtons.add(radioB);

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Area area;
                for (DataSnapshot child : children) {
                    area = child.getValue(Area.class);
                    Areas.add(area);

                    RadioButton radioButton = new RadioButton(UpdateServiceActivity.this);
                    radioButton.setId(Areas.size());
                    radioButton.setButtonDrawable(R.drawable.custom_btn_radio);
                    radioButton.setCompoundDrawablePadding(50);
                    if (sharedPref.getLanguage().equals("en"))
                        radioButton.setText(area.getName());
                    else if (sharedPref.getLanguage().equals("ar"))
                        radioButton.setText(area.getNameAr());
                    else if (sharedPref.getLanguage().equals("tr"))
                        radioButton.setText(area.getNameTr());
                    radioButtons.add(radioButton);
                    areas_rg.addView(radioButton);
                }

                for (int i = 0; i < radioButtons.size(); i++)
                    if (Areas.get(i).getName().equals(AreaName) || Areas.get(i).getNameAr().equals(AreaName)
                            || Areas.get(i).getNameTr().equals(AreaName)) {
                        radioButtons.get(i).setChecked(true);
                        break;
                    }

                progressBar.setVisibility(View.INVISIBLE);

                areas_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                        for (int i = 0; i < radioButtons.size(); i++)
                            if (radioButtons.get(i).getId() == checkedId) {
                                AreaName = Areas.get(i).getName();
                                Log.e("AreaName", AreaName);
                                break;
                            }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (showLocation) {
            locationName_tv.setText(LocationName);
            locationAddress_tv.setText(LocationAddress);
        }
//        initializeListeners();
    }

    void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image))
                , GALLERY_REQUEST_CODE);
    }


    private void uploadImageToFirebase(final String ID, Uri uri, final boolean isFirstPhoto) {
        final long millis = System.currentTimeMillis() % 1000;

        StorageReference ref = storageReference.child("ServicesImages").child(ID).child("Img" + millis);

        final Dialog dialog = new Dialog(UpdateServiceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_upload_image);
        final TextView textView = (TextView) dialog.findViewById(R.id.textDialogUpload);
        dialog.setCancelable(false);
        dialog.show();

        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();

                        Uri uri = taskSnapshot.getDownloadUrl();

                        DatabaseReference databaseRef = firebaseDatabase.getReference().getRoot()
                                .child("Providers").child(currentProvider.getBrokerID()).child("Furniture").child(ID)
                                .child("images");
                        DatabaseReference ref = databaseRef.child("Img" + millis);
                        ref.setValue(uri.toString());

                        DatabaseReference propertyRef = firebaseDatabase.getReference().getRoot().child("Furniture")
                                .child(currentFurniture.getCountry()).child(CityName)
                                .child(currentFurniture.getOffer())
                                .child(currentFurniture.getType())
                                .child(ID);
                        DatabaseReference propertyRef2 = propertyRef.child("images").child("Img" + millis);
                        propertyRef2.setValue(uri.toString());

                        ImagesList.add(uri.toString());

                        if (isFirstPhoto) {
                            ImagesList.remove(0);

                            DatabaseReference databaseRef2 = firebaseDatabase.getReference().getRoot()
                                    .child("Providers").child(currentProvider.getBrokerID()).child("Furniture").child(ID)
                                    .child("image");
                            databaseRef2.setValue(uri.toString());

                            DatabaseReference reference = firebaseDatabase.getReference().getRoot()
                                    .child("Furniture").child(currentFurniture.getCountry()).child(CityName)
                                    .child(currentFurniture.getOffer()).child(currentFurniture.getType())
                                    .child(ID).child("image");
                            reference.setValue(uri.toString());

                            DatabaseReference reference1 = firebaseDatabase.getReference().getRoot()
                                    .child("Providers").child(currentProvider.getBrokerID()).child("Furniture").child(ID)
                                    .child("images").child("img0");
                            reference1.setValue(null);

                            DatabaseReference reference2 = firebaseDatabase.getReference().getRoot()
                                    .child("Furniture").child(currentFurniture.getCountry()).child(CityName)
                                    .child(currentFurniture.getOffer()).child(currentFurniture.getType())
                                    .child(ID).child("images").child("img0");
                            reference2.setValue(null);

                            currentProvider.setImage(uri.toString());

                            mAdapter.notifyDataSetChanged();
                        }
                        Toast.makeText(UpdateServiceActivity.this, R.string.uploaded_success, Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();
                Toast.makeText(UpdateServiceActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                textView.setText(progress + getResources().getString(R.string.percent_uploaded));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            try {
                Uri uri = data.getData();
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if (ImagesList.size() == 1 && ImagesList.get(0).equals(ImagPlaceholderURI)) {
                    uploadImageToFirebase(currentFurniture.getPropertyID(), uri, true);
                } else {
                    uploadImageToFirebase(currentFurniture.getPropertyID(), uri, false);
                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(UpdateServiceActivity.this, getResources().getString(R.string.error_happened)
                        + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAM_REQUREST) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri photoURI = createImageFile(photo);
                if (ImagesList.size() == 1 && ImagesList.get(0).equals(ImagPlaceholderURI)) {
                    uploadImageToFirebase(currentFurniture.getPropertyID(), photoURI, true);
                } else {
                    uploadImageToFirebase(currentFurniture.getPropertyID(), photoURI, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(UpdateServiceActivity.this, getString(R.string.error_happened) + e.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showLocation = false;
    }

    private void deleteProperty() {
        final Dialog dialog = new Dialog(UpdateServiceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_please_wait);
        dialog.setCancelable(false);
        dialog.show();

        final String PropertyID = currentFurniture.getPropertyID();

        databaseReference = firebaseDatabase.getReference().getRoot().child("Providers")
                .child(currentProvider.getBrokerID()).child("Furniture").child(PropertyID);

        databaseReference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DatabaseReference propertyRef = firebaseDatabase.getReference().getRoot().child("Furniture")
                            .child(currentFurniture.getCountry()).child(currentFurniture.getCity())
                            .child(currentFurniture.getOffer()).child(currentFurniture.getType()).child(PropertyID);

                    propertyRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            deleteAllPropertyImages(currentFurniture);

                            dialog.dismiss();
                            showLocation = false;
                            Toast.makeText(UpdateServiceActivity.this,
                                    R.string.property_deleted, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                } else {
                    Toast.makeText(UpdateServiceActivity.this, R.string.property_delete_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteAllPropertyImages(Furniture currentFurniture) {

        Log.e("Images Deleted", "started");
        for (int i = 0; i < ImagesList.size(); i++) {
            deleteCurrentImage(currentFurniture, i);
        }
    }

    private void deleteCurrentImage(final Furniture furniture, final int index) {
        final String key = ImagesKeys.get(index);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference desertRef =
                storageRef.child("FurnitureImages/" + furniture.getPropertyID() + "/" + key);
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Images Deleted", "done");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UpdateServiceActivity.this, getResources().getString(R.string.error_happened)
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showImageTypeDialog() {
        final Dialog dialog = new Dialog(UpdateServiceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_type);

        LinearLayout cameraLL = (LinearLayout) dialog.findViewById(R.id.LinearImageCamera);
        LinearLayout galleryLL = (LinearLayout) dialog.findViewById(R.id.LinearGallery);
        LinearLayout cancelLL = (LinearLayout) dialog.findViewById(R.id.LinearCancel);

        cameraLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(UpdateServiceActivity.this, getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                showCamera();
                dialog.dismiss();
            }
        });

        galleryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateServiceActivity.this, getString(R.string.select_image), Toast.LENGTH_SHORT).show();
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
        if (ActivityCompat.checkSelfPermission(UpdateServiceActivity.this,
                android.Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAM_REQUREST);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(UpdateServiceActivity.this, getString(R.string.location_permission_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                requestPermissions(perms, CAM_REQUREST);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAM_REQUREST);
            }
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAM_REQUREST);
        }

    }

    private Uri createImageFile(Bitmap bitmap) throws IOException {
        // Create an image file name
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        long curMills = System.currentTimeMillis();
        File file = new File(path, "ossul-" + curMills + ".jpg");
        fOut = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.flush(); // Not really required
        fOut.close(); // do not forget to close the stream

        String path2 = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ossul-" + curMills, null);
        return Uri.parse(path2);
    }
}
