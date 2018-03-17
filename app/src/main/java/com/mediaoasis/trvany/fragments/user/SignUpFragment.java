package com.mediaoasis.trvany.fragments.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.MainActivity;
import com.mediaoasis.trvany.models.User;


public class SignUpFragment extends Fragment {

    FirebaseAuth firebaseAuth;

    EditText email_et, password_et, name_et, phone_et, confirmPassword_et;
    Button Register_btn;
    ProgressDialog progressDialog;
    String Email = "", Password = "", Username = "", Phone = "";

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        name_et = (EditText) getActivity().findViewById(R.id.editTextName);
        email_et = (EditText) getActivity().findViewById(R.id.editTextEmail);
        password_et = (EditText) getActivity().findViewById(R.id.editTextPassword);
        confirmPassword_et = (EditText) getActivity().findViewById(R.id.editTextConfirmPassword);
        phone_et = (EditText) getActivity().findViewById(R.id.editTextPhone);

        Register_btn = (Button) getActivity().findViewById(R.id.buttonRegister);
        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        Email = email_et.getText().toString();
        Password = password_et.getText().toString();
        Username = name_et.getText().toString();
        Phone = phone_et.getText().toString();

        if (checkStringsIfEmpty()) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setTitle(getString(R.string.sign_up));
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                saveUserInFireDatabase();
                                progressDialog.dismiss();

                            } else
                                Toast.makeText(getActivity(), "Register failed, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

        }

    }

    private void saveUserInFireDatabase() {

        User mUser = new User();
        mUser.setEmail(Email);
        mUser.setUsername(Username);
        mUser.setPhone(Phone);
        mUser.setPhotoURI("https://firebasestorage.googleapis.com/v0/b/ossul-9a87f.appspot.com/o/" +
                "users%20pictures%2Fperson.png?alt=media&token=91a2d3db-28da-43c1-afa5-7f9a66e2351c");

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.getRoot().child("User").child(firebaseUser.getUid()).setValue(mUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Register succeeded, Welcome", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private boolean checkStringsIfEmpty() {
        boolean isBothNotEmpty = true;
        if (TextUtils.isEmpty(Username)) {
            name_et.setError(getString(R.string.name_empty));
            isBothNotEmpty = false;
        }
        if (TextUtils.isEmpty(Email)) {
            email_et.setError(getString(R.string.empty_email));
            isBothNotEmpty = false;
        }
        if (TextUtils.isEmpty(Password)) {
            password_et.setError(getString(R.string.empty_password));
            isBothNotEmpty = false;
        }
        if (TextUtils.isEmpty(confirmPassword_et.getText().toString())) {
            confirmPassword_et.setError(getString(R.string.empty_password));
            isBothNotEmpty = false;
        }
        if (TextUtils.isEmpty(Phone)) {
            phone_et.setError(getString(R.string.phone_empty));
            isBothNotEmpty = false;
        }
        if (!confirmPassword_et.getText().toString().equals(Password)) {
            confirmPassword_et.setError(getString(R.string.password_not_matched));
            isBothNotEmpty = false;
        }
        return isBothNotEmpty;
    }


}
