package com.mediaoasis.trvany.fragments.user;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.MainActivity;
import com.mediaoasis.trvany.activities.user.SearchActivity;
import com.mediaoasis.trvany.adapters.SpinnerAdapter;
import com.mediaoasis.trvany.models.City;
import com.mediaoasis.trvany.models.Country;
import com.mediaoasis.trvany.models.Offer;
import com.mediaoasis.trvany.models.Type;
import com.mediaoasis.trvany.utils.SharedPref;

import static com.mediaoasis.trvany.activities.user.MainActivity.AllCities;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCitiesNames;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCitiesNamesAr;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCitiesNamesTr;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCountries;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCountriesNames;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCountriesNamesAr;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCountriesNamesTr;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllOffers;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllOffersAR;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllOffersNames;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllOffersTR;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllTypes;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllTypesAR;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllTypesNames;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllTypesTR;

/**
 * Created by Nasr on 1/9/2017.
 */

public class HomeFragment extends Fragment {
    Button search_btn;
    Button adv_search_btn;
    Spinner country_sp, city_sp, type_sp, offer_sp;
    int SelectedCityPosition = 0, SelectedCountryPosition = 0, SelectedTypePos = 0, SelectedOfferPos = 0;
    SpinnerAdapter CountriesAdapter, CitiesAdapter, OffersAdapter, TypeAdapter;
    Dialog progressDialog;
    DatabaseReference citiesRef, countryRef;
    FirebaseDatabase firebaseDatabase;
    String CountryName, CityName;
    boolean isDataReady = false;
    SharedPref sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPref = new SharedPref(getActivity());

        progressDialog = new Dialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.dialog_please_wait);
        progressDialog.show();

        search_btn = (Button) getActivity().findViewById(R.id.buttonSearch);
        adv_search_btn = (Button) getActivity().findViewById(R.id.buttonAdvSearch);
        country_sp = (Spinner) getActivity().findViewById(R.id.editTextSearchCountry);
        city_sp = (Spinner) getActivity().findViewById(R.id.editTextSearchCity);
        type_sp = (Spinner) getActivity().findViewById(R.id.editTextSearchType);
        offer_sp = (Spinner) getActivity().findViewById(R.id.editTextSearchOffer);

        firebaseDatabase = FirebaseDatabase.getInstance();

        countryRef = firebaseDatabase.getReference().getRoot().child("Countries");
        countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childreen = dataSnapshot.getChildren();
                Country country;
                for (DataSnapshot child : childreen) {
                    country = child.getValue(Country.class);
                    AllCountries.add(country);
                    AllCountriesNames.add(country.getName());
                    AllCountriesNamesAr.add(country.getNameAr());
                    AllCountriesNamesTr.add(country.getNameTr());
                }

                if (sharedPref.getLanguage().equals("en"))
                    CountriesAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllCountriesNames);
                if (sharedPref.getLanguage().equals("ar"))
                    CountriesAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllCountriesNamesAr);
                if (sharedPref.getLanguage().equals("tr"))
                    CountriesAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllCountriesNamesTr);
                country_sp.setAdapter(CountriesAdapter);

                for (int i = 0; i < AllCountriesNames.size(); i++) {
                    if (AllCountriesNames.get(i).equals(MainActivity.CurrentUser.getCountry())) {
                        CountryName = AllCountriesNames.get(i);
                        country_sp.setSelection(i);
                        getCities();
                        break;
                    }
                }

                getTypes();
                getOffers();

                if (isDataReady) {
//                    if (sharedPref.getLanguage().equals("en")) {
//                        OffersAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllOffersNames);
//                        offer_sp.setAdapter(OffersAdapter);
//
//                        TypeAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllTypesNames);
//                        type_sp.setAdapter(TypeAdapter);
//                    } else if (sharedPref.getLanguage().equals("ar")) {
//                        OffersAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllOffersAR);
//                        offer_sp.setAdapter(OffersAdapter);
//
//                        TypeAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllTypesAR);
//                        type_sp.setAdapter(TypeAdapter);
//                    } else if (sharedPref.getLanguage().equals("tr")) {
//                        OffersAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllOffersTR);
//                        offer_sp.setAdapter(OffersAdapter);
//
//                        TypeAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllTypesTR);
//                        type_sp.setAdapter(TypeAdapter);
//                    }
                    progressDialog.dismiss();
                } else
                    isDataReady = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


        country_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedCountryPosition = i;
                CountryName = AllCountriesNames.get(i);
                Log.e("CountryName", CountryName);
                getCities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                SelectedCityPosition = 0;
            }
        });

        city_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedCityPosition = i;
                CityName = AllCitiesNames.get(i);
                Log.e("CityName", CityName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                SelectedCityPosition = 0;
            }
        });


        type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedTypePos = i;
                Log.e("type", AllTypesNames.get(SelectedTypePos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                SelectedTypePos = 0;
            }
        });

        offer_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedOfferPos = i;
                Log.e("offer", AllOffersNames.get(SelectedOfferPos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                SelectedOfferPos = 0;
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("isAdv", false);
                intent.putExtra("country", SelectedCountryPosition);
                intent.putExtra("city", SelectedCityPosition);
                intent.putExtra("offer", SelectedOfferPos);
                intent.putExtra("type", SelectedTypePos);
                startActivity(intent);
            }
        });

        adv_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("isAdv", true);
                startActivity(intent);
            }
        });

