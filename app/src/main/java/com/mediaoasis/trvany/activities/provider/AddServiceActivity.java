package com.mediaoasis.trvany.activities.provider;

import android.app.Dialog;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.mediaoasis.trvany.adapters.ServiceImages3Adapter;
import com.mediaoasis.trvany.adapters.SpinnerAdapter;
import com.mediaoasis.trvany.models.Area;
import com.mediaoasis.trvany.models.City;
import com.mediaoasis.trvany.models.Country;
import com.mediaoasis.trvany.models.Furniture;
import com.mediaoasis.trvany.models.Offer;
import com.mediaoasis.trvany.models.Type;
import com.mediaoasis.trvany.utils.SharedPref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.mediaoasis.trvany.activities.provider.Main2Activity.currentProvider;


public class AddServiceActivity extends AppCompatActivity {
    private static final int CAM_REQUREST = 2;
    private static final int GALLERY_REQUEST_CODE = 123;
    public static boolean showLocation = false;
    public static String LocationName = "", LocationAddress = "";
    public static LatLng LocationLatLng = new LatLng(0, 0);
    String[] perms = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    List<Uri> ProfileURL, DownloadURI;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    //    ServiceMyImagesAdapter imagesAdapter;
//    List<Bitmap> bitmaps;
    List<City> AllCities;
    List<Country> AllCountries;
    List<Offer> AllOffers;
    List<Type> AllTypes;
    List<String> /*AllAreasNames, AllAreasNamesAr, AllAreasNamesTr,*/
            AllCitiesNames, AllCitiesNamesAr, AllCitiesNamesTr,
            AllCountriesNames, AllCountriesNamesAr, AllCountriesNamesTr,
            AllTypesNames, AllOffersNames, AllOffersAR, AllTypesAR, AllOffersTR, AllTypesTR;
    SpinnerAdapter CountriesAdapter, CitiesAdapter, TypesAdapter, OffersAdapter/*, PriceRangeAdapter, AreasAdapter*/;
    Spinner country_sp, city_sp, type_sp, offer_sp/*, priceRange_sp*/;
    int SelectedTypePos = 0, SelectedOfferPos = 0,
            SelectedPriceRangePos = 0;
    List<Area> Areas;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    boolean isAreasLoaded = false;
    String CountryName, CityName, AreaName;
    LinearLayout location_ll, country_ll, city_ll, type_ll, offer_ll;
    TextView locationName_tv, locationAddress_tv, txtTitleToolbar, txtPriceLable;
    EditText title_et, description_et, price_et;
    Button saveService_btn;
    LinearLayout addImage_btn;
    Furniture currentFurniture;

    ServiceImages3Adapter mAdapterURI;
    RadioGroup areas_rg;
    ProgressBar progressBar;
    List<RadioButton> radioButtons;
    String ImagPlaceholderURI = "https://firebasestorage.googleapis.com/v0/b/ossul-9a87f.appspot.com/o/" +
            "PropertiesImages%2Fproperty-placeholder.png?alt=media&token=1d3d49b3-95d6-4bcc-9f28-2a3bbf06b8f0";
    SharedPref sharedPref;
    Uri photoURI;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        LinearLayout back_btn = (LinearLayout) findViewById(R.id.toolbarBack);
//        back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showLocation = false;
//                finish();
//            }
//        });
//        txtTitleToolbar = (TextView) findViewById(R.id.txtTitleToolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        ProfileURL = new ArrayList<>();
        DownloadURI = new ArrayList<>();
        sharedPref = new SharedPref(AddServiceActivity.this);

        country_sp = (Spinner) findViewById(R.id.spinnerFilterCountry);
        city_sp = (Spinner) findViewById(R.id.spinnerFilterCities);
        type_sp = (Spinner) findViewById(R.id.spinnerFilterServiceType);
        offer_sp = (Spinner) findViewById(R.id.spinnerFilterOffer);

        locationAddress_tv = (TextView) findViewById(R.id.txtServiceLocationAddress);
        locationName_tv = (TextView) findViewById(R.id.txtServiceLocationName);
        txtPriceLable = (TextView) findViewById(R.id.txtPriceLable);

        country_ll = (LinearLayout) findViewById(R.id.linearCountrySpinner);
        city_ll = (LinearLayout) findViewById(R.id.linearCitySpinner);
        type_ll = (LinearLayout) findViewById(R.id.linearTypeSpinner);
        offer_ll = (LinearLayout) findViewById(R.id.linearOfferSpinner);
        location_ll = (LinearLayout) findViewById(R.id.linearLocation);

