package com.mediaoasis.trvany.fragments.provider;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.provider.ProviderAccessActivity;
import com.mediaoasis.trvany.activities.provider.ProviderPoliciesActivity;
import com.mediaoasis.trvany.adapters.SpinnerAdapter;
import com.mediaoasis.trvany.models.Provider;
import com.mediaoasis.trvany.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpBrokerFragment extends Fragment {
    private static final int CAM_REQUREST = 2;
    private static final int GALLERY_REQUEST_CODE = 1;
    public static CircleImageView img;
    public static boolean isPicFromCamera = false;
    String[] perms = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    FirebaseAuth firebaseAuth;
    CheckBox policy_cb;
    LinearLayout policy_ll, photo_ll;
    Spinner title_sp;
    EditText email_et, password_et, name_et, phone_et, confirmPassword_et, address_et;
    Button Register_btn;
    String Email = "", Password = "", Username = "", Phone = "", Title = "", Address = "";
    Dialog dialog1;
    List<String> Titles;
    SpinnerAdapter adapter;
    SharedPref sharedPref;
    StorageReference storageReference;

    public SignUpBrokerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_broker, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPref = new SharedPref(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        Titles = new ArrayList<>();
        adapter = new SpinnerAdapter(getActivity(), android.R.id.text1, Titles);

        Titles.add(getString(R.string.real_state_broker));
        Titles.add(getString(R.string.freelancer_broker));
        Titles.add(getString(R.string.property_owner));

        Title = Titles.get(0);

        name_et = (EditText) getActivity().findViewById(R.id.editTextName);
        email_et = (EditText) getActivity().findViewById(R.id.editTextEmail);
        address_et = (EditText) getActivity().findViewById(R.id.editTextAddress);
        password_et = (EditText) getActivity().findViewById(R.id.editTextPassword);
        confirmPassword_et = (EditText) getActivity().findViewById(R.id.editTextConfirmPassword);
        phone_et = (EditText) getActivity().findViewById(R.id.editTextPhone);
        Register_btn = (Button) getActivity().findViewById(R.id.buttonRegister);
        policy_cb = (CheckBox) getActivity().findViewById(R.id.checkboxPolicy);
        policy_ll = (LinearLayout) getActivity().findViewById(R.id.linearPolicy);

        photo_ll = (LinearLayout) getActivity().findViewById(R.id.linearProfilePhoto);
        img = (CircleImageView) getActivity().findViewById(R.id.imgSignupBrokerImg);

        title_sp = (Spinner) getActivity().findViewById(R.id.spinnerBrokerTitle);
        title_sp.setAdapter(adapter);

        title_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Title = Titles.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        policy_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProviderPoliciesActivity.class));
            }
        });

        photo_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageTypeDialog();
            }
        });

        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerBroker();
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

    void showFileChooser() {
        isPicFromCamera = false;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), GALLERY_REQUEST_CODE);
    }

    void showCamera() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPicFromCamera = true;
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
                isPicFromCamera = true;
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAM_REQUREST);
            }
            isPicFromCamera = true;
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAM_REQUREST);
        }

    }


    private void uploadImageToFirebase(final String brokerID) {
        if (ProviderAccessActivity.ProfileURL != null) {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("ProvidersImages").child(brokerID);

            final Dialog progressDialog = new Dialog(getActivity());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_upload_image);
            final TextView textView = (TextView) progressDialog.findViewById(R.id.textDialogUpload);
            progressDialog.show();

            ref.putFile(ProviderAccessActivity.ProfileURL)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri DownloadURI = taskSnapshot.getDownloadUrl();

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Providers").child(brokerID).child("image")
                                    .setValue(DownloadURI.toString());

                            progressDialog.dismiss();
//                            Toast.makeText(getActivity(), "Uploaded Successfully..", Toast.LENGTH_SHORT).show();

                            showConfirmationDialog();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    textView.setText(progress + " % uploaded");
                }
            });
        }

    }

    private void showConfirmationDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_signup_confirmation);

        Button done_btn = (Button) dialog.findViewById(R.id.buttonDialogConfirmDone);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

//                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//                firebaseAuth.signOut();
//
                startActivity(new Intent(getActivity(), ProviderAccessActivity.class));
                getActivity().finish();
            }
        });

        dialog.show();
    }

    private void registerBroker() {
        Email = email_et.getText().toString();
        Password = password_et.getText().toString();
        Username = name_et.getText().toString();
        Phone = phone_et.getText().toString();
        Address = address_et.getText().toString();

        if (checkStringsIfEmpty()) {
            dialog1 = new Dialog(getActivity());
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.dialog_signing_up);
            dialog1.show();

            firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                saveUserInFireDatabase();

                            } else {
                                dialog1.dismiss();
                                Toast.makeText(getActivity(), getString(R.string.register_failed)
                                                + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }

    }

    private void saveUserInFireDatabase() {

        Provider provider = new Provider();
        provider.setTitle(Title);
        provider.setEmail(Email);
        provider.setName(Username);
        provider.setPhone(Phone);
        provider.setAddress(Address);
        provider.setActivated(1);
        provider.setRate(0);
        provider.setImage("https://firebasestorage.googleapis.com/v0/b/jaxi-65321.appspot.com/o/profile.png?" +
                "alt=media&token=b5a0be1e-2c36-49f9-85ac-b4daa5470259");

        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.getRoot().child("Providers").child(firebaseUser.getUid())
                .setValue(provider, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        sharedPref.setBoolean("isBroker", true);
                        dialog1.dismiss();
                        if (databaseError == null) {
                            uploadImageToFirebase(firebaseUser.getUid());
                        }
                    }
                });
    }

    private boolean checkStringsIfEmpty() {
        boolean isBothNotEmpty = true;
        if (TextUtils.isEmpty(Username)) {
            name_et.setError("Name is Empty");
            isBothNotEmpty = false;
        }
        if (TextUtils.isEmpty(Email)) {
            email_et.setError("Email is Empty");
            isBothNotEmpty = false;
        }
        if (TextUtils.isEmpty(Password)) {
            password_et.setError("Password is Empty");
            isBothNotEmpty = false;
        }
        if (TextUtils.isEmpty(confirmPassword_et.getText().toString())) {
            confirmPassword_et.setError("Password is Empty");
            isBothNotEmpty = false;
        }
        if (TextUtils.isEmpty(Phone)) {
            phone_et.setError("Phone is Empty");
            isBothNotEmpty = false;
        }
        if (TextUtils.isEmpty(Address)) {
            address_et.setError("Address is Empty");
            isBothNotEmpty = false;
        }
        if (ProviderAccessActivity.ProfileURL == null) {
            Toast.makeText(getActivity(), R.string.add_company_logo, Toast.LENGTH_SHORT).show();
            isBothNotEmpty = false;
        }
        if (!confirmPassword_et.getText().toString().equals(Password)) {
            confirmPassword_et.setError(getString(R.string.password_not_matched));
            isBothNotEmpty = false;
        }
        if (!policy_cb.isChecked()) {
            policy_cb.setError(getString(R.string.agree_terms));
            isBothNotEmpty = false;
        }
        return isBothNotEmpty;
    }

}
