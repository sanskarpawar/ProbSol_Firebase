package com.example.lap.stschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class RegisterActivity extends AppCompatActivity
{
    public Button SignUp, RegisterPhoneLogin;
    private EditText UserEmail,UserPassword;
    private TextView AlreadyHaveAccountLink;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private ProgressDialog loadingBar;
    //private String FirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();



        InitializeField();
        AlreadyHaveAccountLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });


        SignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateNewAccount();
            }
        });
        RegisterPhoneLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneLoginIntent = new Intent(RegisterActivity.this, PhoneLoginActivity.class);
                startActivity(phoneLoginIntent);


            }
        });



    }

    private void sendVerificationEmail()
    {

    }

    private void CreateNewAccount()
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


            loadingBar.setTitle("Crating New Account");
            loadingBar.setMessage("Please Wait, While We Are Creating New Account For You...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {






                                SendEmailVerificationMessage();
                                // String currentUserID = mAuth.getCurrentUser().getUid();
                                //RootRef.child("Users").child(currentUserID).setValue("");
                                // Toast.makeText(RegisterActivity.this, "Account Created Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            } else
                            {

                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }




    }

    private void InitializeField()
    {
        SignUp= (Button) findViewById(R.id.Register_button_signin);
        UserEmail = (EditText) findViewById(R.id.RegisterEmail);
        UserPassword = (EditText) findViewById(R.id.RegisterPassword);
        // UserName = (EditText) findViewById(R.id.RegisterName);

        AlreadyHaveAccountLink = (TextView) findViewById(R.id.already_have_account);
        RegisterPhoneLogin = (Button) findViewById(R.id.RegisterLoginWithPhone);

        //ForgotPassword = (TextView) findViewById(R.id.forgatPassword);


        loadingBar = new ProgressDialog(this);


    }

    private  void SendEmailVerificationMessage()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(RegisterActivity.this, "Registration Successful,we've sent you a mail,please Check you Email Account", Toast.LENGTH_SHORT).show();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        String currentUserID = mAuth.getCurrentUser().getUid();
                        RootRef.child("Users").child(currentUserID).setValue("");


                        RootRef.child("Users").child(currentUserID).child("device_token")
                                .setValue(deviceToken);





                        SendUserToLoginActivity();


                        mAuth.signOut();
                    }
                    else
                    {
                        String error = task.getException().getMessage();

                        Toast.makeText(RegisterActivity.this, "  Error:" + error, Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });




        }
    }
    private void SendUserToLoginActivity()
    {
        Intent  LoginActivity= new Intent(RegisterActivity.this, Activity_Login.class);
        LoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginActivity);
        finish();

    }

    private void SendUserToMainActivity()
    {
        Intent  mainIntent= new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