        saveService_btn = (Button) findViewById(R.id.buttonServiceSaveEdit);
        addImage_btn = (LinearLayout) findViewById(R.id.buttonServiceAddImage);

        title_et = (EditText) findViewById(R.id.txtServiceTitle);
        description_et = (EditText) findViewById(R.id.txtServiceDescription);
        price_et = (EditText) findViewById(R.id.txtServicePrice);

        areas_rg = (RadioGroup) findViewById(R.id.radioGroupAreas);
        progressBar = (ProgressBar) findViewById(R.id.prog);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewGallery);
        mLayoutManager = new LinearLayoutManager(AddServiceActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        initializeLists();

        getCountries();
        getOffers();
        getTypes();

        addImage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageTypeDialog();
            }
        });

        location_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddServiceActivity.this, ServiceLocationActivity.class);
                startActivity(intent);
            }
        });

        saveService_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPercentageDialog();
            }
        });
    }

    private void showImageTypeDialog() {
        final Dialog dialog = new Dialog(AddServiceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_type);

        LinearLayout cameraLL = (LinearLayout) dialog.findViewById(R.id.LinearImageCamera);
        LinearLayout galleryLL = (LinearLayout) dialog.findViewById(R.id.LinearGallery);
        LinearLayout cancelLL = (LinearLayout) dialog.findViewById(R.id.LinearCancel);

        cameraLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddServiceActivity.this, getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                showCamera();
                dialog.dismiss();
            }
        });

        galleryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddServiceActivity.this, getString(R.string.select_image), Toast.LENGTH_SHORT).show();
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

    private void showPercentageDialog() {
        final Dialog dialog = new Dialog(AddServiceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_percentage_confirmation);

        final boolean[] isChecked = {false};

        CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.checkboxPercent);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked[0] = b;
            }
        });

        Button done_btn = (Button) dialog.findViewById(R.id.buttonDialogConfirmDone);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChecked[0])
                    addServiceToBrokerServices();
                else
                    Toast.makeText(AddServiceActivity.this, R.string.confirm_term_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (showLocation) {
            locationName_tv.setText(LocationName);
            locationAddress_tv.setText(LocationAddress);
        }
    }


    void getCountries() {
        AllCountries.clear();
        AllCountriesNames.clear();
        AllCountriesNamesAr.clear();
        AllCountriesNamesTr.clear();

        DatabaseReference countryRef = firebaseDatabase.getReference().getRoot().child("Countries");
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
                    CountriesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCountriesNames);
                else if (sharedPref.getLanguage().equals("ar"))
                    CountriesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCountriesNamesAr);
                else if (sharedPref.getLanguage().equals("tr"))
                    CountriesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCountriesNamesTr);
                country_sp.setAdapter(CountriesAdapter);
                CountryName = AllCountriesNames.get(0);
                getCities();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getCities() {
        AllCities.clear();
        AllCitiesNames.clear();
        AllCitiesNamesAr.clear();
        AllCitiesNamesTr.clear();

        DatabaseReference citiesRef = firebaseDatabase.getReference().getRoot().child("Cities").child(CountryName);
        citiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childreen = dataSnapshot.getChildren();
                City city;
                for (DataSnapshot child : childreen) {
                    city = child.getValue(City.class);
                    AllCities.add(city);
                    AllCitiesNames.add(city.getName());
                    AllCitiesNamesAr.add(city.getNameAr());
                    AllCitiesNamesTr.add(city.getNameTr());
                }
                if (sharedPref.getLanguage().equals("en"))
                    CitiesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCitiesNames);
                else if (sharedPref.getLanguage().equals("ar"))
                    CitiesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCitiesNamesAr);
                else if (sharedPref.getLanguage().equals("tr"))
                    CitiesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCitiesNamesTr);
                city_sp.setAdapter(CitiesAdapter);
                CityName = AllCitiesNames.get(0);
                getAreas();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getAreas() {
        DatabaseReference areaRef = firebaseDatabase.getReference().getRoot().child("Areas")
                .child(CityName);
        areaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                radioButtons = new ArrayList<>();
                areas_rg.removeAllViews();
                Areas.clear();

                Area allAreas = new Area();
                allAreas.setName("All Areas");
                allAreas.setNameAr("كل المناطق");
                allAreas.setNameTr("Bütün Alanlar");
                Areas.add(allAreas);
//                AreaName = "All Areas";

                RadioButton radioB = new RadioButton(AddServiceActivity.this);
                radioB.setId(0);
                radioB.setButtonDrawable(R.drawable.custom_btn_radio);
                radioB.setCompoundDrawablePadding(50);
                radioB.setText(R.string.all_areas);
                radioB.setChecked(true);
                areas_rg.addView(radioB);
                radioButtons.add(radioB);

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Area area;
                for (DataSnapshot child : children) {
                    area = child.getValue(Area.class);
                    Areas.add(area);

                    RadioButton radioButton = new RadioButton(AddServiceActivity.this);
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

                AreaName = Areas.get(0).getName();

                initializeListeners();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    OffersAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllOffersNames);
                else if (sharedPref.getLanguage().equals("ar"))
                    OffersAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllOffersAR);
                else if (sharedPref.getLanguage().equals("tr"))
                    OffersAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllOffersTR);
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
                    TypesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllTypesNames);
                else if (sharedPref.getLanguage().equals("ar"))
                    TypesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllTypesAR);
                else if (sharedPref.getLanguage().equals("tr"))
                    TypesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllTypesTR);
                type_sp.setAdapter(TypesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeListeners() {
        country_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CountryName = AllCountriesNames.get(i);
                getCities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        city_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CityName = AllCitiesNames.get(i);
                getAreas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        offer_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedOfferPos = i;
                SelectedPriceRangePos = 0;
//                if (AllOffers.get(i).equals("Rent"))
//                    txtPriceLable.setText(R.string.price_per_month);
//                else
                txtPriceLable.setText(R.string.price);
                Log.e("offer", AllOffersNames.get(SelectedOfferPos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedTypePos = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initializeLists() {
        ProfileURL = new ArrayList<>();
        AllCities = new ArrayList<>();
        AllCountries = new ArrayList<>();
        AllCities = new ArrayList<>();
        AllCountries = new ArrayList<>();

        AllCountriesNames = new ArrayList<>();
        AllCountriesNamesAr = new ArrayList<>();
        AllCountriesNamesTr = new ArrayList<>();

        AllCitiesNames = new ArrayList<>();
        AllCitiesNamesAr = new ArrayList<>();
        AllCitiesNamesTr = new ArrayList<>();

        AllOffers = new ArrayList<>();
        AllOffersNames = new ArrayList<>();
        AllOffersAR = new ArrayList<>();
        AllOffersTR = new ArrayList<>();

        AllTypes = new ArrayList<>();
        AllTypesNames = new ArrayList<>();
        AllTypesAR = new ArrayList<>();
        AllTypesTR = new ArrayList<>();

        Areas = new ArrayList<>();
//        Areas.add(getString(R.string.all_areas));


        if (sharedPref.getLanguage().equals("en")) {
            CountriesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCountriesNames);
            CitiesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCitiesNames);
            OffersAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllOffers);
            TypesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllTypes);
        } else if (sharedPref.getLanguage().equals("ar")) {
            CountriesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCountriesNamesAr);
            CitiesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCitiesNamesAr);
            OffersAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllOffersAR);
            TypesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllTypesAR);
        } else if (sharedPref.getLanguage().equals("tr")) {
            CountriesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCountriesNamesTr);
            CitiesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllCitiesNamesTr);
            OffersAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllOffersTR);
            TypesAdapter = new SpinnerAdapter(AddServiceActivity.this, android.R.id.text1, AllTypesTR);
        }

