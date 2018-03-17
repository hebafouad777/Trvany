package com.mediaoasis.trvany.fragments.provider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.provider.Main2Activity;
import com.mediaoasis.trvany.activities.provider.ProviderAccessActivity;
import com.mediaoasis.trvany.models.Provider;
import com.mediaoasis.trvany.utils.ConnectionDetector;
import com.mediaoasis.trvany.utils.SharedPref;


public class SignInBrokerFragment extends Fragment {

    EditText email_et, password_et;
    Button forget_password_btn, login_btn;

    String Email = "", Password = "";
    Dialog dialog;
    FirebaseAuth firebaseAuth;
    Provider currentProvider;
    SharedPref sharedPref;

    public SignInBrokerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in_broker, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPref = new SharedPref(getActivity());

        email_et = (EditText) getActivity().findViewById(R.id.editTextSignInEmail);
        password_et = (EditText) getActivity().findViewById(R.id.editTextSignInPassword);
        forget_password_btn = (Button) getActivity().findViewById(R.id.buttonForgetPassword);
        login_btn = (Button) getActivity().findViewById(R.id.buttonLogin);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        forget_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_dialog();
            }
        });


    }

    private void show_dialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forget_password);
//        dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        final EditText text = (EditText) dialog.findViewById(R.id.edittextForgetEmail);
        text.setText(Email);

        Button dialogButton = (Button) dialog.findViewById(R.id.buttonForgetPassword);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnectingToInternet(getActivity())) {
                    if (!text.getText().toString().equals("")) {
                        Email = text.getText().toString();

                        final Dialog dialog1 = new Dialog(getActivity());
                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog1.setContentView(R.layout.dialog_please_wait);
                        dialog1.show();

                        firebaseAuth.sendPasswordResetEmail(Email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("ForgetPassword", "Email sent");
                                            Toast.makeText(getActivity(), "Email sent",
                                                    Toast.LENGTH_SHORT).show();
                                            dialog1.dismiss();
                                        }
                                    }
                                });

                        dialog.dismiss();
                    } else {
                        text.setError(getString(R.string.required));
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet_con), Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }

    private void loginUser() {
        Email = email_et.getText().toString().trim().toLowerCase();
        Password = password_et.getText().toString().trim();

        if (checkStringsIfEmpty()) {
            if (ConnectionDetector.isConnectingToInternet(getActivity())) {
                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_signing_in);
                dialog.show();

                firebaseAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    sharedPref.setBoolean("isBroker", true);
                                    checkIfActivated(firebaseAuth.getCurrentUser().getUid());

                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), getString(R.string.login_failed)
                                            + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getActivity(), R.string.no_internet_con, Toast.LENGTH_SHORT).show();
            }
        }
//        else
//            Toast.makeText(getActivity(), "Login failed 3aaaa", Toast.LENGTH_SHORT).show();

    }

    private void checkIfActivated(String uID) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot();
        databaseReference.child("Providers").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentProvider = dataSnapshot.getValue(Provider.class);
                if (currentProvider != null) {
                    currentProvider.setBrokerID(dataSnapshot.getKey());

                    if (currentProvider.getActivated() == 1) {
                        dialog.dismiss();
                        sharedPref.setBoolean("isActivated", true);
                        Toast.makeText(getActivity(), getString(R.string.welcome)
                                + currentProvider.getName(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), Main2Activity.class);
                        intent.putExtra("broker", currentProvider);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), ProviderAccessActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.create_new_account, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
//                    Toast.makeText(getActivity(), R.string.sign_in_as_a_user,
//                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean checkStringsIfEmpty() {
        boolean isBothNotEmpty = true;
        if (Email.equals("")) {
            email_et.setError(getString(R.string.empty_email));
            isBothNotEmpty = false;
        }
        if (Password.equals("")) {
            password_et.setError(getString(R.string.empty_password));
            isBothNotEmpty = false;
        }
        return isBothNotEmpty;
    }
}
