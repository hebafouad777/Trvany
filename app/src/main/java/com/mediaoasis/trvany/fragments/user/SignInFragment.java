package com.mediaoasis.trvany.fragments.user;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.MainActivity;
import com.mediaoasis.trvany.models.Provider;
import com.mediaoasis.trvany.models.User;
import com.mediaoasis.trvany.utils.ConnectionDetector;
import com.mediaoasis.trvany.utils.SharedPref;


public class SignInFragment extends Fragment {

    EditText email_et, password_et;
    Button forget_password_btn, login_btn;

    ProgressDialog progressDialog;
    String Email = "", Password = "";
    Boolean Isprovider ;
    Provider provider;
    User CurrentUser = new User();

    FirebaseAuth firebaseAuth;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

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
        provider = new Provider();

        Button dialogButton = (Button) dialog.findViewById(R.id.buttonForgetPassword);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnectingToInternet(getActivity())) {
                    if (!text.getText().toString().equals("")) {
                        Email = text.getText().toString();

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.loading));
                        progressDialog.setTitle(getString(R.string.reset_password));
                        progressDialog.show();

                        firebaseAuth.sendPasswordResetEmail(Email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Log.d("ForgetPassword", "Email sent.");
                                            Toast.makeText(getActivity(), "An email is sent to you to reset your password",
                                                    Toast.LENGTH_LONG).show();
                                            dialog.dismiss();

                                        } else {
                                            Toast.makeText(getActivity(), task.getException().getMessage()
                                                    , Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                    }
                                    }
                                });

                        dialog.dismiss();
                    } else {
                        text.setError("Required!");
                        dialog.dismiss();
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet_con), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
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
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setTitle(getString(R.string.login));
                progressDialog.show();


                firebaseAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot();
                                    databaseReference.child("Providers").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                           provider = dataSnapshot.getValue(Provider.class);
                                            if (provider != null) {
                                                firebaseAuth.signOut();
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(), "Please Log in with a user account", Toast.LENGTH_LONG).show();


                                            }
                                            else
                                            {
                                                new SharedPref(getActivity()).setBoolean("isBroker", false);
                                                Toast.makeText(getActivity(), "Login succeed, Welcome", Toast.LENGTH_SHORT).show();
                                                checkDataFound(firebaseAuth.getCurrentUser());
                                                progressDialog.dismiss();
                                            }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });





                                } else {
                                    Toast.makeText(getActivity(), "Login failed, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
            } else
                Toast.makeText(getActivity(), R.string.no_internet_con, Toast.LENGTH_SHORT).show();

        }
//        else
//            Toast.makeText(getActivity(), "Login failed 3aaaa", Toast.LENGTH_SHORT).show();

    }

    private void checkDataFound(FirebaseUser firebaseUser) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("User").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentUser = dataSnapshot.getValue(User.class);
                if (CurrentUser != null) {
                    CurrentUser.setUserID(dataSnapshot.getKey());

                    progressDialog.dismiss();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                } else {

                    Toast.makeText(getActivity(), "Your data is missing as a User!\nTry to login as an agent.."
                            , Toast.LENGTH_LONG);

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
