package com.mediaoasis.trvany.activities.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.adapters.SpinnerAdapter;
import com.mediaoasis.trvany.models.Furniture;
import com.mediaoasis.trvany.models.Order;
import com.mediaoasis.trvany.models.Provider;
import com.mediaoasis.trvany.models.Rating;
import com.mediaoasis.trvany.view.CustomRatingBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderDetailsActivity extends AppCompatActivity {

    public static boolean isLocationChanged = false;
    public static String PickupName, PickupAddress, PickupSign;
    public static double PickupLatitude, PickupLongitude;
    TextView status_tv, date_tv, time_tv, locationName_tv, locationAddress_tv, locationSign_tv, broker_tv,
            propertyTitle_tv, propertyDesc_tv;
    ImageView property_img;
    CircleImageView broker_img;
    CustomRatingBar rate_rb;
    CheckBox remind_cb;
    Spinner spinner;
    Button changeLocation_btn;
    LinearLayout date_ll, time_ll, spinnerReqDetails_ll;
    Order CurrentOrder;
    Furniture currentFurniture;
    Button save;
    FirebaseDatabase firebaseDatabase;
    String DateToDisplay = "", TimeToDisplay = "";

    List<String> ActionsList;
    SpinnerAdapter adapter;

    boolean isChangesHappened = false, isHistory = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

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

        Intent i = getIntent();
        CurrentOrder = i.getParcelableExtra("order");

        Bundle b = i.getExtras();
        isHistory = b.getBoolean("isHistory");
        save = (Button) findViewById(R.id.buttonOrderSaveEdit);


        status_tv = (TextView) findViewById(R.id.textOrderDetailsStatus);
        date_tv = (TextView) findViewById(R.id.textOrderDetailsDate);
        time_tv = (TextView) findViewById(R.id.textOrderDetailsTime);
        locationAddress_tv = (TextView) findViewById(R.id.textviewOrderDetailsAddress);
        locationName_tv = (TextView) findViewById(R.id.textviewOrderDetailsLocation);
        locationSign_tv = (TextView) findViewById(R.id.textviewOrderDetailsSign);
        broker_tv = (TextView) findViewById(R.id.textOrderDetailsBroker);
        propertyTitle_tv = (TextView) findViewById(R.id.textOrderDetailsPropertyTitle);
        propertyDesc_tv = (TextView) findViewById(R.id.textOrderDetailsPropertyDesc);

        rate_rb = (CustomRatingBar) findViewById(R.id.ratingOrderRate);
        remind_cb = (CheckBox) findViewById(R.id.checkboxOrderRemindMe);
        changeLocation_btn = (Button) findViewById(R.id.buttonChangeLocation);
        spinner = (Spinner) findViewById(R.id.spinnerActions);
        property_img = (ImageView) findViewById(R.id.imagePropertyImage);
        broker_img = (CircleImageView) findViewById(R.id.imageBrokerImage);

        date_ll = (LinearLayout) findViewById(R.id.linearOrderDate);
        time_ll = (LinearLayout) findViewById(R.id.linearOrderTime);
        spinnerReqDetails_ll = (LinearLayout) findViewById(R.id.spinnerReqDetails);

        rate_rb.setHalfStars(true);
        rate_rb.setClickable(false);
        rate_rb.setEnabled(false);
        rate_rb.setScrollToSelect(false);

        ActionsList = new ArrayList<>();
        ActionsList.add(getResources().getString(R.string.action));

        adapter = new SpinnerAdapter(OrderDetailsActivity.this, android.R.id.text1, ActionsList);
        spinner.setAdapter(adapter);

        TimeToDisplay = CurrentOrder.getTime();
        DateToDisplay = CurrentOrder.getDate();
        PickupAddress = CurrentOrder.getPickupAddress();
        PickupName = CurrentOrder.getPickupName();
        PickupSign = CurrentOrder.getPickupSign();
        PickupLatitude = CurrentOrder.getPickupLatitude();
        PickupLongitude = CurrentOrder.getPickupLongitude();

        changeLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailsActivity.this, PickupLocationActivity.class);
                intent.putExtra("fromOrderDetails", true);
                startActivity(intent);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference userOrderRef = firebaseDatabase.getReference().child("User").child(CurrentOrder.getUserID())
                        .child("Orders").child(CurrentOrder.getOrderID());

                userOrderRef.child("date").setValue(CurrentOrder.getDate()) .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        userOrderRef.child("time").setValue(CurrentOrder.getTime()) .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            DatabaseReference brokerOrderRef = firebaseDatabase.getReference().child("Providers")
                                    .child(CurrentOrder.getBrokerID()).child("Orders").child(CurrentOrder.getOrderID());
                            brokerOrderRef.child("date").setValue(CurrentOrder.getDate())
                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            brokerOrderRef.child("date").setValue(CurrentOrder.getDate())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                        Toast.makeText(OrderDetailsActivity.this, getResources().getString(R.string.data_saved),
                                                Toast.LENGTH_SHORT).show();
                                        finish();

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

        date_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();

            }
        });

        time_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String Action = ActionsList.get(i);

                if (Action.equals(getResources().getString(R.string.apply_changes))) {
                    if (isChangesHappened || isLocationChanged) {
                        showEditConfirmation();
                    } else {
                        spinner.setSelection(0);
                        Toast.makeText(OrderDetailsActivity.this, getResources().getString(R.string.no_changes_to_apply)
                                , Toast.LENGTH_SHORT).show();
                    }
                } else if (Action.equals(getString(R.string.cancel_appointment))) {
                    showCancelOrderConfirmation();
                } else if (Action.equals(getString(R.string.approve_order))) {
                    showApproveConfirmation();
                } else if (Action.equals(getString(R.string.mark_as_complete))) {
                    showDoneConfirmation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        date_tv.setText(DateToDisplay);
        time_tv.setText(TimeToDisplay);
        locationAddress_tv.setText(PickupAddress);
        locationName_tv.setText(PickupName);
        locationSign_tv.setText(PickupSign);
        broker_tv.setText(CurrentOrder.getBrokerName());

        if (CurrentOrder.getStatus().equals("edited by user")) {
            status_tv.setText(getResources().getString(R.string.pending_approval));
            status_tv.setTextColor(getResources().getColor(R.color.orange));
        } else if (CurrentOrder.getStatus().equals("edited by provider")) {
            status_tv.setText(R.string.waiting_your_approval);
            status_tv.setTextColor(getResources().getColor(R.color.orange));
        } else if (CurrentOrder.getStatus().equals("on request")) {
            status_tv.setText(getResources().getString(R.string.pending_approval));
            status_tv.setTextColor(getResources().getColor(R.color.orange));
        } else if (CurrentOrder.getStatus().equals("approved")) {
            status_tv.setText(R.string.approved);
            status_tv.setTextColor(getResources().getColor(R.color.green));
        } else if (CurrentOrder.getStatus().equals("done")) {
            status_tv.setText(R.string.done);
            status_tv.setTextColor(getResources().getColor(R.color.green));
        } else if (CurrentOrder.getStatus().equals("canceled by user")) {
            status_tv.setText(R.string.you_canceled_order);
            status_tv.setTextColor(getResources().getColor(R.color.gray_txt_body));
        } else if (CurrentOrder.getStatus().equals("canceled by provider")) {
            status_tv.setText(R.string.canceled_by_broker);
            status_tv.setTextColor(getResources().getColor(R.color.gray_txt_body));
        } else {
            status_tv.setText(CurrentOrder.getStatus());
            status_tv.setTextColor(getResources().getColor(R.color.green));
        }

        Picasso.with(OrderDetailsActivity.this).load(CurrentOrder.getBrokerImage()).fit()
                .error(R.drawable.profile_placeholder)
                .placeholder(R.drawable.profile_placeholder).into(broker_img);

        getPropertyData();

        if (isHistory) {
            setToNoActionMode();
        } else {
            if (CurrentOrder.getStatus().equals("on request")) {
                ActionsList.add(getResources().getString(R.string.apply_changes));
                ActionsList.add(getResources().getString(R.string.cancel_appointment));
                ActionsList.add(getResources().getString(R.string.mark_as_complete));
                adapter.notifyDataSetChanged();


            } else if (CurrentOrder.getStatus().equals("approved")) {
                ActionsList.add(getResources().getString(R.string.apply_changes));
                ActionsList.add(getResources().getString(R.string.cancel_appointment));
                ActionsList.add(getResources().getString(R.string.mark_as_complete));
                adapter.notifyDataSetChanged();

            } else if (CurrentOrder.getStatus().equals("edited by user")) {
                ActionsList.add(getResources().getString(R.string.apply_changes));
                ActionsList.add(getResources().getString(R.string.cancel_appointment));
                ActionsList.add(getResources().getString(R.string.mark_as_complete));
                adapter.notifyDataSetChanged();

            } else if (CurrentOrder.getStatus().equals("edited by provider")) {
                ActionsList.add(getResources().getString(R.string.approve_order));
                ActionsList.add(getResources().getString(R.string.apply_changes));
                ActionsList.add(getResources().getString(R.string.cancel_appointment));
                ActionsList.add(getResources().getString(R.string.mark_as_complete));
                adapter.notifyDataSetChanged();
            } else if (CurrentOrder.getStatus().equals("done")) {
                setToNoActionMode();
            }

        }
    }

    private void getPropertyData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("Providers")
                .child(CurrentOrder.getBrokerID()).child("Furniture");
        databaseReference.child(CurrentOrder.getPropertyID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFurniture = dataSnapshot.getValue(Furniture.class);

                propertyTitle_tv.setText(currentFurniture.getTitle());
                propertyDesc_tv.setText(currentFurniture.getDescription());
                try {
                    Picasso.with(OrderDetailsActivity.this).load(currentFurniture.getImage()).fit()
                            .error(R.drawable.property_placeholder)
                            .placeholder(R.drawable.property_placeholder).into(property_img);
                } catch (Exception e) {
                    e.printStackTrace();
                    property_img.setBackgroundResource(R.drawable.property_placeholder);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void setToNoActionMode() {
        date_ll.setClickable(false);
        time_ll.setClickable(false);
        remind_cb.setVisibility(View.GONE);
        spinnerReqDetails_ll.setVisibility(View.GONE);
    }

    void showCancelOrderConfirmation() {
        AlertDialog alertDialog = new AlertDialog.Builder(OrderDetailsActivity.this).create();
        alertDialog.setTitle(getResources().getString(R.string.cancel_appointment));
        alertDialog.setMessage(getResources().getString(R.string.cancel_request_question));

        // Setting OK Button
        alertDialog.setButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cancelOrder();
            }
        });

        alertDialog.setButton2(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    void cancelOrder() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userOrderRef = firebaseDatabase.getReference().child("User").child(CurrentOrder.getUserID())
                .child("Orders").child(CurrentOrder.getOrderID()).child("status");
        userOrderRef.setValue("user_cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    DatabaseReference brokerOrderRef = firebaseDatabase.getReference().child("Providers")
                            .child(CurrentOrder.getBrokerID()).child("Orders").child(CurrentOrder.getOrderID()).child("status");
                    brokerOrderRef.setValue("user_cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(OrderDetailsActivity.this, getResources().getString(R.string.order_canceled),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OrderDetailsActivity.this, getResources().getString(R.string.order_cancel_failed),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else {
                    Toast.makeText(OrderDetailsActivity.this, getResources().getString(R.string.order_cancel_failed),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    void showEditConfirmation() {
        AlertDialog alertDialog = new AlertDialog.Builder(OrderDetailsActivity.this).create();
        alertDialog.setTitle(getString(R.string.edit_appointment));
        alertDialog.setMessage(getString(R.string.edit_appointment_question));

        // Setting OK Button
        alertDialog.setButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                editOrder();
            }
        });

        alertDialog.setButton2(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    void editOrder() {
        CurrentOrder.setStatus("edited by user");

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userOrderRef = firebaseDatabase.getReference().child("User").child(CurrentOrder.getUserID())
                .child("Orders").child(CurrentOrder.getOrderID());
        userOrderRef.setValue(CurrentOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    DatabaseReference brokerOrderRef = firebaseDatabase.getReference().child("Providers")
                            .child(CurrentOrder.getBrokerID()).child("Orders").child(CurrentOrder.getOrderID());
                    brokerOrderRef.setValue(CurrentOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                showWaitApprovalDialog();
//                                Toast.makeText(OrderDetailsActivity.this, "Appointment is edited Successfully!",
//                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OrderDetailsActivity.this, "Edit is Failed!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else {
                    Toast.makeText(OrderDetailsActivity.this, "Edit is Failed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showWaitApprovalDialog() {
        final Dialog dialog = new Dialog(OrderDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_wait_approval);

        Button done_btn = (Button) dialog.findViewById(R.id.buttonDialogWaitApproval);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showApproveConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetailsActivity.this);
        alertDialogBuilder.setTitle("Approve Order");
        alertDialogBuilder
                .setMessage("Do you want to approve this request?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        approveOrder();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void approveOrder() {
        CurrentOrder.setStatus("approved");

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(CurrentOrder.getUserID())
                .child("Orders").child(CurrentOrder.getOrderID()).child("status");

        userRef.setValue("approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DatabaseReference brokerRef = firebaseDatabase.getReference().child("Providers")
                            .child(CurrentOrder.getBrokerID()).child("Orders").child(CurrentOrder.getOrderID()).child("status");

                    brokerRef.setValue("approved")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(OrderDetailsActivity.this, "Order Approved Successfully..",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(OrderDetailsActivity.this,
                                                "Failed to Request Order, Try Again!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                } else {
                    Toast.makeText(OrderDetailsActivity.this,
                            "Failed to Request Order, Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDoneConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetailsActivity.this);
        alertDialogBuilder.setTitle(R.string.order_is_done);
        alertDialogBuilder
                .setMessage(R.string.order_complete_question)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doneOrder();
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
    }

    private void doneOrder() {
        CurrentOrder.setStatus("done");

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(CurrentOrder.getUserID())
                .child("Orders").child(CurrentOrder.getOrderID()).child("status");

        userRef.setValue("done").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DatabaseReference brokerRef = firebaseDatabase.getReference().child("Providers")
                            .child(CurrentOrder.getBrokerID()).child("Orders").child(CurrentOrder.getOrderID()).child("status");

                    brokerRef.setValue("done")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(OrderDetailsActivity.this, R.string.order_is_complete,
                                                Toast.LENGTH_SHORT).show();
                                        showRateDialog();
                                    } else {
                                        Toast.makeText(OrderDetailsActivity.this,
                                                R.string.order_complete_failed, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                } else {
                    Toast.makeText(OrderDetailsActivity.this,
                            R.string.order_complete_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showRateDialog() {
        final Dialog dialog = new Dialog(OrderDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_rate_broker);

        final float[] UserScore = {0};
        CircleImageView brokerImg = (CircleImageView) dialog.findViewById(R.id.imgDialogRate);
        TextView brokerName_tv = (TextView) dialog.findViewById(R.id.textRateBrokerName);
        final CustomRatingBar ratingBar = (CustomRatingBar) dialog.findViewById(R.id.rateDialogRate);
        final EditText review_et = (EditText) dialog.findViewById(R.id.edittextDialogRate);
        Button done_btn = (Button) dialog.findViewById(R.id.buttonDialogRateDone);
        Button cancel_btn = (Button) dialog.findViewById(R.id.buttonDialogRateCancel);

        brokerName_tv.setText(CurrentOrder.getBrokerName());
        Picasso.with(OrderDetailsActivity.this).load(CurrentOrder.getBrokerImage()).fit().into(brokerImg);

        ratingBar.setOnScoreChanged(new CustomRatingBar.IRatingBarCallbacks() {
            @Override
            public void scoreChanged(float score) {
                UserScore[0] = score;
                Toast.makeText(OrderDetailsActivity.this, getString(R.string.score) + score, Toast.LENGTH_SHORT).show();
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserScore[0] > 0) {
                    if (review_et.getText().toString().length() > 0) {
                        showRatingProgressDialog(review_et.getText().toString(), UserScore[0]);

                        dialog.dismiss();
                    } else {
                        review_et.setError(getString(R.string.review_empty));
                    }
                } else {
                    Toast.makeText(OrderDetailsActivity.this, R.string.you_didnot_rate_broker, Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showRatingProgressDialog(final String review, final float rate) {
        final ProgressDialog progressDialog = new ProgressDialog(OrderDetailsActivity.this);
        progressDialog.setMessage(getString(R.string.posting_rate));
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("Providers")
                .child(CurrentOrder.getBrokerID());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Provider provider = dataSnapshot.getValue(Provider.class);

                // calculate new rate
                float temp = provider.getNumberOfRates() * provider.getRate();
                float newTemp = temp + rate;
                int newNumOfRates = provider.getNumberOfRates() + 1;
                float newRate = newTemp / newNumOfRates;

                Rating rating = new Rating();
                rating.setUserID(CurrentOrder.getUserID());
                rating.setUserName(CurrentOrder.getUserName());
                rating.setUserImage(CurrentOrder.getUserImage());
                rating.setReview(review);
                rating.setRate(rate);

                DatabaseReference reference = firebaseDatabase.getReference().getRoot().child("Providers")
                        .child(CurrentOrder.getBrokerID());
                reference.child("numberOfRates").setValue(newNumOfRates);
                reference.child("rate").setValue(newRate);
                reference.child("Reviews").push().setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void showDatePickerDialog() {
        final Dialog dialog = new Dialog(OrderDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_date_picker);

        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePickerDialogSchedule);

        LinearLayout done_ll = (LinearLayout) dialog.findViewById(R.id.linearScheduleDialogDone);
        done_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = datePicker.getMonth() + 1;
                DateToDisplay = datePicker.getDayOfMonth() + "/" + month + "/" + datePicker.getYear();
                date_tv.setText(DateToDisplay);
                if (!DateToDisplay.equals(CurrentOrder.getDate())) {
                    CurrentOrder.setDate(DateToDisplay);
                    isChangesHappened = true;
                }
                dialog.dismiss();
//                Toast.makeText(ScheduleProviderActivity.this, ""DateToDisplay, Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
    }

    private void showTimePickerDialog() {
        final Dialog dialog = new Dialog(OrderDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_time_picker);

        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.TimePickerDialogSchedule);
        LinearLayout done_ll = (LinearLayout) dialog.findViewById(R.id.linearTimeScheduleDialogDone);
        done_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int h = timePicker.getCurrentHour();
                int m = timePicker.getCurrentMinute();
//                int t = timePicker.get
                TimeToDisplay = h + ":" + m;
                time_tv.setText(TimeToDisplay);
                if (!TimeToDisplay.equals(CurrentOrder.getTime())) {
                    CurrentOrder.setTime(TimeToDisplay);
                    isChangesHappened = true;
//                    save_ll.setVisibility(View.VISIBLE);
//                    ActionsList.add("Apply Changes");
                }
                dialog.dismiss();
//                Toast.makeText(ScheduleProviderActivity.this, TimeToDisplay, Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
