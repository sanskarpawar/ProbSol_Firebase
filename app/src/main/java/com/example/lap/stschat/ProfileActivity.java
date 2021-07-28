package com.example.lap.stschat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.INVISIBLE;

public class ProfileActivity extends AppCompatActivity
{
    private String reciverUserID,SenderUserID,Current_state;

    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus,tiptext;
    private Button SendMessageRequestButton,DeclineMessageRequestButton;
    private ImageButton tip1;


    private DatabaseReference UserRef,ChatRequestRef, ContactRef,NotificationRef,RootRef,LikeRef;
    private FirebaseAuth mAuth;
    TextView DisplayNoOfLike;
    boolean LikeChecker = false;
    ImageButton LikePostButton;
    int countLikes;
    String currentUserId;
    //DatabaseReference LikesRef;
    //private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat Request");
        ChatRequestRef.keepSynced(true);
        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        ContactRef.keepSynced(true);

        RootRef = FirebaseDatabase.getInstance().getReference();
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");



        LikePostButton = (ImageButton) findViewById(R.id.ups_button1);
        DisplayNoOfLike = (TextView) findViewById(R.id.display_no_of_ups1);
        reciverUserID = getIntent().getExtras().get("visit_user_Id").toString();
        SenderUserID = mAuth.getCurrentUser().getUid();
        tip1 = (ImageButton) findViewById(R.id.tiplinkprofile);
        tiptext = (TextView)findViewById(R.id.tiptext1);

        userProfileImage = (CircleImageView) findViewById(R.id.visit_profile_image);
        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
        SendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton = (Button) findViewById(R.id.decline_message_request_button);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.likesound);

        Current_state = "new";



        LikePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LikeChecker = true;
                mp.start();
                LikeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(LikeChecker ==(true))
                        {
                            if(dataSnapshot.child(reciverUserID).hasChild(SenderUserID))
                            {

                                LikeRef.child(reciverUserID).child(SenderUserID).removeValue();
                                LikeChecker = false;

                            }
                            else
                            {
                                LikeRef.child(reciverUserID).child(SenderUserID).setValue(true);
                                LikeChecker = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });







        setLikeButtonStatus(reciverUserID);






        RetriveUserInfo();
        Tip();
    }



    @Override
    protected void onStart() {

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            updateUserStatus("online");
        }
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


    private void RetriveUserInfo()
    {
        UserRef.child(reciverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if((dataSnapshot.exists()) &&  (dataSnapshot.hasChild("image")))
                {
                    final String userImage =dataSnapshot.child("image").getValue().toString();
                    String userName =dataSnapshot.child("name").getValue().toString();
                    String userStatus =dataSnapshot.child("status").getValue().toString();

                   // Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_image)
                            .into(userProfileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                                }
                            });
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                    ManageChatRequest();


                }
                else
                {

                    String userName =dataSnapshot.child("name").getValue().toString();
                    String userStatus =dataSnapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                    ManageChatRequest();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void Tip()
    {
        UserRef.child(reciverUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("tip")))
                        {
                            final String retrivetip = dataSnapshot.child("tip").getValue().toString();

                            tip1.setVisibility(View.VISIBLE);
                            tiptext.setVisibility(View.VISIBLE);
                            tip1.setOnClickListener(new View.OnClickListener() {
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
                            tip1.setVisibility(INVISIBLE);
                            tiptext.setVisibility(INVISIBLE);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void ManageChatRequest()
    {
        ChatRequestRef.child(SenderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(reciverUserID))
                        {
                            String request_type = dataSnapshot.child(reciverUserID).child("request_type").getValue().toString();
                            if(request_type.equals("sent"))
                            {


                                Current_state = "request_sent";
                                SendMessageRequestButton.setText("Cancel Talk Request");

                            }
                            else if(request_type.equals("received"))
                            {
                                Current_state = "request_received";
                                SendMessageRequestButton.setText("Accept Talk Request");
                                DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                                DeclineMessageRequestButton.setEnabled(true);

                                DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        CancelChatRequest();


                                    }
                                });
                            }
                        }
                        else
                        {
                            ContactRef.child(SenderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(reciverUserID))
                                            {
                                                Current_state = "friends";
                                                SendMessageRequestButton.setText("Remove This Contact");


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {


                    }
                });
        if(!SenderUserID.equals(reciverUserID))
        {
            SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    SendMessageRequestButton.setEnabled(false);
                    if(Current_state.equals("new"))
                    {
                        SendChatRequest();
                    }
                    if(Current_state.equals("request_sent"))
                    {
                        CancelChatRequest();

                    }
                    if(Current_state.equals("request_received"))
                    {

                        AcceptChatRequest();


                    }
                    if(Current_state.equals("friends"))
                    {

                        RemoveSpecificContact();


                    }
                }
            });
        }
        else
        {
            SendMessageRequestButton.setVisibility(INVISIBLE);
        }
    }

    private void RemoveSpecificContact()
    {
        ContactRef.child(SenderUserID).child(reciverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            ContactRef.child(reciverUserID).child(SenderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                SendMessageRequestButton.setEnabled(true);
                                                Current_state = "new";
                                                SendMessageRequestButton.setText("Send Message");


                                                DeclineMessageRequestButton.setVisibility(INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });
    }


    private void AcceptChatRequest()
    {
        ContactRef.child(SenderUserID).child(reciverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            ContactRef.child(reciverUserID).child(SenderUserID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                ChatRequestRef.child(SenderUserID).child(reciverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    ChatRequestRef.child(reciverUserID).child(SenderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {

                                                                                    SendMessageRequestButton.setEnabled(true);
                                                                                    Current_state = "friend";
                                                                                    SendMessageRequestButton.setText("Remove This Contact");

                                                                                    DeclineMessageRequestButton.setVisibility(INVISIBLE);
                                                                                    DeclineMessageRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }

                                        }
                                    });

                        }

                    }
                });
    }





    private void CancelChatRequest()
    {
        ChatRequestRef.child(SenderUserID).child(reciverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            ChatRequestRef.child(reciverUserID).child(SenderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                SendMessageRequestButton.setEnabled(true);
                                                Current_state = "new";
                                                SendMessageRequestButton.setText("Send Message");


                                                DeclineMessageRequestButton.setVisibility(INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });
    }

    private void SendChatRequest()
    {
        ChatRequestRef.child(SenderUserID).child(reciverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            ChatRequestRef.child(reciverUserID).child(SenderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {

                                                HashMap<String,String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from",SenderUserID);
                                                chatNotificationMap.put("type","request");

                                                NotificationRef.child(reciverUserID).push()
                                                        .setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    SendMessageRequestButton.setEnabled(true);
                                                                    Current_state = "request_sent";
                                                                    SendMessageRequestButton.setText("Cancel Talk Request");
                                                                }
                                                            }
                                                        });







                                            }
                                        }
                                    });
                        }
                    }
                });
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

    public void setLikeButtonStatus(final String reciverUserID)
    {
        LikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(reciverUserID).hasChild(SenderUserID))
                {
                    countLikes = (int) dataSnapshot.child(reciverUserID).getChildrenCount();
                    LikePostButton.setImageResource(R.drawable.ic_down);
                    DisplayNoOfLike.setText((Integer.toString(countLikes)+(" Ups")));

                }
                else
                {
                    countLikes = (int) dataSnapshot.child(reciverUserID).getChildrenCount();
                    LikePostButton.setImageResource(R.drawable.ic_up);
                    DisplayNoOfLike.setText((Integer.toString(countLikes)+("Ups")));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }) ;
    }


}
