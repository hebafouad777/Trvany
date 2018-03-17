package com.mediaoasis.trvany.activities.user;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mediaoasis.trvany.activities.provider.Main2Activity;
import com.mediaoasis.trvany.adapters.MessagesAdapter;
import com.mediaoasis.trvany.models.Conversation;
import com.mediaoasis.trvany.models.Message;
import com.mediaoasis.trvany.utils.ConnectionDetector;
import com.mediaoasis.trvany.utils.GPSTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatActivity extends AppCompatActivity {

    private static final int CAM_REQUREST = 2;
    private static final int GALLERY_REQUEST_CODE = 123;
    String[] perms = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    //    public static Uri ProfileURL, DownloadURI;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    TextView title_tv;
    Button sendMsg_btn, attach;
    //    ImageView avatar;
    RecyclerView recyclerView;
    EditText editText;
    ProgressBar progressBar;

    Conversation conversation;
    ArrayList<Message> MessagesList;

    FirebaseDatabase firebaseDatabase;
    int isBroker = 0;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

        firebaseDatabase = FirebaseDatabase.getInstance();
        MessagesList = new ArrayList<>();

        Bundle b = getIntent().getExtras();

        String ConfID = b.getString("confID");
        isBroker = b.getInt("isBroker");

        title_tv = (TextView) findViewById(R.id.txtTitleToolbar);
        sendMsg_btn = (Button) findViewById(R.id.buttonSendMsg);
        progressBar = (ProgressBar) findViewById(R.id.progressChat);
        editText = (EditText) findViewById(R.id.edittextMsg);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewChat);
        attach = (Button) findViewById(R.id.chatAttach);
//        avatar =

        mLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (isBroker == 0) {
            for (int i = 0; i < MainActivity.AllConversations.size(); i++)
                if (MainActivity.AllConversations.get(i).getConversationID().equals(ConfID)) {
                    conversation = MainActivity.AllConversations.get(i);
                    break;
                }
            title_tv.setText(conversation.getBrokerName());

        } else {
            for (int i = 0; i < Main2Activity.AllConversations.size(); i++)
                if (Main2Activity.AllConversations.get(i).getConversationID().equals(ConfID)) {
                    conversation = Main2Activity.AllConversations.get(i);
                    break;
                }
            title_tv.setText(conversation.getUserName());
//            try {
//                Picasso.with(ChatActivity.this).load(conversation.getUserImage()).fit().into(avatar);
//            } catch (Exception e) {
//                e.printStackTrace();
//                avatar.setImageResource(R.drawable.profile_placeholder);
//            }
        }


        loadMsgs();

        sendMsg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals(""))
                    editText.setError(getString(R.string.required));
                else {
                    String msgTxt = editText.getText().toString();
                    if (ConnectionDetector.isConnectingToInternet(ChatActivity.this)) {
                        Message newMsg = new Message();
                        newMsg.setIsFromBroker(isBroker);
                        newMsg.setText(msgTxt);
                        newMsg.setType("text");

                        if (isBroker == 1)
                            newMsg.setSenderId(conversation.getBrokerID());
                        else
                            newMsg.setSenderId(conversation.getUserID());

                        DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("Messages")
                                .child(conversation.getConversationID());
                        databaseReference.push().setValue(newMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    editText.setText("");

                                } else {
                                    editText.setError(getString(R.string.msg_send_err));
                                }
                            }
                        });

