package com.example.lap.stschat;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment
{

    private View ContactsView;
    private RecyclerView myContactsList;
    private DatabaseReference ContactsRef,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    Object main_tabs_pager;
    public NoticeFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        ContactsView = inflater.inflate(R.layout.fragment_notice, container, false);

        myContactsList = (RecyclerView) ContactsView.findViewById(R.id.contact_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
            currentUserID = mAuth.getCurrentUser().getUid();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        UsersRef.keepSynced(true);
        return  ContactsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(ContactsRef, Contacts.class)
                    .build();


        FirebaseRecyclerAdapter<Contacts,ContactViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final ContactViewHolder holder, final int position, @NonNull Contacts model)
            {
                String usersIds = getRef(position).getKey();
                UsersRef.child(usersIds).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {



                       if(dataSnapshot.exists())
                       {

                           if(dataSnapshot.child("userState").hasChild("state"))
                           {
                               String state = dataSnapshot.child("userState").child("state").getValue().toString();
                               String date = dataSnapshot.child("userState").child("date").getValue().toString();
                               String time = dataSnapshot.child("userState").child("time").getValue().toString();
                               if (state.equals("online"))
                               {
                                   holder.onlineIcon.setVisibility(View.VISIBLE);

                               }
                               else if(state.equals("offline"))
                               {
                                   holder.onlineIcon.setVisibility(View.INVISIBLE);

                               }
                           }
                           else
                           {
                               holder.onlineIcon.setVisibility(View.INVISIBLE);

                           }


                           if(dataSnapshot.hasChild("image"))
                           {
                              final String userImage = dataSnapshot.child("image").getValue().toString();
                               String profileName= dataSnapshot.child("name").getValue().toString();
                               String profileStatus = dataSnapshot.child("status").getValue().toString();

                               holder.userName.setText(profileName);
                               holder.userStatus.setText(profileStatus);
                               //Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);


                               Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_image)
                                       .into(holder.profileImage, new Callback() {
                                           @Override
                                           public void onSuccess() {

                                           }

                                           @Override
                                           public void onError(Exception e) {
                                               Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                           }
                                       });
                           }
                           else
                           {
                               String profileName= dataSnapshot.child("name").getValue().toString();
                               String profileStatus = dataSnapshot.child("status").getValue().toString();

                               holder.userName.setText(profileName);
                               holder.userStatus.setText(profileStatus);
                           }
                       }
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();

                                Intent ProfileIntent = new Intent(getActivity(), ProfileActivity.class);

                                ProfileIntent.putExtra("visit_user_Id",visit_user_id);

                                startActivity(ProfileIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout1,viewGroup,false);
                ContactViewHolder viewHolder = new ContactViewHolder(view);
                return  viewHolder;
            }

        };
        myContactsList.setAdapter(adapter);
        adapter.startListening();


    }


    public  static class ContactViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        RoundedImageView profileImage;
        ImageView onlineIcon;


        public ContactViewHolder(@NonNull View itemView)
        {

            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name1);
            userStatus = itemView.findViewById(R.id.user_status1);
            profileImage = itemView.findViewById(R.id.user_profile_image1);
            onlineIcon = (ImageView) itemView.findViewById(R.id.user_online_status1);


        }
    }
}
