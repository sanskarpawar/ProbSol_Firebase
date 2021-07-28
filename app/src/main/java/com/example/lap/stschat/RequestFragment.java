package com.example.lap.stschat;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {


        private View RequestFragmentView;
        private RecyclerView myRequestsList;
        private DatabaseReference ChatRequestRef, UserRef, ContactsRef;
        private FirebaseAuth mAuth;
        private String currentUserID;






    public RequestFragment()
        {

            // Required empty public constructor
        }

                    @Override
            public View onCreateView (LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState)

            {
                // Inflate the layout for this fragment
                RequestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);

                mAuth = FirebaseAuth.getInstance();
                currentUserID = mAuth.getCurrentUser().getUid();
                UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
                ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat Request");
                ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

                myRequestsList = (RecyclerView) RequestFragmentView.findViewById(R.id.chat_request_list);
                myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));


                return RequestFragmentView;


            }


        @Override
        public void onStart ()
        {
            super.onStart();

            FirebaseRecyclerOptions<Contacts> options =
                    new FirebaseRecyclerOptions.Builder<Contacts>()
                            .setQuery(ChatRequestRef.child(currentUserID), Contacts.class)
                            .build();


            FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Contacts,RequestsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model) {
                            holder.itemView.findViewById(R.id.reauest_accept_button).setVisibility(View.VISIBLE);
                            holder.itemView.findViewById(R.id.reauest_cancel_button).setVisibility(View.VISIBLE);


                            final String list_use_id = getRef(position).getKey();


                            final DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                            getTypeRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        String type = dataSnapshot.getValue().toString();

                                        if (type.equals("received")) {
                                            UserRef.child(list_use_id).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild("image")) {

                                                        final String requestProfileImage = dataSnapshot.child("image").getValue().toString();

                                                        Picasso.get().load(requestProfileImage).into(holder.profileImage);

                                                    }

                                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText("wants to connect with you");


                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            CharSequence options[] = new CharSequence[]
                                                                    {
                                                                            "Accept",
                                                                            "Cancel"
                                                                    };
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                            builder.setTitle(requestUserName + " Talk Request");

                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    if (which == 0) {
                                                                        ContactsRef.child(currentUserID).child(list_use_id).child("Contacts")
                                                                                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    ContactsRef.child(list_use_id).child(currentUserID).child("Contacts")
                                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                ChatRequestRef.child(currentUserID).child(list_use_id)
                                                                                                        .removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    ChatRequestRef.child(list_use_id).child(currentUserID)
                                                                                                                            .removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        Snackbar.make(getView(), "New Contact Added", Snackbar.LENGTH_LONG)
                                                                                                                                                .setAction("Action", null).show();
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
                                                                            }
                                                                        });
                                                                    }
                                                                    if (which == 1) {
                                                                        ChatRequestRef.child(currentUserID).child(list_use_id)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            ChatRequestRef.child(list_use_id).child(currentUserID)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                Snackbar.make(getView(), "Request Declined ", Snackbar.LENGTH_LONG)
                                                                                                                        .setAction("Action", null).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                            builder.show();
                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                        } else if (type.equals("sent")) {
                                            Button request_sent_btn = holder.itemView.findViewById(R.id.reauest_accept_button);
                                            request_sent_btn.setText("Request Sent");

                                            holder.itemView.findViewById(R.id.reauest_cancel_button).setVisibility(View.INVISIBLE);

                                            UserRef.child(list_use_id).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild("image")) {

                                                        final String requestProfileImage = dataSnapshot.child("image").getValue().toString();

                                                        Picasso.get().load(requestProfileImage).into(holder.profileImage);

                                                    }

                                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText("you have sent request to " + requestUserName);


                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            CharSequence options[] = new CharSequence[]
                                                                    {
                                                                            "Cancel Talk request"
                                                                    };
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                            builder.setTitle("Already Sent Request");

                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    if (which == 0) {
                                                                        ChatRequestRef.child(currentUserID).child(list_use_id)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            ChatRequestRef.child(list_use_id).child(currentUserID)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                Snackbar.make(getView(), "You have cancelled the chat request..", Snackbar.LENGTH_LONG)
                                                                                                                        .setAction("Action", null).show();
                                                                                                                //Toast.makeText(, "", Toast.LENGTH_SHORT).show();


                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                            builder.show();
                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

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
                        public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                            RequestsViewHolder holder = new RequestsViewHolder(view);
                            return holder;
                        }
                    };
            myRequestsList.setAdapter(adapter);
            adapter.startListening();

        }



        public static class RequestsViewHolder extends RecyclerView.ViewHolder {

            TextView userName, userStatus;
            RoundedImageView profileImage;
            Button AcceptButton, CancelButton;

            public RequestsViewHolder(@NonNull View itemView) {

                super(itemView);

                userName = itemView.findViewById(R.id.user_profile_name);
                userStatus = itemView.findViewById(R.id.user_status);
                profileImage = itemView.findViewById(R.id.user_profile_image);
                AcceptButton = itemView.findViewById(R.id.reauest_accept_button);
                CancelButton = itemView.findViewById(R.id.reauest_cancel_button);


            }
        }
    }
