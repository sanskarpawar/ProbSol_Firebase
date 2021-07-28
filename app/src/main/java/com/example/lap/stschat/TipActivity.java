package com.example.lap.stschat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pd.chocobar.ChocoBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tfb.fbtoast.FBToast;

import java.util.HashMap;

public class TipActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference UsersRef;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private EditText tip;
    private Button updatelink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);



        mToolbar = (Toolbar) findViewById(R.id.tip_bar_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add tip link");



        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = mAuth.getCurrentUser().getUid();
        tip = (EditText) findViewById(R.id.addtip);
        updatelink = (Button) findViewById(R.id.updatelink);


        RetriveUserInfo();


        updatelink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UpdateSettings();

            }
        });


    }



    private void UpdateSettings()
    {
        String setTip =  tip.getText().toString();


        if(TextUtils.isEmpty(setTip)) {
            FBToast.warningToast(TipActivity.this,"Please enter tip link first...", FBToast.LENGTH_SHORT);
        }
        if(Patterns.WEB_URL.matcher(setTip).matches())
        {



            HashMap<String,Object> profileMap = new HashMap<>();

            profileMap.put("tip",setTip);

            UsersRef.child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {


                                FBToast.successToast(TipActivity.this,"Tip Link Updated Successfully...", FBToast.LENGTH_SHORT);

                            }
                            else
                            {
                                String message = task .getException().toString();
                                //Toast.makeText(TipActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                                FBToast.errorToast(TipActivity.this,"Error" + message, FBToast.LENGTH_SHORT);

                            }
                        }
                    });



        }

        else

        {
            FBToast.warningToast(TipActivity.this,"Please enter Valid paypal.me link first...", FBToast.LENGTH_SHORT);


        }

    }






    private void RetriveUserInfo()
    {
        UsersRef.child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("tip")))
                        {
                            String retrivetip = dataSnapshot.child("tip").getValue().toString();
                            tip.setText(retrivetip);



                        }

                        else
                        {
                            //userName.setVisibility(View.VISIBLE);

                            ChocoBar.builder().setActivity(TipActivity.this)
                                    .setText("Please enter tip link to receive tip from other users")
                                    .setDuration(ChocoBar.LENGTH_INDEFINITE)
                                    .setActionText(android.R.string.ok)
                                    .orange()   // in built red ChocoBar
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}