package com.mediaoasis.trvany.activities.user;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.adapters.ServicesAdapter;
import com.mediaoasis.trvany.adapters.SpinnerAdapter;
import com.mediaoasis.trvany.models.Area;
import com.mediaoasis.trvany.models.City;
import com.mediaoasis.trvany.models.Furniture;
import com.mediaoasis.trvany.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.mediaoasis.trvany.activities.user.MainActivity.AllCities;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCitiesNames;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCitiesNamesAr;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCitiesNamesTr;
import static com.mediaoasis.trvany.activities.user.MainActivity.AllCountriesNames;


public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    LinearLayout mapViewLayout, listViewLayout, filterLayout, mapTabLayout, listTabLayout;
    //    Button search;
    GoogleMap googleMap;
    TextView num_of_results_tv;
    SupportMapFragment supportMapFragment;

    List<Area> Areas;
    List<Furniture> allFurnitures, filteredFurnitures;
    List<Marker> AllServicesMarkers;
    int SelectedCityPosition = 0, SelectedCountryPosition = 0, SelectedTypePos = 0, SelectedOfferPos = 0,
            SelectedPriceRangePos = 0, SelectedAreaPos = 0;
    int city, country, type, offer;
    //    List<City> AllCities;
    List<String> AllAreas, AllAreasAr, AllAreasTr, AllRentPriceRange, AllSellPriceRange;
    SpinnerAdapter CountriesAdapter, CitiesAdapter, TypesAdapter, OffersAdapter, PriceRangeAdapter, AreasAdapter;
    // firebase
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    boolean isMapReady = false, isSellOffer = false, isAreasLoaded = false;

    boolean isImagesLoaded = false;
    String /*Type = "All Types", Offer = "All Offers",*/ PriceRange = "All Ranges";
    ProgressBar areaProgressBar;
    Spinner area_sp;
    SharedPref sharedPref;
    //    boolean isCitiesLoaded = false;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ServicesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        LinearLayout back_btn = (LinearLayout) findViewById(R.id.toolbarBack);
