package com.example.lap.stschat;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pd.chocobar.ChocoBar;
import com.squareup.picasso.Picasso;
import com.tfb.fbtoast.FBToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hk.ids.gws.android.sclick.SClick;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG ="ChatActivity" ;
    private String messageReciverID, messageReciverName,messageReciverImage, messageSenderID;
    private TextView userName , userLastSeen;
    public TextView senderMessageText, receiverMessageText,tiptext,messagenew;
    private CircleImageView userImage;
    private ImageButton tip;
    private Toolbar ChatToolBar;
    private ImageButton SendMessageButton;
    private ImageView newmessage,messageisthere;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef,NotificationRef,MessageRef,TipRef,UsersRef;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private  MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        MessageRef = RootRef.child("Messages");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        messageReciverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReciverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReciverImage = getIntent().getExtras().get("visit_image").toString();
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notificationschat");
        //TipRef = FirebaseDatabase.getInstance().getReference().child("Users").child(messageReciverID).child("tip");



        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sendsound);







        InitializeControllers();

        userName.setText(messageReciverName);
        Picasso.get().load(messageReciverImage).placeholder(R.drawable.profile_image).into(userImage);


        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (!SClick.check(SClick.BUTTON_CLICK, 2000)) return; // It will auto unlock after 2sec
                SendMessage();
                Animation anim = android.view.animation.AnimationUtils.loadAnimation(SendMessageButton.getContext(),R.anim.shake);
                anim.setDuration(100L);
                SendMessageButton.startAnimation(anim);
                mp.start();

            }
        });


        if (MainActivity.InternetConnection.checkConnection(getApplicationContext())) {
            // Internet Available...
        }
        else
        {
            // Internet Not Available...
            ChocoBar.builder().setActivity(ChatActivity.this)
                    .setText("Please check your network connection")
                    .setDuration(ChocoBar.LENGTH_INDEFINITE)

                    .red()   // in built red ChocoBar
                    .show();
        }



















    }

    private void InitializeControllers()
    {

        ChatToolBar =(Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);



        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView  = layoutInflater.inflate(R.layout.coustom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userImage = (CircleImageView)findViewById(R.id.custom_profile_image);
        userName = (TextView)findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView)findViewById(R.id.custom_user_last_seen);
        tip = (ImageButton) findViewById(R.id.tiplink1);
        messageisthere = (ImageView) findViewById(R.id.messagewhat);
        messagenew = (TextView) findViewById(R.id.messagestatus);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);
        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        DisplayLastSeen();
        RetriveUserInfo();

    }

    private void  DisplayLastSeen()
    {
        RootRef.child("Users").child(messageReciverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();
                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online now");
                            }
                            if(state.equals("offline"))
                            {

                                userLastSeen.setText("Last Seen: " + date + " " + time);


                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });


        Query last = MessageRef.child(messageReciverID).child(messageSenderID).orderByKey();

        last.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {


                for (DataSnapshot ds : dataSnapshot.getChildren())
                {



                    if (ds.hasChild("count")) {
                        String what = ds.child("count").getValue().toString();

                        if (what.equals("unread")) {
                            messageisthere.setImageResource(R.drawable.ic_conversation);
                            messagenew.setText("Delivered");

                        }
                        else if (what.equals("read"))
                        {


                            messageisthere.setImageResource(R.drawable.ic_readmsg);
                            messagenew.setText("Read");

                        }
                    }
                    else
                    {
                        messageisthere.setImageResource(R.drawable.ic_conversation);
                        messagenew.setText("Delivered");
                    }
                }
            }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    private void RetriveUserInfo()
    {
        UsersRef.child(messageReciverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("tip")))
                        {
                            final String retrivetip = dataSnapshot.child("tip").getValue().toString();

                            tip.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                   // final String url = TipRef;

                                        /*Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                        intent.putExtra("id", url);
                                        startActivity(intent);*/


                                    Uri uri = Uri.parse(retrivetip);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                    startActivity(intent);
                                }
                            });

                        }

                        else
                        {
                            tip.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    protected void onStart()
    {




        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            updateUserStatus("online");
        }
        MessageRef.child(messageSenderID).child(messageReciverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();


                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

                        /*DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                                .child(messageSenderID).child(messageReciverID);
                        String messagePushID = userMessageKeyRef.getKey();
                        MessageRef.child(messageSenderID).child(messageReciverID).child(messagePushID).child("count").setValue("read");*/


                      // updateCount("read");











                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        /*DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                                .child(messageSenderID).child(messageReciverID);
                        String messagePushID = userMessageKeyRef.getKey();
                        MessageRef.child(messageSenderID).child(messageReciverID).child(messagePushID).child("count").setValue("read");*/

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot)
                    {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });






    }

    @Override
    protected void onPause()
    {
        super.onPause();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            updateUserStatus("offline");

        }
    }


    private void SendMessage()
    {
        final String messageText = MessageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText))
        {

            FBToast.warningToast(ChatActivity.this,"First write your Query...", FBToast.LENGTH_SHORT);

        }
        else
        {
            HashMap<String,String> chatNotificationMap = new HashMap<>();
            chatNotificationMap.put("from",messageSenderID);
            chatNotificationMap.put("type","message");


            NotificationRef.child(messageReciverID).push()
                    .setValue(chatNotificationMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReciverID;
                                String messageReciverRef = "Messages/" + messageReciverID + "/" + messageSenderID;

                                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                                        .child(messageSenderID).child(messageReciverID).push();
                                String messagePushID = userMessageKeyRef.getKey();
                                Map messageTextBody = new HashMap();
                                messageTextBody.put("message", messageText);
                                messageTextBody.put("type", "text");
                                messageTextBody.put("from", messageSenderID);

                                Map messageBodyDetails = new HashMap();
                                messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);

                                messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                                RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task)
                                    {
                                        if(task.isSuccessful())
                                        {

                                            FBToast.successToast(ChatActivity.this,"Your Query is Send Successfully", FBToast.LENGTH_SHORT);

                                                updateCount("unread");

                                        }
                                        else
                                        {
                                            Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();

                                        }

                                        MessageInputText.setText("");
                                    }
                                });
                            }
                        }
                    });




        }
    }


    private void  updateUserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;
        String currentUserID = mAuth.getCurrentUser().getUid();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());


        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String ,Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state",state);

        currentUserID =mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);


    }

    private void  updateCount(final String state1) {






        MessageRef.child(messageReciverID).child(messageSenderID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {


                        HashMap<String ,Object> onlineStateMap = new HashMap<>();

                        onlineStateMap.put("count",state1);



                        String messagePushID = dataSnapshot.getKey();
                        MessageRef.child(messageReciverID).child(messageSenderID).child(messagePushID)
                                .updateChildren(onlineStateMap);





                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot)
                    {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });


    }

//now you can use model.getname();....










    public static class InternetConnection {

        /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
        public static boolean checkConnection(Context context) {
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                //Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    return true;
                }
            }
            return false;
        }
    }






}
