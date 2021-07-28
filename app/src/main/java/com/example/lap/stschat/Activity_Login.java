package com.example.lap.stschat;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Activity_Login extends AppCompatActivity
{

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private Button LoginButton,PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView DontHaveAnAccount1,ForgotPassword;
    private Boolean emailAdressChecker;
    private DatabaseReference UserRef;


    // public void init1()
    //{
    //button_signin= (Button)findViewById(R.id.button_signin);
    //button_signin.setOnClickListener(new OnClickListener()
    //{
    //    @Override
    //  public void onClick(View v)
    //  {

    //Intent toy1=new Intent(Activity_Login.this,MainActivity.class);
    //startActivity(toy1);


    //}
    //});
    //}


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //init1();

        mAuth = FirebaseAuth.getInstance();
        UserRef  = FirebaseDatabase.getInstance().getReference().child("Users");

        InitializeField();

        DontHaveAnAccount1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToRegisterActivity();
            }
        });


        LoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowUserToLogin();

            }
        });
        PhoneLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneLoginIntent = new Intent(Activity_Login.this, PhoneLoginActivity.class);
                startActivity(phoneLoginIntent);

            }
        });


        ForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Activity_Login.this,ResetPasswordActivity.class));
            }
        });
    }

    private void AllowUserToLogin()
    {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        //String name =UserName.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter Email...", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter Password...", Toast.LENGTH_SHORT).show();
        }

        else {
            loadingBar.setTitle("Log In");
            loadingBar.setMessage("Please Wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                VerifyEmailAddress();

                                Toast.makeText(Activity_Login.this, "Logged In Successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else {

                                String message = task.getException().toString();
                                Toast.makeText(Activity_Login.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                        }
                    });


        }
    }

    private void InitializeField()
    {
        LoginButton = (Button) findViewById(R.id.button_signin);
        UserEmail = (EditText) findViewById(R.id.UserEmail);
        UserPassword = (EditText) findViewById(R.id.UserPassword);
        DontHaveAnAccount1 = (TextView) findViewById(R.id.DontHaveAnAccount);
        ForgotPassword = (TextView) findViewById(R.id.forgatPassword);
        PhoneLoginButton = (Button) findViewById(R.id.LoginWithPhone);
        loadingBar = new ProgressDialog(this);
    }


    private  void VerifyEmailAddress()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        emailAdressChecker = user.isEmailVerified();
        if (emailAdressChecker)
        {String currentUserId= mAuth.getCurrentUser().getUid();
            String deviceToken = FirebaseInstanceId.getInstance().getToken();
            UserRef.child(currentUserId).child("device_token")
                    .setValue(deviceToken)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                SendUserToMainActivity();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Please Verify Your Account First", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }

    }


    private void SendUserToMainActivity()
    {
        Intent  mainIntent = new Intent(Activity_Login.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void SendUserToRegisterActivity()
    {
        Intent  RegisterIntent= new Intent(Activity_Login.this, RegisterActivity.class);
        startActivity(RegisterIntent);
    }
}