//        back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        sharedPref = new SharedPref(SearchActivity.this);

        initializeLists();
        firebaseDatabase = FirebaseDatabase.getInstance();

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(SearchActivity.this);

        mapViewLayout = (LinearLayout) findViewById(R.id.LinearMapView);
        listViewLayout = (LinearLayout) findViewById(R.id.LinearListView);
        filterLayout = (LinearLayout) findViewById(R.id.textviewSearchFilters);
        mapTabLayout = (LinearLayout) findViewById(R.id.LinearMapViewTab);
        listTabLayout = (LinearLayout) findViewById(R.id.LinearListViewTab);
        num_of_results_tv = (TextView) findViewById(R.id.textviewResultsCount);

        Bundle b = getIntent().getExtras();
        if (b.getBoolean("isAdv")) {
            showAdvancedSearchDialog();
        } else {
            country = b.getInt("country");
            city = b.getInt("city");
            type = b.getInt("type");
            offer = b.getInt("offer");

            getServices();
//            getAreas();

        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ServicesAdapter(SearchActivity.this, filteredFurnitures, false);
        mRecyclerView.setAdapter(mAdapter);

        mapTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapViewLayout.setVisibility(View.VISIBLE);
                listViewLayout.setVisibility(View.INVISIBLE);
                mapTabLayout.setBackgroundColor(getResources().getColor(R.color.gray_bg));
                listTabLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        listTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapViewLayout.setVisibility(View.INVISIBLE);
                listViewLayout.setVisibility(View.VISIBLE);
                mapTabLayout.setBackgroundColor(getResources().getColor(R.color.white));
                listTabLayout.setBackgroundColor(getResources().getColor(R.color.gray_bg));
            }
        });

        filterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAdvancedSearchDialog();
            }
        });

    }

    private void getServices() {
        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        databaseReference = firebaseDatabase.getReference().getRoot().child("Furniture")
                .child(MainActivity.AllCountries.get(country).getName())
                .child(AllCities.get(city).getName())
                .child(MainActivity.AllOffersNames.get(offer))
                .child(MainActivity.AllTypesNames.get(type));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Furniture furniture;
                int i = 0;
//                HashMap<String, Furniture> Services = new HashMap<String, Furniture>();
                for (DataSnapshot child : children) {
//                    Services.put(child.getKey(), child.getValue(Furniture.class));
                    furniture = child.getValue(Furniture.class);
                    furniture.setPropertyID(child.getKey());
//                    Services.get(i).setCountry(MainActivity.AllCountries.get(country).getName());
//                    Services.get(i).setCityName(AllCities.get(city).getName());
//                    Services.get(i).setOffer(MainActivity.AllOffers.get(offer));
//                    Services.get(i).setType(MainActivity.AllTypes.get(type));
                    furniture.setCountry(MainActivity.AllCountries.get(country).getName());
                    furniture.setCityName(AllCities.get(city).getName());
                    furniture.setOffer(MainActivity.AllOffersNames.get(offer));
                    furniture.setType(MainActivity.AllTypesNames.get(type));

//                    allFurnitures.add(Services.get(i));
//                    filteredFurnitures.add(Services.get(i));
                    allFurnitures.add(furniture);
                    filteredFurnitures.add(furniture);
                }
                num_of_results_tv.setText(filteredFurnitures.size() + " " + getString(R.string.results_found));
                mAdapter = new ServicesAdapter(SearchActivity.this, filteredFurnitures, false);
                mRecyclerView.setAdapter(mAdapter);
                if (filteredFurnitures.size() > 0) {
                    showFilteredServicesMarkersOnMap();
                }
//                else
//                    Toast.makeText(SearchActivity.this, " " + R.string.no_results_found, Toast.LENGTH_LONG).show();

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void initializeLists() {

        allFurnitures = new ArrayList<>();
        filteredFurnitures = new ArrayList<>();
        AllServicesMarkers = new ArrayList<>();
        Areas = new ArrayList<>();
        AllAreas = new ArrayList<>();
        AllAreasAr = new ArrayList<>();
        AllAreasTr = new ArrayList<>();

        AllRentPriceRange = new ArrayList<>();
        AllSellPriceRange = new ArrayList<>();

        AllRentPriceRange.add(PriceRange);
        AllRentPriceRange.add("< 1000");
        AllRentPriceRange.add("1001 - 2000");
        AllRentPriceRange.add("2001 - 4000");
        AllRentPriceRange.add("> 4000");

        AllSellPriceRange.add(PriceRange);
        AllSellPriceRange.add("< 100,000");
        AllSellPriceRange.add("100,001 - 200,000");
        AllSellPriceRange.add("200,001 - 400,000");
        AllSellPriceRange.add("400,001 - 700,000");
        AllSellPriceRange.add("700,001 - 1,000,000");
        AllSellPriceRange.add("> 1,000,000");

        if (sharedPref.getLanguage().equals("en")) {
            CountriesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllCountriesNames);
            CitiesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllCitiesNames);
            TypesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, MainActivity.AllTypes);
            OffersAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, MainActivity.AllOffers);
        } else if (sharedPref.getLanguage().equals("ar")) {
            CountriesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, MainActivity.AllCountriesNamesAr);
            CitiesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllCitiesNamesAr);
            TypesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, MainActivity.AllTypesAR);
            OffersAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, MainActivity.AllOffersAR);
        } else if (sharedPref.getLanguage().equals("tr")) {
            CountriesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, MainActivity.AllCountriesNamesTr);
            CitiesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllCitiesNamesTr);
            TypesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, MainActivity.AllTypesTR);
            OffersAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, MainActivity.AllOffersTR);
        }
        PriceRangeAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllRentPriceRange);
    }

    void getCities(final Spinner city_sp) {
//        isCitiesLoaded = false;
        DatabaseReference citiesRef = firebaseDatabase.getReference().getRoot().child("Cities")
                .child(AllCountriesNames.get(SelectedCountryPosition));
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
                if (sharedPref.getLanguage().equals("en"))
                    CitiesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllCitiesNames);
                else if (sharedPref.getLanguage().equals("ar"))
                    CitiesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllCitiesNamesAr);
                else if (sharedPref.getLanguage().equals("tr"))
                    CitiesAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllCitiesNamesTr);
                city_sp.setAdapter(CitiesAdapter);
