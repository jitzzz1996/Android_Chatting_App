package com.example.jitesh.android_brill_training_task_2;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity
{
    private ImageView mProfileImageView;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mProfileSendRequestButton,mProfileDeclineRequest;
    private DatabaseReference mUsersdatabase,mFriendRequestdatabase,mFriendDatabase,mNotificationDatabase,mRootRefrence,mUserref;
    private ProgressDialog mprogressdialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrent_User;
    private String current_state,uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //uuid is the uid of the profile which is clicked by us
        uuid=getIntent().getStringExtra("user_id");
        mUsersdatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uuid);
        mProfileImageView=findViewById(R.id.profileImageview);
        mProfileName=findViewById(R.id.profilename);
        mProfileStatus=findViewById(R.id.profilestatus);
        mProfileFriendsCount=findViewById(R.id.profileFriends);
        mProfileSendRequestButton=findViewById(R.id.profilesendrequest);
        mProfileDeclineRequest=findViewById(R.id.profiledeclinerequest);
        mFriendRequestdatabase=FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        mFriendRequestdatabase.keepSynced(true);
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendDatabase.keepSynced(true);
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("Notifications");
        mRootRefrence=FirebaseDatabase.getInstance().getReference();
        mRootRefrence.keepSynced(true);
        mAuth=FirebaseAuth.getInstance();
        mUserref= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        //this is uid of the user who is logged in
        mCurrent_User= FirebaseAuth.getInstance().getCurrentUser();

        current_state="not_friends";
        mProfileDeclineRequest.setVisibility(View.INVISIBLE);
        mProfileDeclineRequest.setEnabled(false);



        mprogressdialog=new ProgressDialog(this);
        mprogressdialog.setTitle("Loading User Data");
        mprogressdialog.setMessage("Please Wait While We Load.....");
        mprogressdialog.setCancelable(false);
        mprogressdialog.show();


        mUsersdatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String display_name=dataSnapshot.child("Name").getValue().toString();
                String status=dataSnapshot.child("Status").getValue().toString();
                String image=dataSnapshot.child("Image").getValue().toString();


                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.blankprofile).into(mProfileImageView);

                //......friends list/ request feature.......
                mFriendRequestdatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild(uuid))
                        {
                            String req_type=dataSnapshot.child(uuid).child("RequestType").getValue().toString();
                            if(req_type.equals("received"))
                            {
                                current_state="req_received";
                                mProfileSendRequestButton.setText("Accept Friend Request");
                                mProfileDeclineRequest.setVisibility(View.VISIBLE);
                                mProfileDeclineRequest.setEnabled(true);
                            }
                            else if(req_type.equals("sent"))
                            {
                                current_state="req_sent";
                                mProfileSendRequestButton.setText("Cancel Friend Request");
                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);
                            }
                            mprogressdialog.dismiss();

                        }
                        else
                        {
                            mFriendDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override

                                public void onDataChange(DataSnapshot dataSnapshot)
                                {

                                    if(dataSnapshot.hasChild(uuid))
                                    {
                                        current_state="friends";
                                        mProfileSendRequestButton.setText("Unfriend it");
                                        mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                        mProfileDeclineRequest.setEnabled(false);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {

                                }
                            });
                            mprogressdialog.dismiss();

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        mprogressdialog.dismiss();

                    }
                });




            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        mProfileSendRequestButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mProfileSendRequestButton.setEnabled(false);

                //....................Not Friends State...................
                if(current_state.equals("not_friends"))
                {


                    DatabaseReference newNotificationRef=mRootRefrence.child("Notifications").child(uuid).push();
                    String newNotificationId=newNotificationRef.getKey();
                    HashMap<String,String>notificationData=new HashMap<>();
                    notificationData.put("From",mCurrent_User.getUid());
                    notificationData.put("Type","request");


                    Map requestMap=new HashMap();
                    requestMap.put("FriendRequests/"+mCurrent_User.getUid()+"/"+uuid+"/RequestType","sent");
                    requestMap.put("FriendRequests/"+uuid+"/"+mCurrent_User.getUid()+"/RequestType","received");
                    requestMap.put("Notifications/"+uuid+"/"+newNotificationId,notificationData);

                    mRootRefrence.updateChildren(requestMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {

                           if(databaseError==null)
                           {
                               current_state="req_sent";
                               mProfileSendRequestButton.setText("Cancel Friend Request");
                           }
                           else
                           {
                               String error=databaseError.getMessage();
                               Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_LONG).show();
                           }
                            mProfileSendRequestButton.setEnabled(true);


                        }
                    });

                }

                //....................Cancel Request State...................

                if(current_state.equals("req_sent"))
                {
                    Map CancelReqMap=new HashMap();

                    CancelReqMap.put("FriendRequests/"+mCurrent_User.getUid()+"/"+uuid,null);
                    CancelReqMap.put("FriendRequests/"+uuid+"/"+mCurrent_User.getUid(),null);

                    mRootRefrence.updateChildren(CancelReqMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {

                            if(databaseError==null)
                            {
                                current_state="not_friends";
                                mProfileSendRequestButton.setText("Send Friend Request");

                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);

                            }

                            else
                            {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_LONG).show();

                            }
                            mProfileSendRequestButton.setEnabled(true);


                        }
                    });


                }


                //....................Request Received State...................

                if(current_state.equals("req_received"))
                {
                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());

                    Map friendMap=new HashMap();
                    friendMap.put("Friends/"+mCurrent_User.getUid()+"/"+uuid+"/Date",currentDate);
                    friendMap.put("Friends/"+uuid+"/"+mCurrent_User.getUid()+"/Date",currentDate);


                    friendMap.put("FriendRequests/"+mCurrent_User.getUid()+"/"+uuid,null);
                    friendMap.put("FriendRequests/"+uuid+"/"+mCurrent_User.getUid(),null);


                    mRootRefrence.updateChildren(friendMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {

                            if(databaseError==null)
                            {
                                current_state="friends";
                                mProfileSendRequestButton.setText("Unfriend it");

                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);

                            }

                            else
                            {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_LONG).show();

                            }
                            mProfileSendRequestButton.setEnabled(true);


                        }
                    });

                }

                //............UnFriend it.....................................

                if(current_state.equals("friends"))
                {
                   Map unfriendMap=new HashMap();
                   unfriendMap.put("Friends/"+mCurrent_User.getUid()+"/"+uuid,null);
                   unfriendMap.put("Friends/"+uuid+"/"+mCurrent_User.getUid(),null);


                    mRootRefrence.updateChildren(unfriendMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {

                            if(databaseError==null)
                            {
                                current_state="not_friends";
                                mProfileSendRequestButton.setText("Send Friend Request");

                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);

                            }

                            else
                            {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_LONG).show();

                            }
                            mProfileSendRequestButton.setEnabled(true);


                        }
                    });



                }



            }
        });

        mProfileDeclineRequest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                //............Decline Request.....................................

                    Map declineReqMap=new HashMap();
                declineReqMap.put("FriendRequests/"+mCurrent_User.getUid()+"/"+uuid,null);
                declineReqMap.put("FriendRequests/"+uuid+"/"+mCurrent_User.getUid(),null);


                    mRootRefrence.updateChildren(declineReqMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {

                            if(databaseError==null)
                            {
                                current_state="not_friends";
                                mProfileSendRequestButton.setText("Send Friend Request");

                                mProfileDeclineRequest.setVisibility(View.INVISIBLE);
                                mProfileDeclineRequest.setEnabled(false);

                            }

                            else
                            {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_LONG).show();

                            }
                            mProfileSendRequestButton.setEnabled(true);


                        }
                    });





            }
        });







    }//end of on Create


  /*  @Override
    protected void onStart()
    {
        super.onStart();
        mUserref.child("Online").setValue(true);

    }*/


   // @Override
    /*protected void onStop()
    {
        super.onStop();
        mUserref.child("Online").setValue(false);

    }*/
}
