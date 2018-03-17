package com.mediaoasis.trvany.activities.provider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mediaoasis.trvany.R;
import com.mediaoasis.trvany.activities.user.ChatActivity;
import com.mediaoasis.trvany.activities.user.MainActivity;
import com.mediaoasis.trvany.adapters.SpinnerAdapter;
import com.mediaoasis.trvany.models.Conversation;
import com.mediaoasis.trvany.models.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.mediaoasis.trvany.activities.provider.Main2Activity.currentProvider;


public class RequestDetailsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int CALL_REQUEST_CODE = 200;
    String[] permsCall = new String[]{"android.permission.CALL_PHONE"};
    TextView clientName_tv, pickupLocation_tv, pickupSign_tv, propertyTitle_tv, orderPrice_tv, status_tv, date_tv, time_tv;
    Button callClient_btn, chatClient_btn, changeDate_btn, changeTime_btn;
    ImageView propertyImg;
    LinearLayout spinnerReqDetails_ll;
    CircleImageView clientImg;
    CheckBox checkBox;
    Spinner spinner;

    Order CurrentOrder;
    String DateToDisplay = "", TimeToDisplay = "";

    List<String> ActionsList;
    SpinnerAdapter adapter;
    boolean isChangesHappened = false, isHistory = false;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
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

        firebaseDatabase = FirebaseDatabase.getInstance();

        Intent i = getIntent();
        CurrentOrder = i.getParcelableExtra("order");

        Bundle b = i.getExtras();
        isHistory = b.getBoolean("isHistory");

        clientName_tv = (TextView) findViewById(R.id.textClientName);
        pickupLocation_tv = (TextView) findViewById(R.id.textPickupLocation);
        pickupSign_tv = (TextView) findViewById(R.id.textPickupLocationSign);
        propertyTitle_tv = (TextView) findViewById(R.id.textPropertyTitle);
//        propertyLocation_tv = (TextView) findViewById(R.id.txtPropertyLocation);
        orderPrice_tv = (TextView) findViewById(R.id.textOrderPrice);
        date_tv = (TextView) findViewById(R.id.textOrderDetailsDate);
        time_tv = (TextView) findViewById(R.id.textOrderDetailsTime);
        status_tv = (TextView) findViewById(R.id.textReqDetailsStatus);

        callClient_btn = (Button) findViewById(R.id.buttonCallBroker);
        chatClient_btn = (Button) findViewById(R.id.buttonMsgBroker);
        changeDate_btn = (Button) findViewById(R.id.buttonChangeDate);
        changeTime_btn = (Button) findViewById(R.id.buttonChangeTime);

        propertyImg = (ImageView) findViewById(R.id.imgPropertyImage);
        clientImg = (CircleImageView) findViewById(R.id.imgClient);
        checkBox = (CheckBox) findViewById(R.id.checkboxOrderRemindMe);
        spinner = (Spinner) findViewById(R.id.spinnerActions);
        spinnerReqDetails_ll = (LinearLayout) findViewById(R.id.spinnerReqDetails);

        clientName_tv.setText(getString(R.string.client_name) + CurrentOrder.getUserName());
        pickupLocation_tv.setText(CurrentOrder.getPickupName() + " - " + CurrentOrder.getPickupAddress());
        pickupSign_tv.setText(getString(R.string.pickup_sign) + CurrentOrder.getPickupSign());
        propertyTitle_tv.setText(CurrentOrder.getPropertyTitle());
