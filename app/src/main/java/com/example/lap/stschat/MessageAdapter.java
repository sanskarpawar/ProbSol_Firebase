package com.example.lap.stschat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.support.v4.content.ContextCompat.createDeviceProtectedStorageContext;
import static android.support.v4.content.ContextCompat.getSystemService;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
 private List<Messages> userMessageList;
 private FirebaseAuth mAuth;
    private DatabaseReference RootRef,NotificationRef,MessageRef,TipRef,UsersRef;
 private DatabaseReference usersRef;
 private Context context;

 public MessageAdapter (List<Messages> userMessageList)
 {
     this.userMessageList = userMessageList;



 }


    public class  MessageViewHolder extends RecyclerView.ViewHolder
    {

        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);
            senderMessageText =(TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText =(TextView) itemView.findViewById(R.id.reciver_message_text);
            receiverProfileImage =(CircleImageView) itemView.findViewById(R.id.message_profile_image);
        }


    }






    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup,false);





        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);





    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i)
    {
      final   String messageSenderID = mAuth.getCurrentUser().getUid();


        Messages messages = userMessageList.get(i);
        final String fromUserID = messages.getFrom();
         String fromMessageType = messages.getType();



         usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

         usersRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot)
             {
                 if (dataSnapshot.hasChild("image"))
                 {
                     String receiverImage = dataSnapshot.child("image").getValue().toString();
                     Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(messageViewHolder.receiverProfileImage);

                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError)
             {

             }
         });
         if(fromMessageType.equals("text"))
         {
             messageViewHolder.receiverMessageText.setVisibility(View.INVISIBLE);
             messageViewHolder.receiverProfileImage.setVisibility(View.INVISIBLE);
             messageViewHolder.senderMessageText.setVisibility(View.INVISIBLE);



             if (fromUserID.equals(messageSenderID))
             {
                 messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                 messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                 messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                 messageViewHolder.senderMessageText.setText(messages.getMessage());



             }
             else
             {

                 messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                 messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);


                 messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                 messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                 messageViewHolder.receiverMessageText.setText(messages.getMessage());

               /*  RootRef = FirebaseDatabase.getInstance().getReference();
                 MessageRef = RootRef.child("Messages");



                 MessageRef.child(messageSenderID).child(fromUserID)
                         .addChildEventListener(new ChildEventListener()
                         {
                             @Override
                             public void onChildAdded( DataSnapshot dataSnapshot, String s)
                             {


                                 HashMap<String ,Object> onlineStateMap = new HashMap<>();

                                 onlineStateMap.put("count","read");



                                 String messagePushID =  dataSnapshot.getKey();


                                 MessageRef.child(messageSenderID).child(fromUserID).child(messagePushID)
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
                         });*/








             }
         }




    }



    @Override
    public int getItemCount()
    {
        return userMessageList.size();


    }








    }
