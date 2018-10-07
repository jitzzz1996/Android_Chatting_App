package com.example.jitesh.android_brill_training_task_2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity
{

    private TextInputLayout mLoginEmailLayout,mLoginPasswordLayout;
    private EditText mLoginEmail,mLoginPassword;
    private String email,password;
    private ProgressDialog mprogressdialog;
    private Button mLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private ScrollView sView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginEmail=findViewById(R.id.etloginemail);
        mLoginPassword=findViewById(R.id.etloginpassword);
        mLogin=findViewById(R.id.login_button);
        mLoginEmailLayout=findViewById(R.id.login_email_layout);
        mLoginPasswordLayout=findViewById(R.id.login_password_layout);
        sView=findViewById(R.id.sview);


        mAuth = FirebaseAuth.getInstance();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mprogressdialog=new ProgressDialog(this);

        mLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Login();
            }
        });


    }

    //Code for form validation
    public static boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void Login()
    {
        if (isValidEmail(mLoginEmail.getText().toString().trim()))
        {
            if (mLoginPassword.getText().toString().length() >= 6)
            {
                mprogressdialog.setTitle("Logging In");
                mprogressdialog.setMessage("Please Wait While We Check Your Credentials");
                mprogressdialog.show();
                mprogressdialog.setCancelable(false);

                email=mLoginEmail.getText().toString().trim();
                password=mLoginPassword.getText().toString().trim();


                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        mprogressdialog.dismiss();
                        if (task.isSuccessful())
                        {
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            String uuid=mAuth.getCurrentUser().getUid();

                            mUserDatabase.child(uuid).child("DeviceToken").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    Toast.makeText(LoginActivity.this,"Login Successfull",Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(getBaseContext(),MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });


                        }
                        else
                        {
                            Log.w("Login Activity",task.getException().getMessage());
                            Snackbar.make(sView,"Invalid user name or password",Snackbar.LENGTH_LONG).show();
                        }

                    }
                });

            }
            else
            {
                mLoginPasswordLayout.setError("Please enter valid password");
                mLoginPassword.requestFocus();
            }


        }
        else
        {
            mLoginEmailLayout.setError("please enter a valid email address");
            mLoginEmail.requestFocus();
        }

    }

}
