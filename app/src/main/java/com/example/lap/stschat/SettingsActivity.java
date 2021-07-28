package com.example.lap.stschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.DrmStore;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;


import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{
    private Button UpdateAccountSettings;
    private EditText userName, userStatus;
    private RoundedImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef,UsersRef;
    private static final int GalleryPick = 1;

    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingbar;
    private android.support.v7.widget.Toolbar SettingsToolbar;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
       // RootRef.child("Users").keepSynced(true);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            UsersRef.keepSynced(true);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");



        InitializeField();
        // userName.setVisibility(View.INVISIBLE);



        UpdateAccountSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UpdateSettings();
                Animation anim = android.view.animation.AnimationUtils.loadAnimation(UpdateAccountSettings.getContext(),R.anim.shake);
                anim.setDuration(100L);
                UpdateAccountSettings.startAnimation(anim);
            }
        });

        RetriveUserInfo();


        userProfileImage.setOnClickListener(new View.OnClickListener() {        //profile Image
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);
            }
        });
    }










    private void InitializeField()
    {
        UpdateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (RoundedImageView) findViewById(R.id.set_profile_image);
        loadingbar = new ProgressDialog(this);
        SettingsToolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)//profile Image code
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if(resultCode == RESULT_OK)
            {
                loadingbar.setTitle("Set Profile Image");
                loadingbar.setMessage("Please wait, your profile image is updating...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                Uri resultUri = result.getUri();


                StorageReference filepath = UserProfileImageRef.child(currentUserID + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            //Toast.makeText(SettingsActivity.this, "Profile Image uploaded Successfully", Toast.LENGTH_SHORT).show();

                            final  String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UsersRef.child(currentUserID).child("image")
                                    .setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(SettingsActivity.this, "Image Saved In database , Successfully: It will be displayed after update", Toast.LENGTH_LONG).show();
                                                loadingbar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });




                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });


            }

        }

    }

    private void UpdateSettings()
    {
        String setUserName =  userName.getText().toString();
        String setStatus =  userStatus.getText().toString();

        if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "Please Write Your UserName First....", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(setStatus))
        {
            Toast.makeText(this, "Please Write Your Field....", Toast.LENGTH_SHORT).show();
        }
        else

        {

            HashMap<String,Object> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUserName);
            profileMap.put("status",setStatus);
           UsersRef.child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task .getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
    private void RetriveUserInfo()
    {
        UsersRef.child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                        {
                            String retriveUserName = dataSnapshot.child("name").getValue().toString();
                            String retriveStatus= dataSnapshot.child("status").getValue().toString();
                           final String retriveProfileImage = dataSnapshot.child("image").getValue().toString();

                            userName.setText(retriveUserName);
                            userStatus.setText(retriveStatus);
                           // Picasso.get().load(retriveProfileImage).into(userProfileImage);
                            Picasso.get().load(retriveProfileImage).networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(userProfileImage, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(retriveProfileImage).into(userProfileImage);
                                        }
                                    });

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retriveUserName = dataSnapshot.child("name").getValue().toString();
                            String retriveStatus= dataSnapshot.child("status").getValue().toString();

                            userName.setText(retriveUserName);
                            userStatus.setText(retriveStatus);

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                        {
                            final String retriveProfileImage1 = dataSnapshot.child("image").getValue().toString();


                            Picasso.get().load(retriveProfileImage1).into(userProfileImage);

                        }
                        else
                        {
                            //userName.setVisibility(View.VISIBLE);

                            Toast.makeText(SettingsActivity.this, "Please set and Update Your Profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent= new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }





}