//                isCitiesLoaded = true;
                getAreas();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getAreas() {

        DatabaseReference areaRef = firebaseDatabase.getReference().getRoot().child("Areas")
                .child(AllCitiesNames.get(SelectedCityPosition));
        areaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Areas.clear();
                AllAreasAr.clear();
                AllAreas.clear();
                AllAreasTr.clear();

                Area allAreas = new Area();
                allAreas.setName("All Areas");
                allAreas.setNameAr("كل المناطق");
                allAreas.setNameTr("Bütün Alanlar");
                Areas.add(allAreas);

                AllAreasAr.add("كل المناطق");
                AllAreas.add("All Areas");
                AllAreasTr.add("Bütün Alanlar");

                Area area;
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    area = child.getValue(Area.class);
                    Areas.add(area);
                    AllAreas.add(area.getName());
                    AllAreasTr.add(area.getNameTr());
                    AllAreasAr.add(area.getNameAr());

                }
                if (isAreasLoaded)
                    loadAreaInDialog(area_sp, areaProgressBar);
                else
                    isAreasLoaded = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        }
    }

    private void showAdvancedSearchDialog() {
        final Dialog dialog = new Dialog(SearchActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search_filters);
        dialog.setCancelable(false);

        Button search_btn, close_btn;
        final Spinner country_sp, city_sp, type_sp, offer_sp, priceRange_sp;

        country_sp = (Spinner) dialog.findViewById(R.id.spinnerFilterCountry);
        city_sp = (Spinner) dialog.findViewById(R.id.spinnerFilterCities);
        type_sp = (Spinner) dialog.findViewById(R.id.spinnerFilterPropertyType);
        offer_sp = (Spinner) dialog.findViewById(R.id.spinnerFilterOffer);
        priceRange_sp = (Spinner) dialog.findViewById(R.id.spinnerFilterPriceRange);
        area_sp = (Spinner) dialog.findViewById(R.id.spinnerFilterArea);

        areaProgressBar = (ProgressBar) dialog.findViewById(R.id.prog);
        search_btn = (Button) dialog.findViewById(R.id.butttonFilterSearch);
        close_btn = (Button) dialog.findViewById(R.id.butttonFilterClose);

        country_sp.setAdapter(CountriesAdapter);
        city_sp.setAdapter(CitiesAdapter);
        type_sp.setAdapter(TypesAdapter);
        offer_sp.setAdapter(OffersAdapter);
        priceRange_sp.setAdapter(PriceRangeAdapter);

        country_sp.setSelection(country);
        city_sp.setSelection(city);
        offer_sp.setSelection(offer);
        type_sp.setSelection(type);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        priceRange_sp.setSelection(SelectedPriceRangePos);
//        area_sp.setSelection(SelectedAreaPos);
//        getAreas();

        loadAreaInDialog(area_sp, areaProgressBar);

        country_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedCountryPosition = i;
                SelectedCityPosition = 0;
                city_sp.setSelection(0);
                getCities(city_sp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        city_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedCityPosition = i;
                SelectedAreaPos = 0;
//                area_sp.setSelection(SelectedAreaPos);
//                isCitiesLoaded = true;
                isAreasLoaded = false;
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
                if (i == 1) {
                    PriceRangeAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllRentPriceRange);
                    priceRange_sp.setAdapter(PriceRangeAdapter);
                    isSellOffer = false;
                } else {
                    PriceRangeAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllSellPriceRange);
                    priceRange_sp.setAdapter(PriceRangeAdapter);
                    isSellOffer = true;
                }
                SelectedPriceRangePos = 0;
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

        priceRange_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSellOffer)
                    PriceRange = AllSellPriceRange.get(i);
                else
                    PriceRange = AllRentPriceRange.get(i);
                SelectedPriceRangePos = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        area_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedAreaPos = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (country != SelectedCountryPosition || city != SelectedCityPosition || type != SelectedTypePos
                        || offer != SelectedOfferPos) {
                    allFurnitures.clear();
                    filteredFurnitures.clear();

                    for (int i = 0; i < filteredFurnitures.size(); i++) {
                        AllServicesMarkers.get(i).remove();
                    }
                    AllServicesMarkers.clear();
                    googleMap.clear();

                    country = SelectedCountryPosition;
                    city = SelectedCityPosition;
                    type = SelectedTypePos;
                    offer = SelectedOfferPos;

                    getServices();
                } else {
                    filteredFurnitures.clear();
                    for (int i = 0; i < filteredFurnitures.size(); i++) {
                        AllServicesMarkers.get(i).remove();
                    }
                    AllServicesMarkers.clear();
                    googleMap.clear();

                    filterData();
                    // temp
//                    mAdapter.notifyDataSetChanged();
//                    mAdapter = new ServicesAdapter(SearchActivity.this, filteredFurnitures);
//                    mRecyclerView.setAdapter(mAdapter);
                    // end-temp
                }

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void filterData() {
        Furniture furniture;
        boolean isAreaFilterPassed = false, isPriceFilterPassed = false;
        for (int i = 0; i < allFurnitures.size(); i++) {
            furniture = allFurnitures.get(i);

            if (SelectedAreaPos != 0) {
                String area = AllAreas.get(SelectedAreaPos);
                if (furniture.getArea().equals(area)) {
                    isAreaFilterPassed = true;
                }
            } else
                isAreaFilterPassed = true;

            if (isAreaFilterPassed) {

                if (SelectedPriceRangePos == 0) {
                    isPriceFilterPassed = true;
                } else {
                    long price = furniture.getPrice();
                    if (isSellOffer) {
                        String PropertyPriceRange = "";
                        if (price <= 100000)
                            PropertyPriceRange = AllSellPriceRange.get(1);
                        else if (price > 100000 & price <= 200000)
                            PropertyPriceRange = AllSellPriceRange.get(2);
                        else if (price > 200000 & price <= 400000)
                            PropertyPriceRange = AllSellPriceRange.get(3);
                        else if (price > 400000 & price <= 700000)
                            PropertyPriceRange = AllSellPriceRange.get(4);
                        else if (price > 700000 & price <= 1000000)
                            PropertyPriceRange = AllSellPriceRange.get(5);
                        else if (price > 1000000)
                            PropertyPriceRange = AllSellPriceRange.get(6);

//                        furniture.setPriceRange(PropertyPriceRange);

//                        if (furniture.getPriceRange().equals(AllSellPriceRange.get(SelectedPriceRangePos)))
//                            isPriceFilterPassed = true;

                    } else {
                        String PropertyPriceRange = "";
                        if (price <= 1000)
                            PropertyPriceRange = AllRentPriceRange.get(1);
                        else if (price > 1000 & price <= 2000)
                            PropertyPriceRange = AllRentPriceRange.get(2);
                        else if (price > 2000 & price <= 4000)
                            PropertyPriceRange = AllRentPriceRange.get(3);
                        else if (price > 4000)
                            PropertyPriceRange = AllRentPriceRange.get(4);

//                        furniture.setPriceRange(PropertyPriceRange);

//                        for (int j = 0; j < AllRentPriceRange.size(); j++)
//                            if (furniture.getPriceRange().equals(AllRentPriceRange.get(j)))
//                                isPriceFilterPassed = true;
                    }
                }

            }
            if (isPriceFilterPassed && isAreaFilterPassed) {
                filteredFurnitures.add(furniture);
            }
        }
        mAdapter = new ServicesAdapter(SearchActivity.this, filteredFurnitures, false);
        mRecyclerView.setAdapter(mAdapter);
        if (filteredFurnitures.size() > 0) {
            showFilteredServicesMarkersOnMap();
        }
//        else
//            Toast.makeText(SearchActivity.this, R.string.no_results_found, Toast.LENGTH_LONG).show();
    }

    private void loadAreaInDialog(Spinner area_sp, ProgressBar areaProgressBar) {
        if (isAreasLoaded) {
            if (sharedPref.getLanguage().equals("en"))
                AreasAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllAreas);
            else if (sharedPref.getLanguage().equals("ar"))
                AreasAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllAreasAr);
            else if (sharedPref.getLanguage().equals("tr"))
                AreasAdapter = new SpinnerAdapter(SearchActivity.this, android.R.id.text1, AllAreasTr);
            area_sp.setAdapter(AreasAdapter);
            areaProgressBar.setVisibility(View.INVISIBLE);
            area_sp.setVisibility(View.VISIBLE);
            area_sp.setSelection(SelectedAreaPos);
        } else
            isAreasLoaded = true;
    }

    private void showFilteredServicesMarkersOnMap() {
        for (int i = 0; i < filteredFurnitures.size(); i++) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(filteredFurnitures.get(i).getLatitude(), filteredFurnitures.get(i).getLongitude()))
                    .title(filteredFurnitures.get(i).getAddress())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue)));
            AllServicesMarkers.add(marker);
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng currentPin = marker.getPosition();

                for (int j = 0; j < allFurnitures.size(); j++) {
                    double valueLat = allFurnitures.get(j).getLatitude();
                    double valueLng = allFurnitures.get(j).getLongitude();
                    if (currentPin.latitude == valueLat && currentPin.longitude == valueLng) {
//                        Toast.makeText(SearchActivity.this, allFurnitures.get(j).getArea() + ", " +
//                                allFurnitures.get(j).getType() + ", " +
//                                allFurnitures.get(j).getOffer(), Toast.LENGTH_SHORT).show();
                        showPropertyDialog(allFurnitures.get(j));
                        break;
                    }
                }
                return true;
            }
        });

        if (isMapReady)
            zoomOnAllMarkers();
        else
            isMapReady = true;
    }

    private void showPropertyDialog(final Furniture furniture) {
        final Dialog dialog = new Dialog(SearchActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_property_details);


        TextView title = (TextView) dialog.findViewById(R.id.textDialogPropertyTitle);
        TextView desc = (TextView) dialog.findViewById(R.id.textDialogPropertyDesc);
        Button dialogButton = (Button) dialog.findViewById(R.id.buttonDialogProperty);
        final ImageView img = (ImageView) dialog.findViewById(R.id.imageDialogProperty);

        title.setText(furniture.getTitle());
        desc.setText(furniture.getDescription());
        Picasso.with(SearchActivity.this).load(furniture.getImage()).into(img);

//        DatabaseReference imgRef = databaseReference.child(furniture.getPropertyID()).child("Images");
//        imgRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//                HashMap<String, String> Images = new HashMap();
//
//                for (DataSnapshot child : children) {
//                    Images.put(child.getKey(), child.getValue(String.class));
//                }
//                if (Images.size() > 0) {
//                    furniture.setImages(Images);
//                    Map.Entry<String, String> entry = Images.entrySet().iterator().next();
//                    Picasso.with(SearchActivity.this).load(entry.getValue()).into(img);
//                }
//                if (isImagesLoaded) {
//                    Intent intent = new Intent(SearchActivity.this, ServiceDetailsActivity.class);
//                    intent.putExtra("furniture", furniture);
//                    startActivity(intent);
//                } else {
//                    isImagesLoaded = true;
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isImagesLoaded) {
                    Intent intent = new Intent(SearchActivity.this, ServiceDetailsActivity.class);
                    intent.putExtra("furniture", furniture);
                    startActivity(intent);
                } else {
                    isImagesLoaded = true;
                }
            }
        });

        dialog.show();
    }

    public void zoomOnAllMarkers() {
//        if (isMapReady) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : AllServicesMarkers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 7; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);
//    } else
//            isMapReady = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMaxZoomPreference(16);

        if (isMapReady)
            zoomOnAllMarkers();
        else
            isMapReady = true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
