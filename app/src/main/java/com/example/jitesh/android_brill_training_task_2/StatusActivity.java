package com.example.jitesh.android_brill_training_task_2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StatusActivity extends AppCompatActivity
{
    private TextInputLayout txtStatusInputLayout;
    private EditText txtStatusInput;
    private Button btnInputStatus;
    private String uuid;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase;
    private StorageReference mstorage;
    private ProgressDialog mprogressdialog;
    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        txtStatusInput=findViewById(R.id.txtinputstatus);
        txtStatusInputLayout=findViewById(R.id.txtinputstatuslayout);
        btnInputStatus=findViewById(R.id.btninputstatus);
        relativeLayout=findViewById(R.id.rlayout);

        mprogressdialog=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mstorage= FirebaseStorage.getInstance().getReference();
        uuid=mAuth.getUid();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uuid);

        String status_value=getIntent().getStringExtra("status_value");
        txtStatusInput.setText(status_value);

        btnInputStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                    if(txtStatusInput.getText().toString().trim().length()>
                            1)
                    {
                        mprogressdialog.setTitle("Updating...");
                        mprogressdialog.setMessage("Please Wait......");
                        mprogressdialog.show();
                        mprogressdialog.setCancelable(false);
                        String status=txtStatusInput.getText().toString().trim();
                        mdatabase.child("Status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    mprogressdialog.dismiss();
                                    Snackbar.make(relativeLayout,"Status Updated Successfully",Snackbar.LENGTH_LONG).show();
                                    Intent intent=new Intent(StatusActivity.this,SettingsActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    mprogressdialog.dismiss();
                                    Snackbar.make(relativeLayout,"Internet Issues Occur",Snackbar.LENGTH_LONG).show();


                                }
                            }
                        });
                    }
                    else
                    {
                        txtStatusInputLayout.setError("Pls Enter A valid Status");
                        txtStatusInput.requestFocus();
                    }

            }
        });

    }
}
