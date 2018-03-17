package com.mediaoasis.trvany.fragments.user;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.MainActivity;
import com.mediaoasis.trvany.adapters.SpinnerAdapter;
import com.mediaoasis.trvany.models.Country;
import com.mediaoasis.trvany.models.User;
import com.mediaoasis.trvany.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nasr on 1/9/2017.
 */

public class SettingsFragment extends Fragment {

    Button saveChanges;
    CheckBox allowNotifications_cb;
    Spinner country_sp, lang_sp;

    List<String> AllCountriesNames;
    SpinnerAdapter CountriesAdapter, LanguagesAdapter;
    int SelectedCountryIndex = 0, isNotificationsAllowed = 1;
    boolean isCountryChanged = false, isNotificationChanged = false;

    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    SharedPref sharedPref;
    List<String> Languages;
    int langIndex = 0, oldIndex = 0;
    String langCode = "ar";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        AllCountriesNames = new ArrayList<>();
        Languages = new ArrayList<>();

        sharedPref = new SharedPref(getActivity());

        country_sp = (Spinner) getActivity().findViewById(R.id.spinnerCountry);
        lang_sp = (Spinner) getActivity().findViewById(R.id.spinnerLanguage);
        allowNotifications_cb = (CheckBox) getActivity().findViewById(R.id.checkboxNotifications);
        saveChanges = (Button) getActivity().findViewById(R.id.buttonSettingsSave);

        DatabaseReference countryRef = firebaseDatabase.getReference().getRoot().child("Countries");
        countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childreen = dataSnapshot.getChildren();
                Country country;
                for (DataSnapshot child : childreen) {
                    country = child.getValue(Country.class);
                    AllCountriesNames.add(country.getName());
                }
                CountriesAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllCountriesNames);
                country_sp.setAdapter(CountriesAdapter);

                for (int i = 0; i < AllCountriesNames.size(); i++)
                    if (AllCountriesNames.get(i).equals(MainActivity.CurrentUser.getCountry())) {
                        SelectedCountryIndex = i;
                        country_sp.setSelection(SelectedCountryIndex);
                        break;
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Languages.add("العربية");
        Languages.add("English");
        Languages.add("Türk");

        LanguagesAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, Languages);
        lang_sp.setAdapter(LanguagesAdapter);

        if (sharedPref.getLanguage().equals("ar")) {
            oldIndex = 0;
            langCode = "ar";
        } else if (sharedPref.getLanguage().equals("en")) {
            oldIndex = 1;
            langCode = "en";
        } else if (sharedPref.getLanguage().equals("tr")) {
            oldIndex = 2;
            langCode = "tr";
        }
        Log.e("oldIndex", oldIndex + "");
        Log.e("oldLangCode", langCode + "");
        lang_sp.setSelection(oldIndex);

        lang_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                langIndex = position;
                if (langIndex == 0) {
                    langCode = "ar";
                } else if (langIndex == 1) {
                    langCode = "en";
                } else if (langIndex == 2) {
                    langCode = "tr";
                }
                Log.e("langIndex", langIndex + "");
                Log.e("langCode", langCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        country_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedCountryIndex = i;
                isCountryChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (MainActivity.CurrentUser.getAllowNotifications() == 1)
            allowNotifications_cb.setChecked(true);
        else
            allowNotifications_cb.setChecked(false);
        isNotificationsAllowed = MainActivity.CurrentUser.getAllowNotifications();

        allowNotifications_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && isNotificationsAllowed == 0) {
                    isNotificationChanged = true;
                    isNotificationsAllowed = 1;
                } else if (!b && isNotificationsAllowed == 1) {
                    isNotificationChanged = false;
                    isNotificationsAllowed = 0;
                }

            }
        });


        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.setTitle(getString(R.string.updating_settings));
                progressDialog.show();

                final User user = MainActivity.CurrentUser;

                user.setAllowNotifications(isNotificationsAllowed);
                user.setCountry(AllCountriesNames.get(SelectedCountryIndex));

                final DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(firebaseUser.getUid());
                userRef.child("country").setValue(AllCountriesNames.get(SelectedCountryIndex))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    userRef.child("allowNotifications").setValue(isNotificationsAllowed)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (oldIndex != langIndex) {
                                                            userRef.child("language").setValue(langCode)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sharedPref.setLanguage(getActivity(), langCode);
                                                                    userRef.child("language").setValue(langCode);

                                                                    MainActivity.CurrentUser
                                                                            .setCountry(AllCountriesNames.get(SelectedCountryIndex));
                                                                    MainActivity.CurrentUser.
                                                                            setAllowNotifications(isNotificationsAllowed);
                                                                    MainActivity.CurrentUser.setLanguage(langCode);
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(getActivity(),
                                                                            R.string.settings_updated, Toast.LENGTH_SHORT).show();

                                                                }
                                                            });

                                                        }

                                                    }
                                                }
                                            });


                                }
                            }
                        });

            }
        });


    }
}
