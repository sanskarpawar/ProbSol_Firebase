package com.example.lap.stschat;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.himangi.imagepreview.ImagePreviewActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    //Object main_tabs_pager;

    private  View PrivateChatView;
    private RecyclerView chatlist;
    private DatabaseReference chattsref,UsersRef,LikeRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRef,NotificationRef,MessageRef,TipRef;
    private String messageReciverID, messageReciverName,messageReciverImage, messageSenderID;


    //boolean LikeChecker = false;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        PrivateChatView = inflater.inflate(R.layout.fragment_chats, container, false);
        chatlist = (RecyclerView) PrivateChatView.findViewById(R.id.ChatsList);
        chatlist.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth =FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();


        chattsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RootRef = FirebaseDatabase.getInstance().getReference();
        MessageRef = RootRef.child("Messages");
        messageSenderID = mAuth.getCurrentUser().getUid();
       // messageReciverID = getActivity().getIntent().getExtras().get("visit_user_id").toString();

        // LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");



        return PrivateChatView;



    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chattsref, Contacts.class)
                        .build();


        final FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model)
                    {
                       final String usersIds = getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        //holder.setLikeButtonStatus(usersIds);



                        UsersRef.child(usersIds).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                               if(dataSnapshot.exists())
                               {

                                   if(dataSnapshot.hasChild("image"))
                                   {
                                           retImage[0] = dataSnapshot.child("image").getValue().toString();
                                       Picasso.get().load(retImage[0]).into(holder.profileImage);



                                   }

                                   final String retName= dataSnapshot.child("name").getValue().toString();
                                   final String retStatus = dataSnapshot.child("status").getValue().toString();

                                   holder.userName.setText(retName);

                                   if(dataSnapshot.child("userState").hasChild("state"))
                                   {
                                       String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                       String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                       String time = dataSnapshot.child("userState").child("time").getValue().toString();
                                       if (state.equals("online"))
                                       {
                                           holder.userStatus.setText("online now");
                                           holder.onlineIcon.setVisibility(View.VISIBLE);
                                       }
                                       else if(state.equals("offline"))
                                       {
                                           holder.userStatus.setTextSize(15);

                                           holder.userStatus.setText("Last Seen: " + date + " " + time);
                                           holder.onlineIcon.setVisibility(View.INVISIBLE);


                                       }
                                   }
                                   else
                                   {
                                       holder.userStatus.setText("offline");
                                       holder.onlineIcon.setVisibility(View.INVISIBLE);


                                   }

















                                   holder.itemView.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v)
                                       {
                                           final Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                           chatIntent.putExtra("visit_user_id", usersIds);
                                           chatIntent.putExtra("visit_user_name", retName);
                                           chatIntent.putExtra("visit_image", retImage[0]);


                                           startActivity(chatIntent);




                                           MessageRef.child(messageSenderID).child(usersIds)
                                                   .addChildEventListener(new ChildEventListener()
                                                   {
                                                       @Override
                                                       public void onChildAdded( DataSnapshot dataSnapshot, String s)
                                                       {


                                                           HashMap<String ,Object> onlineStateMap = new HashMap<>();

                                                           onlineStateMap.put("count","read");



                                                           String messagePushID = dataSnapshot.getKey();


                                                           MessageRef.child(messageSenderID).child(usersIds).child(messagePushID)
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
                                   });


                                  /* holder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v)
                                       {
                                           LikeChecker = true;
                                           LikeRef.addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot dataSnapshot)
                                               {
                                                 if(LikeChecker ==(true))
                                                 {
                                                     if(dataSnapshot.child(usersIds).hasChild(currentUserID))
                                                     {

                                                         LikeRef.child(usersIds).child(currentUserID).removeValue();
                                                         LikeChecker = false;

                                                     }
                                                     else
                                                     {
                                                         LikeRef.child(usersIds).child(currentUserID).setValue(true);
                                                         LikeChecker = false;
                                                     }
                                                 }
                                               }

                                               @Override
                                               public void onCancelled(DatabaseError databaseError) {

                                               }
                                           });
                                       }
                                   });*/
                               }
                                /*MessageRef.child(messageSenderID).child(usersIds).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)

                                    {
                                        if(dataSnapshot.exists())
                                        {

                                            String messagePushID = dataSnapshot.getKey();


                                            if (dataSnapshot.child(messagePushID).hasChild("count"))
                                            {
                                                String what = dataSnapshot.child(messagePushID).child("count").getValue().toString();

                                                if (what.equals("unread"))
                                                {
                                                    holder.readimg.setVisibility(View.VISIBLE);

                                                    holder.readtxt.setVisibility(View.VISIBLE);
                                                }
                                                else if (what.equals("read"))
                                                {
                                                    holder.readimg.setVisibility(View.INVISIBLE);
                                                    holder.readtxt.setVisibility(View.INVISIBLE);


                                                }
                                            }
                                            else
                                            {
                                                holder.readimg.setVisibility(View.INVISIBLE);
                                                holder.readtxt.setVisibility(View.INVISIBLE);


                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });*/






                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Query last = MessageRef.child(messageSenderID).child(usersIds).orderByKey();

                        last.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {


                                for (DataSnapshot ds : dataSnapshot.getChildren())
                                {



                                    if (ds.hasChild("count")) {
                                        String what = ds.child("count").getValue().toString();

                                        if (what.equals("unread")) {
                                            holder.readimg.setVisibility(View.VISIBLE);

                                            holder.readtxt.setVisibility(View.VISIBLE);

                                        }
                                        else if (what.equals("read"))
                                        {
                                            holder.readimg.setVisibility(View.INVISIBLE);
                                            holder.readtxt.setVisibility(View.INVISIBLE);



                                        }
                                    }
                                    else
                                        {
                                        holder.readimg.setVisibility(View.INVISIBLE);
                                        holder.readtxt.setVisibility(View.INVISIBLE);
                                         }
                                }
                            }




                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });





























                    }


                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout1,viewGroup,false);
                       return new ChatsViewHolder(view);
                    }
                };

        chatlist.setAdapter(adapter);
        adapter.startListening();

    }
    public  static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus,readtxt;
        RoundedImageView profileImage;
        ImageView onlineIcon,readimg;

        //ImageButton LikePostButton;
      // TextView DisplayNoOfLike;
        //int countLikes;
        //String currentUserId;
        //DatabaseReference LikesRef;

        public ChatsViewHolder(@NonNull View itemView)
        {

            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name1);
            userStatus = itemView.findViewById(R.id.user_status1);
            readtxt = itemView.findViewById(R.id.newmsgtext);
            profileImage = itemView.findViewById(R.id.user_profile_image1);
            onlineIcon = (ImageView) itemView.findViewById(R.id.user_online_status1);
            readimg = (ImageView) itemView.findViewById(R.id.newmsgimage);



          /*  LikePostButton = itemView.findViewById(R.id.ups_button);
          // DisplayNoOfLike = itemView.findViewById(R.id.display_no_of_ups);
            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();*/


        }


      /*  public void setLikeButtonStatus(final String usersIds)
        {
          LikesRef.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot)
              {
                  if(dataSnapshot.child(usersIds).hasChild(currentUserId))
                  {
                     // countLikes = (int) dataSnapshot.child(usersIds).getChildrenCount();
                      LikePostButton.setImageResource(R.drawable.like1);
                     // DisplayNoOfLike.setText((Integer.toString(countLikes)+(" Ups")));

                  }
                  else
                  {
                     // countLikes = (int) dataSnapshot.child(usersIds).getChildrenCount();
                      LikePostButton.setImageResource(R.drawable.dislike1);
                     // DisplayNoOfLike.setText((Integer.toString(countLikes)+("Ups")));
                  }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          }) ;
        }*/



    }







}