//        help_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), WalkThroughActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void getCities() {
        citiesRef = firebaseDatabase.getReference().getRoot().child("Cities").child(CountryName);
        citiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AllCities.clear();
                AllCitiesNames.clear();
                AllCitiesNamesAr.clear();
                AllCitiesNamesTr.clear();
                Iterable<DataSnapshot> childreen = dataSnapshot.getChildren();
                City city;
                for (DataSnapshot child : childreen) {
                    city = child.getValue(City.class);
                    AllCities.add(city);
                    AllCitiesNames.add(city.getName());
                    AllCitiesNamesAr.add(city.getNameAr());
                    AllCitiesNamesTr.add(city.getNameTr());
                }
                if (isDataReady) {
                    if (sharedPref.getLanguage().equals("en")) {
                        CitiesAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllCitiesNames);
                        city_sp.setAdapter(CitiesAdapter);

                        OffersAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllOffersNames);
                        offer_sp.setAdapter(OffersAdapter);

                        TypeAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllTypesNames);
                        type_sp.setAdapter(TypeAdapter);
                    } else if (sharedPref.getLanguage().equals("ar")) {
                        CitiesAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllCitiesNamesAr);
                        city_sp.setAdapter(CitiesAdapter);

                        OffersAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllOffersAR);
                        offer_sp.setAdapter(OffersAdapter);

                        TypeAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllTypesAR);
                        type_sp.setAdapter(TypeAdapter);
                    } else if (sharedPref.getLanguage().equals("tr")) {
                        CitiesAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllCitiesNamesTr);
                        city_sp.setAdapter(CitiesAdapter);

                        OffersAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllOffersTR);
                        offer_sp.setAdapter(OffersAdapter);

                        TypeAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllTypesTR);
                        type_sp.setAdapter(TypeAdapter);
                    }

                    progressDialog.dismiss();
                } else
                    isDataReady = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    void getOffers() {
        AllOffers.clear();
        AllOffersNames.clear();
        AllOffersAR.clear();
        AllOffersTR.clear();

        DatabaseReference countryRef = firebaseDatabase.getReference().getRoot().child("Offers");
        countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childreen = dataSnapshot.getChildren();
                Offer offer;
                for (DataSnapshot child : childreen) {
                    offer = child.getValue(Offer.class);
                    AllOffers.add(offer);
                    AllOffersNames.add(offer.getName());
                    AllOffersAR.add(offer.getNameAR());
                    AllOffersTR.add(offer.getNameTR());
                }
                if (sharedPref.getLanguage().equals("en"))
                    OffersAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllOffersNames);
                else if (sharedPref.getLanguage().equals("ar"))
                    OffersAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllOffersAR);
                else if (sharedPref.getLanguage().equals("tr"))
                    OffersAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllOffersTR);
                offer_sp.setAdapter(OffersAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getTypes() {
        AllTypes.clear();
        AllTypesNames.clear();
        AllTypesAR.clear();
        AllTypesTR.clear();

        DatabaseReference countryRef = firebaseDatabase.getReference().getRoot().child("Types");
        countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childreen = dataSnapshot.getChildren();
                Type offer;
                for (DataSnapshot child : childreen) {
                    offer = child.getValue(Type.class);
                    AllTypes.add(offer);
                    AllTypesNames.add(offer.getName());
                    AllTypesAR.add(offer.getNameAR());
                    AllTypesTR.add(offer.getNameTR());
                }
                if (sharedPref.getLanguage().equals("en"))
                    TypeAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllTypesNames);
                else if (sharedPref.getLanguage().equals("ar"))
                    TypeAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllTypesAR);
                else if (sharedPref.getLanguage().equals("tr"))
                    TypeAdapter = new SpinnerAdapter(getActivity(), android.R.id.text1, AllTypesTR);
                type_sp.setAdapter(TypeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