//                        newMsg.setMessageText(editText.getText().toString());
                    } else {
                        Toast.makeText(ChatActivity.this, getString(R.string.no_internet_con), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttachDialog();
            }
        });

    }

    private void showAttachDialog() {
        final Dialog dialog = new Dialog(ChatActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_attach);

        LinearLayout imageLL = (LinearLayout) dialog.findViewById(R.id.LinearImage);
        LinearLayout cancelLL = (LinearLayout) dialog.findViewById(R.id.LinearCancel);
        LinearLayout locationLL = (LinearLayout) dialog.findViewById(R.id.LinearLocation);

        imageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatActivity.this, getResources().getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                showImageTypeDialog();
            }
        });

        cancelLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        locationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(ChatActivity.this, "Sharing Location..", Toast.LENGTH_SHORT).show();
                shareLocation();
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void shareLocation() {
        GPSTracker gpsTracker = new GPSTracker(ChatActivity.this);

        if (ConnectionDetector.isConnectingToInternet(ChatActivity.this)) {
            Message newMsg = new Message();
            newMsg.setIsFromBroker(isBroker);
            newMsg.setText(gpsTracker.getLatitude() + "," + gpsTracker.getLongitude());
            newMsg.setType("location");
            newMsg.setLatitude(gpsTracker.getLatitude());
            newMsg.setLongitude(gpsTracker.getLongitude());

            if (isBroker == 1)
                newMsg.setSenderId(conversation.getBrokerID());
            else
                newMsg.setSenderId(conversation.getUserID());

            DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("Messages")
                    .child(conversation.getConversationID());
            databaseReference.push().setValue(newMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
//                        editText.setText("");
                        Toast.makeText(ChatActivity.this, R.string.location_shared, Toast.LENGTH_SHORT).show();
                    } else {
                        editText.setError(getResources().getString(R.string.msg_send_err));
                    }
                }
            });

//                        newMsg.setMessageText(editText.getText().toString());
        } else {
            Toast.makeText(ChatActivity.this, getString(R.string.no_internet_con), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMsgs() {
        DatabaseReference msgRef = firebaseDatabase.getReference().getRoot().child("Messages")
                .child(conversation.getConversationID());
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                MessagesList.clear();
                Message message;
                for (DataSnapshot child : children) {
                    message = child.getValue(Message.class);
                    MessagesList.add(message);
                }
                progressBar.setVisibility(View.INVISIBLE);

                messagesAdapter = new MessagesAdapter(ChatActivity.this, MessagesList, conversation);
                mRecyclerView.setAdapter(messagesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)
        ), GALLERY_REQUEST_CODE);
    }


    private void uploadImageToFirebase(Uri uri) {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference()
                .child("ChatImages").child(conversation.getConversationID());

        final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setTitle(getString(R.string.upload_image));
        progressDialog.setCancelable(false);
        progressDialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

                Uri uri = taskSnapshot.getDownloadUrl();
//                    DownloadURI.set(index, uri);

                Message newMsg = new Message();
                newMsg.setIsFromBroker(isBroker);
                newMsg.setText(uri.toString());
                newMsg.setType("image");

                if (isBroker == 1)
                    newMsg.setSenderId(conversation.getBrokerID());
                else
                    newMsg.setSenderId(conversation.getUserID());

                DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("Messages")
                        .child(conversation.getConversationID());
                databaseReference.push().setValue(newMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(ChatActivity.this, R.string.sent_success, Toast.LENGTH_SHORT).show();
                        } else {
                            editText.setError(getResources().getString(R.string.msg_send_err));
                        }
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(ChatActivity.this, getResources().getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage(progress + getResources().getString(R.string.percent_uploaded));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            try {
                Uri uri = data.getData();
                uploadImageToFirebase(uri);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChatActivity.this, getResources().getString(R.string.error_happened) + e.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAM_REQUREST) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri photoURI = createImageFile(photo);
                if (photoURI != null) {
                    uploadImageToFirebase(photoURI);
                } else {
                    Toast.makeText(ChatActivity.this, getString(R.string.error_happened),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChatActivity.this, getString(R.string.error_happened) + e.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showImageTypeDialog() {
        final Dialog dialog = new Dialog(ChatActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_type);

        LinearLayout cameraLL = (LinearLayout) dialog.findViewById(R.id.LinearImageCamera);
        LinearLayout galleryLL = (LinearLayout) dialog.findViewById(R.id.LinearGallery);
        LinearLayout cancelLL = (LinearLayout) dialog.findViewById(R.id.LinearCancel);

        cameraLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                showCamera();
                dialog.dismiss();
            }
        });

        galleryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, getString(R.string.select_image), Toast.LENGTH_SHORT).show();
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

    void showCamera() {
        if (ActivityCompat.checkSelfPermission(ChatActivity.this,
                android.Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAM_REQUREST);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(ChatActivity.this, getString(R.string.location_permission_error),
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