//        propertyLocation_tv.setText(CurrentOrder.property());
        orderPrice_tv.setText(CurrentOrder.getFees());
        date_tv.setText(CurrentOrder.getDate());
        time_tv.setText(CurrentOrder.getTime());


        if (CurrentOrder.getStatus().equals("edited by user")) {
            status_tv.setText(R.string.waiting_your_approval);
            status_tv.setTextColor(getResources().getColor(R.color.orange));
        } else if (CurrentOrder.getStatus().equals("edited by broker")) {
            status_tv.setText(getResources().getString(R.string.pending_approval));
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
            status_tv.setText(R.string.canceled_by_user);
            status_tv.setTextColor(getResources().getColor(R.color.gray_txt_body));
        } else if (CurrentOrder.getStatus().equals("canceled by broker")) {
            status_tv.setText(R.string.you_canceled_order);
            status_tv.setTextColor(getResources().getColor(R.color.gray_txt_body));
        } else {
            status_tv.setText(CurrentOrder.getStatus());
            status_tv.setTextColor(getResources().getColor(R.color.green));
        }

        Picasso.with(RequestDetailsActivity.this).load(CurrentOrder.getUserImage()).into(clientImg);
        Picasso.with(RequestDetailsActivity.this).load(CurrentOrder.getPropertyImage()).into(propertyImg);

        ActionsList = new ArrayList<>();
        ActionsList.add(getString(R.string.action));

        adapter = new SpinnerAdapter(RequestDetailsActivity.this, android.R.id.text1, ActionsList);
        spinner.setAdapter(adapter);

        if (isHistory) {
            setToNoActionMode();
        } else {
            if (CurrentOrder.getStatus().equals("on request")) {
                ActionsList.add("Approve Order");
                ActionsList.add("Apply Changes");
                ActionsList.add("Cancel Appointment");
                adapter.notifyDataSetChanged();


            } else if (CurrentOrder.getStatus().equals("approved")) {
                ActionsList.add("Apply Changes");
                ActionsList.add("Cancel Order");
                adapter.notifyDataSetChanged();

            } else if (CurrentOrder.getStatus().equals("edited by user")) {
                ActionsList.add("Apply Changes");
                ActionsList.add("Approve Order");
                ActionsList.add("Cancel Order");
                adapter.notifyDataSetChanged();

            } else if (CurrentOrder.getStatus().equals("edited by broker")) {
                setToNoActionMode();
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String Action = ActionsList.get(i);

                if (Action.equals("Apply Changes")) {
                    if (isChangesHappened) {
                        showEditConfirmation();
                    } else {
                        spinner.setSelection(0);
                        Toast.makeText(RequestDetailsActivity.this, R.string.no_changes_to_apply, Toast.LENGTH_SHORT).show();
                    }
                } else if (Action.equals("Cancel Order")) {
                    showCancelConfirmation();
                } else if (Action.equals("Approve Order")) {
                    showApproveConfirmation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        callClient_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBroker();
            }
        });

        changeDate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(RequestDetailsActivity.this, "Not Implemented YET !", Toast.LENGTH_SHORT).show();
                showDatePickerDialog();
            }
        });


        changeTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(RequestDetailsActivity.this, "Not Implemented YET !", Toast.LENGTH_SHORT).show();
                showTimePickerDialog();
            }
        });

        chatClient_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ConfID = "";
                boolean isConfFound = false;
                for (int i = 0; i < Main2Activity.AllConversations.size(); i++)
                    if (Main2Activity.AllConversations.get(i).getUserID().equals(CurrentOrder.getUserID())) {
                        ConfID = Main2Activity.AllConversations.get(i).getConversationID();
                        isConfFound = true;
                        break;
                    }

                if (isConfFound) {
                    startChatActivity(ConfID);
                } else {
                    createConversation();
                }
            }
        });

    }

    private void setToNoActionMode() {
        changeDate_btn.setVisibility(View.GONE);
        changeTime_btn.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);
        spinnerReqDetails_ll.setVisibility(View.GONE);
    }

    private void createConversation() {
        final Conversation conversation = new Conversation();
        conversation.setBrokerID(CurrentOrder.getBrokerID());
        conversation.setUserID(CurrentOrder.getUserID());

        conversation.setBrokerImage(CurrentOrder.getBrokerImage());
        conversation.setUserImage(CurrentOrder.getUserImage());

        conversation.setBrokerName(CurrentOrder.getBrokerName());
        conversation.setUserName(CurrentOrder.getUserName());

        conversation.setConversationID(MainActivity.CurrentUser.getUserID() + currentProvider.getBrokerID());

        DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("Providers")
                .child(CurrentOrder.getBrokerID()).child("Conversations")
                .child(conversation.getConversationID());

        databaseReference.child(conversation.getConversationID())
                .setValue(conversation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    DatabaseReference userRef = firebaseDatabase.getReference().getRoot().child("User")
                            .child(CurrentOrder.getUserID()).child("Conversations").child(conversation.getConversationID());
                    userRef.setValue(conversation).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Main2Activity.AllConversations.add(conversation);
                                startChatActivity(conversation.getConversationID());
                            }
                        }
                    });

                }
            }
        });
    }

    void startChatActivity(String ConfID) {
        Intent intent = new Intent(RequestDetailsActivity.this, ChatActivity.class);
        intent.putExtra("confID", ConfID);
        intent.putExtra("isBroker", 1);
        startActivity(intent);
    }


    private void showEditConfirmation() {
        final Dialog dialog = new Dialog(RequestDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_edit);
        dialog.setCancelable(false);

        Button confirm_btn = (Button) dialog.findViewById(R.id.btnDialogConfirm);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editOrder();
                dialog.dismiss();
            }
        });

        Button cancel_btn = (Button) dialog.findViewById(R.id.btnDialogConfirmCancel);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void editOrder() {
        CurrentOrder.setStatus("edited by broker");

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

                                Toast.makeText(RequestDetailsActivity.this, R.string.appointment_edited,
                                        Toast.LENGTH_SHORT).show();

                                showPendingConfirmationDialog();

                                boolean isApplyChangesAdded = false;
                                for (int i = 0; i < ActionsList.size(); i++)
                                    if (ActionsList.get(i).equals(getString(R.string.apply_changes))) {
                                        isApplyChangesAdded = true;
                                        break;
                                    }
                                if (!isApplyChangesAdded) {
                                    ActionsList.add(getString(R.string.apply_changes));
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(RequestDetailsActivity.this, R.string.edit_failed,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else {
                    Toast.makeText(RequestDetailsActivity.this, getString(R.string.edit_failed),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showPendingConfirmationDialog() {
        final Dialog dialog = new Dialog(RequestDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait_approval);
        dialog.setCancelable(false);

        Button done_btn = (Button) dialog.findViewById(R.id.buttonDialogWaitApproval);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void showCancelConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RequestDetailsActivity.this);
        alertDialogBuilder.setTitle("Cancel Order");
        alertDialogBuilder
                .setMessage(R.string.cancel_request_question)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cancelOrder();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void cancelOrder() {
        DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(CurrentOrder.getUserID())
                .child("Orders").child(CurrentOrder.getOrderID()).child("status");

        userRef.setValue("canceled by broker").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DatabaseReference brokerRef = firebaseDatabase.getReference().child("Providers")
                            .child(CurrentOrder.getBrokerID()).child("Orders").child(CurrentOrder.getOrderID()).child("status");

                    brokerRef.setValue("canceled by broker")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(RequestDetailsActivity.this, R.string.order_canceled,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RequestDetailsActivity.this,
                                                R.string.order_cancel_failed, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                } else {
                    Toast.makeText(RequestDetailsActivity.this,
                            R.string.order_cancel_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showApproveConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RequestDetailsActivity.this);
        alertDialogBuilder.setTitle("Approve Order");
        alertDialogBuilder
                .setMessage(R.string.approve_order_question)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        approveOrder();
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

    private void approveOrder() {
        CurrentOrder.setStatus("edited by user");

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

                                        Toast.makeText(RequestDetailsActivity.this, R.string.order_approved,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RequestDetailsActivity.this,
                                                R.string.failed_to_approve_order, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                } else {
                    Toast.makeText(RequestDetailsActivity.this,
                            R.string.failed_to_approve_order, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showDatePickerDialog() {
        final Dialog dialog = new Dialog(RequestDetailsActivity.this);
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
        final Dialog dialog = new Dialog(RequestDetailsActivity.this);
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
                }
                dialog.dismiss();
//                Toast.makeText(ScheduleProviderActivity.this, TimeToDisplay, Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
    }


    @SuppressLint("NewApi")
    private void callBroker() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + CurrentOrder.getUserPhone()));
                startActivity(callIntent);
            } catch (ActivityNotFoundException activityException) {
                Log.e("Calling a Phone Number", "Call failed", activityException);
            }
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CALL_PHONE)) {

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(this, R.string.call_permission_needed,
                            Toast.LENGTH_SHORT).show();
                }
            }
            requestPermissions(permsCall, CALL_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CALL_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED/* && ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                    return;
                }
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + CurrentOrder.getBrokerPhone()));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
