package com.example.lap.stschat;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import br.com.liveo.searchliveo.SearchLiveo;
import de.hdodenhof.circleimageview.CircleImageView;


public class FindMembersActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private RecyclerView FindFriendRecyclerList;
    private DatabaseReference UserRef,FieldRef;
    private EditText SearchInputText;
    private ImageButton SearchButton;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_members);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");



        mAuth = FirebaseAuth.getInstance();

        RootRef = FirebaseDatabase.getInstance().getReference();
        //FieldRef = FirebaseDatabase.getInstance().getReference().child("Users").child("status");

        FindFriendRecyclerList = (RecyclerView) findViewById(R.id.find_friend_recycler_list);
        FindFriendRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.find_members_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Field");



        SearchButton = (ImageButton) findViewById(R.id.search_field);
        SearchInputText = (EditText) findViewById(R.id.editText_field);
        //getSupportActionBar().mSearchLiveo.show();


        // mSearchLiveo = findViewById(R.id.search_liveo);
        //mSearchLiveo.with(this).build();
        //mSearchLiveo.show();




        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String searchFieldName = SearchInputText.getText().toString();
                if(TextUtils.isEmpty(searchFieldName))
                {
                    Toast.makeText(FindMembersActivity.this, "Please Type Field First...", Toast.LENGTH_SHORT).show();
                    //SearchForPeopleAndFriend();
                }
                else
                {
                    SearchForPeopleAndFriend(searchFieldName);

                }
            }
        });


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




    private void SearchForPeopleAndFriend(String searchFieldName)
    {

        Toast.makeText(this, "Searching....", Toast.LENGTH_SHORT).show();
        Query searchField = UserRef.orderByChild("status")
                .startAt(searchFieldName).endAt(searchFieldName + "\uf8ff");


        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new  FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(searchField, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Contacts model)
                    {
                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();

                                Intent ProfileIntent = new Intent(FindMembersActivity.this, ProfileActivity.class);
                                ProfileIntent.putExtra("visit_user_Id",visit_user_id);

                                startActivity(ProfileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout1, viewGroup,false);
                        FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                        return viewHolder;
                    }
                };
        FindFriendRecyclerList.setAdapter(adapter);
        adapter.startListening();



    }


    public static class FindFriendsViewHolder extends  RecyclerView.ViewHolder
    {

        TextView userName , userStatus;
        RoundedImageView profileImage;

        public FindFriendsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name1);
            userStatus = itemView.findViewById(R.id.user_status1);
            profileImage = itemView.findViewById(R.id.user_profile_image1);

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
}
