package com.example.jitesh.android_brill_training_task_2;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private Toolbar mtoolbar;
    private TextInputLayout mPasswordlayout,mCnfPasswordlayout,mDisplayNamelayout,mEmaillayout;
    private EditText mPassword,mCnfPassword,mDisplayName,mEmail;
    private EditText mDate;
    private Spinner mSpinner;
    private Button mCreateButton;
    private String displayname,email,password,uuid,deviceToken,gendervar="",dob,status="Hey! There I am Using Chat Buddy App",image="default",thumbimage="Default";
    private ProgressDialog mprogressdialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase;
    private StorageReference mstorage;
    private RelativeLayout rlayout;

    private Calendar calendar;
    private int year, month, day;
    private DatePickerDialog.OnDateSetListener mdatelistener;

    // Declaring the String Array with the Text Data for the Spinners
    private String[] gender = { "Select a gender", "Male", "Female","Other"};
   /* // Declaring the Integer Array with resourse Id's of Images for the Spinners
    private Integer[] images = { 0, R.drawable.ic_malewhite, R.drawable.ic_femalewhite,R.drawable.ic_other};*/



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mPasswordlayout=findViewById(R.id.reg_password);
        mCnfPasswordlayout=findViewById(R.id.reg_Cnf_Password);
        mDisplayNamelayout=findViewById(R.id.reg_display_Name);
        mEmaillayout =findViewById(R.id.reg_email);
        mDisplayName=findViewById(R.id.etregname);
        mEmail=findViewById(R.id.etregmail);
        mPassword=findViewById(R.id.etregpassword);
        mCreateButton=findViewById(R.id.reg_Button_Create);
        mCnfPassword=findViewById(R.id.etregcnfpaasword);
        mDate=findViewById(R.id.reg_Date);
        mSpinner=findViewById(R.id.reg_Spinner);

        mprogressdialog=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mstorage= FirebaseStorage.getInstance().getReference();
        mdatabase= FirebaseDatabase.getInstance().getReference();
        rlayout=findViewById(R.id.rlayout);


      /*  mtoolbar=findViewById(R.id.registerpagetoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Register");*/



        mDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //this 4 lines code for calendar
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(RegisterActivity.this,android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,mdatelistener,year,month,day);
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                dialog.show();
            }
        });

        mdatelistener=new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
            {
                i1+=1;
                String date=i2+"/"+i1+"/"+i;
                mDate.setText(date);
            }
        };



        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                gendervar=adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(SignupActivity.this,gendervar,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });


        MyAdapter _objMyAdapter= new MyAdapter(this, R.layout.custom, gender);
        // Setting a Custom Adapter to the Spinner
        mSpinner.setAdapter(_objMyAdapter);





        mCreateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.w("150","Inside Create button calling");
                validdate();
            }
        });

    }//end of oncreate


    //Code for form validation
    public static boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    private void validdate()
    {

        if (mDisplayName.getText().toString().length() >= 3)
        {
           if (isValidEmail(mEmail.getText().toString().trim()))
            {
                if (mPassword.getText().toString().length() >= 6) {
                    if (mPassword.getText().toString().equals(mCnfPassword.getText().toString())) {

                        if (!mDate.getText().toString().equals("DD/MM/YYYY")) {
                            String name = "Select a gender";
                            if (mSpinner.getSelectedItem() != name)
                            {
                               // name = (String) mSpinner.getSelectedItem();
                                mprogressdialog.setMessage("Please Wait......");
                                mprogressdialog.show();
                                mprogressdialog.setCancelable(false);
                                //Toast.makeText(this,"validation sucess",Toast.LENGTH_LONG).show();
                                auth();


                            } else {
                                Snackbar.make(rlayout, "Please Select Gender", Snackbar.LENGTH_LONG).show();

                            }

                        } else {
                            mDate.setError("Please Enter your Date of birth");
                            mDate.requestFocus();
                        }
                    }
                    else
                    {
                        mCnfPasswordlayout.setError("Password Not matched");
                        mCnfPassword.requestFocus();
                    }

                }
                else
                {
                        mPasswordlayout.setError("Password should be atleast of 6 characters");
                        mPassword.requestFocus();
                }

            }
            else
            {
                    mEmaillayout.setError("please enter a valid email address");
                    mEmail.requestFocus();
            }

        }
        else
        {
                mDisplayNamelayout.setError("Name should be atleast of 3 characters");
                mDisplayName.requestFocus();
        }

    }//end of validate method

    private void auth()
    {
        email=mEmail.getText().toString().trim();
        password=mPassword.getText().toString().trim();

        try
        {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                // Log.w("Inserting mail","383");
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        uuid=mAuth.getUid();
                        deviceToken= FirebaseInstanceId.getInstance().getToken();

                        Log.w("255", "here");
                        Log.w("Inserting mail", "388");
                        submitdata();

                    }
                    else
                    {
                        mprogressdialog.dismiss();

                        Snackbar.make(rlayout, "Email is already registered", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
        catch (Exception ex)
        {
            Log.w("Exception in auth",ex.getMessage());
        }

    }

    private void submitdata()
    {

        displayname=mDisplayName.getText().toString().trim();
        email=mEmail.getText().toString().trim();
        password=mPassword.getText().toString().trim();
        dob=mDate.getText().toString().trim();

        HashMap<String,String>datamap=new HashMap<String, String>();
        datamap.put("Name",displayname);
        datamap.put("Email",email);
        datamap.put("Password",password);
        datamap.put("DOB",dob);
        datamap.put("Gender", gendervar);
        datamap.put("Status",status);
        datamap.put("Image",image);
        datamap.put("ThumbImage",thumbimage);
        datamap.put("DeviceToken",deviceToken);


        Log.w("Datamap values", datamap.toString());





        mdatabase.child("Users").child(uuid).setValue(datamap).addOnCompleteListener( new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                mprogressdialog.dismiss();
                Snackbar.make(rlayout, "Account Created Successfully", Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });





    }


    // Creating an Adapter Class
    public class MyAdapter extends ArrayAdapter
    {

        public MyAdapter(Context context, int textViewResourceId, String[] objects)
        {
            super(context, textViewResourceId, objects);
        }



        public View getCustomView(int position, View convertView, ViewGroup parent)
        {

            // Inflating the layout for the custom Spinner

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom, parent, false);

            // Declaring and Typecasting the textview in the inflated layout

            TextView tvLanguage = (TextView) layout.findViewById(R.id.tvLanguage);

            // Setting the text using the array
            tvLanguage.setText(gender[position]);

            // Setting the color of the text
            tvLanguage.setTextColor(Color.rgb(75, 180, 225));

            // Declaring and Typecasting the imageView in the inflated layout
           // ImageView img = (ImageView) layout.findViewById(R.id.imgLanguage);

            // Setting an image using the id's in the array

            // Setting Special atrributes for 1st element
            if (position == 0)
            {
                // Removing the image view
                //img.setVisibility(View.GONE);
                // Setting the size of the text
                tvLanguage.setTextSize(20f);
                // Setting the text Color
                tvLanguage.setTextColor(Color.BLACK);

            }
            else
            {
               // img.setImageResource(images[position]);
                tvLanguage.setTextColor(Color.BLACK);


            }

            return layout;
        }

        // It gets a View that displays in the drop down popup the data at the specified position
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        // It gets a View that displays the data at the specified position
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }
    }


}
