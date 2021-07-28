package com.example.lap.stschat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hussain_chachuliya.snappy.Snappy;
import com.pd.chocobar.ChocoBar;
import com.rahimlis.badgedtablayout.BadgedTabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
//private FloatingActionButton fab;


    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, connectedRef;
    private String currentUserID,reciverUserID,SenderUserID;
    public boolean isFirstStart;
    private DatabaseReference ChatRequestRef,UserRef,ContactsRef;
    BadgedTabLayout badgedTabLayout;

    private RequestFragment f1;


    Context mcontext;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

//        currentUserID = mAuth.getCurrentUser().getUid();

        // SenderUserID = mAuth.getCurrentUser().getUid();



        RootRef = FirebaseDatabase.getInstance().getReference();
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat Request");




        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" ProbSol");
        getSupportActionBar().setIcon(R.drawable.ic_collaboration);


        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        //myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        // myTabLayout.setupWithViewPager(myViewPager);
        final BadgedTabLayout myTabLayout = (BadgedTabLayout) findViewById(R.id.tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        // myTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //myTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        myTabLayout.setIcon(0, R.drawable.talks);
        myTabLayout.setIcon(1, R.drawable.notes1);
        myTabLayout.setIcon(2, R.drawable.target);
        myTabLayout.setIcon(3,R.drawable.request);


        if (InternetConnection.checkConnection(getApplicationContext())) {
            // Internet Available...
        }
        else
            {
            // Internet Not Available...
                ChocoBar.builder().setActivity(MainActivity.this)
                        .setText("Network Error")
                        .setDuration(ChocoBar.LENGTH_INDEFINITE)
                        .setActionText(android.R.string.ok)
                        .red()   // in built red ChocoBar
                        .show();
        }
















        // myTabLayout.getTabAt(0).setIcon(R.drawable.discussion);
        //myTabLayout.getTabAt(1).setIcon(R.drawable.notes);
        //myTabLayout.getTabAt(2).setIcon(R.drawable.contacts);
        // myTabLayout.getTabAt(3).setIcon(R.drawable.application);







        /*for (int i = 0; i <myTabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            TextView tv = (TextView)LayoutInflater.from(this).inflate(R.layout.tabtext,null);
            tv.setTypeface(Typeface.DEFAULT);
            myTabLayout.getTabAt(i).setCustomView(tv);
        }*/

        /*TextView tabOne = (TextView)
                LayoutInflater.from(this).inflate(R.layout.tabtext, null);
        tabOne.setText("Talks");
        tabOne.setTextSize(14); // set font size as per your requirement
        myTabLayout.getTabAt(0).setCustomView(tabOne);*/




        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation anim = android.view.animation.AnimationUtils.loadAnimation(fab.getContext(),R.anim.shake);
                anim.setDuration(300L);
                fab.startAnimation(anim);
                SendUserToNewsActiviy();

        //handle button activities


            }
        });

        // connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        //connectedRef.addValueEventListener(new ValueEventListener() {
        //  @Override
        //public void onDataChange(DataSnapshot snapshot) {
        //  boolean connected = snapshot.getValue(Boolean.class);
        //if (connected)
        //{

        //}
        //else
        //{
        //  updateUserStatus("offline");

        //}
        //}

        //@Override
        //public void onCancelled(DatabaseError error)
        //{

        //}
        //});
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Intro App Initialize SharedPreferences
                SharedPreferences getSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                //  Check either activity or app is open very first time or not and do action
                if (isFirstStart) {

                    //  Launch application introduction screen
                    Intent i = new Intent(MainActivity.this, Activity_AppIntro.class);
                    startActivity(i);
                    SharedPreferences.Editor e = getSharedPreferences.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();

    }









    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            SendUserToLoginActiviy();
        }
        else
        {

           VerifyUserExistance();
            updateUserStatus("online");
        }

    }



    /*@Override
   protected void onStop()
    {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            updateUserStatus("offline");

        }
    }*/

    @Override
    protected void onDestroy()
    {

        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            updateUserStatus("offline");

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



    private void VerifyUserExistance()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists()))
                {

                }
                else
                {

                    SendUserToSettingsActiviy();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {




        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;






    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_find_logout_option)
        {
            updateUserStatus("offline");

            mAuth.signOut();
            SendUserToLoginActiviy();
        }
        if (item.getItemId() == R.id.main_find_setting_option)
        {
            SendUserToSettingsActiviy();
        }

        if (item.getItemId() == R.id.main_find_member_option)
        {
            SendUserToFindMembersActiviy();
        }


        if (item.getItemId() == R.id.mybutton)
        {
            SendUserToFindMembersActiviy();
        }
        if (item.getItemId() == R.id.main_reset_password)
        {
            SendUserToResetPasswordActivity();
        }
        if (item.getItemId() == R.id.main_tip_link)
        {
            SendUserToTipActivity();
        }
        if (item.getItemId() == R.id.mybutton1)
        {
            SendUserToTakenoteActivity();
        }
       /*if (item.getItemId() == R.id.main_find_talk_option)
        {
            SendUserToFindTalkActiviy();
        }*/

        return true;
    }



    private void RequestNewGroup() //Creating Batches
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter the Batch Name :");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g BVIOT");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write Batch Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);

                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void CreateNewGroup(final String groupName)
    {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName + " Batch is Created Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToLoginActiviy()
    {
        Intent toy1 = new Intent(MainActivity.this, Activity_Login.class);
        toy1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toy1);
        finish();
    }
    private void SendUserToSettingsActiviy()
    {
        Intent Settings = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(Settings);

    }
    private void SendUserToFindMembersActiviy()
    {
        Intent FindMembers = new Intent(MainActivity.this,FindMembersActivity.class);
        startActivity(FindMembers);

    }
    private void SendUserToResetPasswordActivity()
    {
        Intent ResetPassword = new Intent(MainActivity.this,ResetPasswordActivity.class);
        startActivity(ResetPassword);

    }
    private void SendUserToTipActivity()
    {
        Intent tip = new Intent(MainActivity.this,TipActivity.class);
        startActivity(tip);

    }
    private void SendUserToTakenoteActivity()
    {
        Intent takenote = new Intent(MainActivity.this,TakenoteActivity.class);
        startActivity(takenote);

    }


    private void  updateUserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;
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

    private void SendUserToNewsActiviy()
    {
        Intent News = new Intent(MainActivity.this,NewsActivity.class);
        startActivity(News);

    }

}


