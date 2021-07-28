package com.example.lap.stschat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import net.vrgsoft.layoutmanager.RollingLayoutManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class NewsActivity extends AppCompatActivity {

    private RecyclerView mPeopleRV;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<News, NewsViewHolder> mPeopleRVAdapter;
    ImageButton SearchButton;
    EditText SearchInputText;
    private DatabaseReference UserRef,RootRef,personsRef,UserRef1;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        setTitle("News");



        SearchButton = (ImageButton) findViewById(R.id.news_search_field);
        SearchInputText = (EditText) findViewById(R.id.news_editText_field);

        UserRef = FirebaseDatabase.getInstance().getReference().child("News");




        mToolbar = (Toolbar) findViewById(R.id.news_find_members_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Webs");




        //UserRef = FirebaseDatabase.getInstance().getReference().child("Users");



        mAuth = FirebaseAuth.getInstance();

        RootRef = FirebaseDatabase.getInstance().getReference();

        //"News" here will reflect what you have called your database in Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("News");
        mDatabase.keepSynced(true);

        mPeopleRV = (RecyclerView) findViewById(R.id.myRecycleView);
        RollingLayoutManager rollingLayoutManager = new RollingLayoutManager(this);
        mPeopleRV.setLayoutManager(rollingLayoutManager);

        personsRef = FirebaseDatabase.getInstance().getReference().child("News");
       // Query personsQuery = personsRef.orderByKey();









        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String searchFieldName = SearchInputText.getText().toString();
                if(TextUtils.isEmpty(searchFieldName))
                {
                    Toast.makeText(NewsActivity.this, "Please Type Field First...", Toast.LENGTH_SHORT).show();
                    //SearchForPeopleAndFriend();
                }
                else
                {
                    SearchForPeopleAndFriend(searchFieldName);

                }
            }
        });








    }

   /* @Override
    public void onStart() {
        super.onStart();
        mPeopleRVAdapter.startListening();
    }*/

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




    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public NewsViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setDesc(String desc){
            TextView post_desc = (TextView)mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }
        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(image).into(post_image);
        }
    }





    private void SearchForPeopleAndFriend(String searchFieldName)
    {




        Toast.makeText(this, "Searching....", Toast.LENGTH_SHORT).show();




       // Query personsQuery = personsRef.orderByKey();

        Query searchField = UserRef.orderByChild("desc")
                .startAt(searchFieldName).endAt(searchFieldName + "\uf8ff");

        super.onStart();

        mPeopleRV.hasFixedSize();
        mPeopleRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<News>().setQuery(searchField, News.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(NewsActivity.NewsViewHolder holder, final int position, final News model) {
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDesc());
                holder.setImage(getBaseContext(), model.getImage());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String url = model.getUrl();
                        Intent intent = new Intent(getApplicationContext(), NewsWebView.class);
                        intent.putExtra("id", url);
                        startActivity(intent);


                    }
                });
            }

            @Override
            public NewsActivity.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.news_row, parent, false);

                return new NewsActivity.NewsViewHolder(view);
            }
        };
        mPeopleRV.setAdapter(mPeopleRVAdapter);
        mPeopleRVAdapter.startListening();
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