//        type_sp.setAdapter(TypesAdapter);
        offer_sp.setAdapter(OffersAdapter);
    }

    void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), GALLERY_REQUEST_CODE);
    }

    void addServiceToBrokerServices() {
        final Furniture property = new Furniture();

        property.setCountry(CountryName);
        property.setCity(CityName);
        property.setArea(AreaName);

        property.setType(AllTypesNames.get(SelectedTypePos));
        property.setOffer(AllOffersNames.get(SelectedOfferPos));

        property.setAddress(LocationName + ", " + LocationAddress);
        property.setLatitude(LocationLatLng.latitude);
        property.setLongitude(LocationLatLng.longitude);

        if (LocationLatLng.latitude != 0 && LocationLatLng.longitude != 0) {
            try {
                property.setBrokerID(currentProvider.getBrokerID());
                property.setDescription(description_et.getText().toString());
                property.setTitle(title_et.getText().toString());
                property.setPrice(Integer.parseInt(price_et.getText().toString()));

                if (ProfileURL.size() == 0)
                    property.setImage(ImagPlaceholderURI);

                final Dialog dialog = new Dialog(AddServiceActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_please_wait);
                dialog.show();

                databaseReference = firebaseDatabase.getReference().getRoot().child("Providers")
                        .child(currentProvider.getBrokerID()).child("Furniture");

                final String ServiceID = databaseReference.push().getKey();
                databaseReference.child(ServiceID).setValue(property).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (ProfileURL.size() == 0)
                            databaseReference.child(ServiceID).child("images").child("img0").setValue(ImagPlaceholderURI);

                        final DatabaseReference propertyRef = firebaseDatabase.getReference().getRoot().child("Furniture")
                                .child(CountryName).child(CityName).child(AllOffersNames.get(SelectedOfferPos))
                                .child(AllTypesNames.get(SelectedTypePos)).child(ServiceID);

                        propertyRef.setValue(property).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (ProfileURL.size() == 0)
                                    propertyRef.child("images").child("img0").setValue(ImagPlaceholderURI);

                                dialog.dismiss();
                                Toast.makeText(AddServiceActivity.this, "Furniture Added Successfully",
                                        Toast.LENGTH_SHORT).show();
                                if (ProfileURL.size() > 0) {
                                    if (ProfileURL.size() == 1) {
                                        uploadImageToFirebase(ServiceID, ProfileURL.get(0), true, true);
                                    } else {
                                        for (int i = 0; i < ProfileURL.size(); i++)
                                            if (i == 0)
                                                uploadImageToFirebase(ServiceID, ProfileURL.get(i), true, false);
                                            else {
                                                if (i == ProfileURL.size() - 1 && i > 0)
                                                    uploadImageToFirebase(ServiceID, ProfileURL.get(i), false, true);
                                                else
                                                    uploadImageToFirebase(ServiceID, ProfileURL.get(i), false, false);
                                            }
                                    }
                                } else {
                                    showLocation = false;
                                    finish();
                                }
                            }
                        });

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddServiceActivity.this, e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(AddServiceActivity.this, R.string.property_location_error,
                    Toast.LENGTH_LONG).show();
        }

    }


    private void uploadImageToFirebase(final String ID, Uri uri, final boolean isFirstPhoto, final boolean isLastPhoto) {
        final long millis = System.currentTimeMillis() % 1000;

        StorageReference ref = storageReference.child("ServicesImages").child(ID).child("Img" + millis);

        final Dialog dialog = new Dialog(AddServiceActivity.this);
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
                                .child("images").child("Img" + millis);
                        databaseRef.setValue(uri.toString());

                        DatabaseReference propertyRef = firebaseDatabase.getReference().getRoot().child("Furniture")
                                .child(CountryName).child(CityName)
                                .child(AllOffersNames.get(SelectedOfferPos))
                                .child(AllTypesNames.get(SelectedTypePos))
                                .child(ID);
                        DatabaseReference propertyRef2 = propertyRef.child("images").child("Img" + millis);
                        propertyRef2.setValue(uri.toString());

//                        if (!isNewService) {
//                            ImagesList.add(uri.toString());
//                            mAdapter.notifyDataSetChanged();
//                        }

                        if (isFirstPhoto) {
                            DatabaseReference databaseRef2 = firebaseDatabase.getReference().getRoot()
                                    .child("Providers").child(currentProvider.getBrokerID()).child("Furniture").child(ID)
                                    .child("image");
                            databaseRef2.setValue(uri.toString().toString());

                            currentProvider.setImage(uri.toString());
                            propertyRef.child("image").setValue(uri.toString());
                        }
                        Toast.makeText(AddServiceActivity.this, R.string.uploaded_success, Toast.LENGTH_SHORT).show();

                        if (isLastPhoto) {
                            showLocation = false;
                            finish();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();
                Toast.makeText(AddServiceActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                textView.setText(progress + getString(R.string.percent_uploaded));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            try {
                Uri uri = data.getData();
                ProfileURL.add(uri);
                mAdapterURI = new ServiceImages3Adapter(AddServiceActivity.this, ProfileURL);
                mRecyclerView.setAdapter(mAdapterURI);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddServiceActivity.this, getString(R.string.error_happened) + e.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAM_REQUREST) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photoURI = createImageFile(photo);
                if (photoURI != null) {
                    ProfileURL.add(photoURI);
                    mAdapterURI = new ServiceImages3Adapter(AddServiceActivity.this, ProfileURL);
                    mRecyclerView.setAdapter(mAdapterURI);
                } else {
                    Toast.makeText(AddServiceActivity.this, getString(R.string.error_happened),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddServiceActivity.this, getString(R.string.error_happened) + e.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showLocation = false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    void showCamera() {
        if (ActivityCompat.checkSelfPermission(AddServiceActivity.this,
                android.Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAM_REQUREST);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(AddServiceActivity.this, getString(R.string.location_permission_error),
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
