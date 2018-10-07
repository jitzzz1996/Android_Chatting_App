package com.example.jitesh.android_brill_training_task_2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity
{

    private RelativeLayout relativeLayout;
    private DatabaseReference mDatabseRefrence,databaseRef,mUserref;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private StorageReference mStoarage;
    private TextView mName,mStatus;
    private Bitmap thumb_bitmap;
    private ProgressDialog mprogressdialog;
    private String uuid,ImageURl;
    private CircleImageView mcircleImageView;
    private Button btnChangestatus,btnChangeImage;
    private static final int GALLERY_PICK=2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        uuid=mCurrentUser.getUid();
        mDatabseRefrence= FirebaseDatabase.getInstance().getReference().child("Users").child(uuid);
        mDatabseRefrence.keepSynced(true);
        mStoarage= FirebaseStorage.getInstance().getReference();
        mprogressdialog=new ProgressDialog(this);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uuid);
        mAuth=FirebaseAuth.getInstance();
        mUserref= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());




        relativeLayout=findViewById(R.id.rlayout);
        mName=findViewById(R.id.display);
        mStatus=findViewById(R.id.txtStatus);
        mcircleImageView=findViewById(R.id.dp);
        btnChangestatus=findViewById(R.id.btnChangeStatus);
        btnChangeImage=findViewById(R.id.btnChangeImage);

        mDatabseRefrence.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.w("DataSnapshot",dataSnapshot.toString());
                String name=dataSnapshot.child("Name").getValue().toString();
                final String image=dataSnapshot.child("Image").getValue().toString();
                String status=dataSnapshot.child("Status").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                //for rertriveing image offline
                Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                        placeholder(R.drawable.blankprofile).into(mcircleImageView, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {

                    }

                    @Override
                    public void onError()
                    {
                        //for rertriveing image on line
                        Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.blankprofile).into(mcircleImageView);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        btnChangestatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String status_value =mStatus.getText().toString().trim();
                Intent intent=new Intent(SettingsActivity.this,StatusActivity.class);
                intent.putExtra("status_value",status_value);
                startActivity(intent);
            }
        });


        btnChangeImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);

                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);*/

            }
        });


    }


   /* @Override
    protected void onStart()
    {
        super.onStart();
        mUserref.child("Online").setValue(true);

    }


    @Override
    protected void onStop()
    {
        super.onStop();
        mUserref.child("Online").setValue(false);

    }*/



    private void updateValue(String key,String value)
    {
        databaseRef.child(key).setValue(value).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    mprogressdialog.dismiss();
                    Snackbar.make(relativeLayout,"Uploading Successful",Snackbar.LENGTH_LONG).show();

                }
                else
                {
                    mprogressdialog.dismiss();
                    Snackbar.make(relativeLayout,"Error Occurs While Uploading",Snackbar.LENGTH_LONG).show();

                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null)
        {


            Uri imageUri = data.getData();
            Log.w("ImageUri",imageUri.toString());

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                mprogressdialog.setTitle("Uploading Image......");
                mprogressdialog.setMessage("Please Wait......");
                mprogressdialog.show();
                mprogressdialog.setCancelable(false);

                Uri resultUri = result.getUri();

                File thumb_filepath=new File(resultUri.getPath());
                Log.w("CroppedImageUri",resultUri.toString());

                try
                {
                    thumb_bitmap = new Compressor(this).
                    setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumb_filepath);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte=baos.toByteArray();

                StorageReference filePath=mStoarage.child("Profile_Images").child(uuid+".jpg");
                final StorageReference filepath_thumb=mStoarage.child("Profile_Images").child("Thumbs").child(uuid+".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            ImageURl = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask=filepath_thumb.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task)
                                {
                                    if(thumb_task.isSuccessful())
                                    {
                                        String thumb_donloadurl=thumb_task.getResult().getDownloadUrl().toString();
                                        updateValue("Image",ImageURl);
                                        updateValue("ThumbImage",thumb_donloadurl);

                                    }
                                    else
                                    {
                                        Snackbar.make(relativeLayout,"ThumbNail Not uploaded ",Snackbar.LENGTH_LONG).show();

                                    }

                                }
                            });


                        }
                        else
                        {
                            Snackbar.make(relativeLayout,"Image Not uploaded ",Snackbar.LENGTH_LONG).show();

                        }
                    }
                });






               /* uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {

                        Snackbar.make(relativeLayout,"Image uploaded Successfully",Snackbar.LENGTH_LONG).show();
                        //adding an upload to firebase database

                        String newURl = taskSnapshot.getDownloadUrl().toString();
                        DatabaseReference databaseRef;
                        databaseRef = FirebaseDatabase.getInstance().getReference();
                        Log.w("uuid",uuid+" ----> " +newURl);
                        databaseRef.child(uuid).child("Image").setValue(newURl);
                        mprogressdialog.dismiss();





                    }
                });*/


               /* //set imageview from local file path
                String[] pathcolumn={MediaStore.Images.Media.DATA};
                Cursor cursor=getContentResolver().query(resultUri,pathcolumn,null,null,null);
                cursor.moveToFirst();

                int index=cursor.getColumnIndex(pathcolumn[0]);
                String pp=cursor.getString(index);
                cursor.close();
                mcircleImageView.setImageBitmap(BitmapFactory.decodeFile(pp));*/

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Log.w("Error",error.toString());
            }
        }
    }




}
