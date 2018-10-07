package com.example.jitesh.android_brill_training_task_2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragement extends Fragment
{


    private RecyclerView mFriendList;
    private DatabaseReference mFriendDatabase,mUsersDatbase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;
    FirebaseRecyclerOptions<Friends> options;
    private FirebaseRecyclerAdapter<Friends,FriendsViewHolder>firebaseRecyclerAdapter;

    public FriendsFragement()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        mMainView=inflater.inflate(R.layout.fragment_friends_fragement,container,false);
        mFriendList=mMainView.findViewById(R.id.friends_list);
        mAuth=FirebaseAuth.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();
        mFriendDatabase= FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);
        mUsersDatbase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatbase.keepSynced(true);

        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mFriendDatabase, Friends.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsFragement.FriendsViewHolder>(options)
        {
            @NonNull
            @Override
            public FriendsFragement.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
            {
                Log.w("in oncreateviewholder","167");
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_single_layout,parent,false);
                return new FriendsFragement.FriendsViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(final FriendsFragement.FriendsViewHolder holder, int position, Friends model)
            {

                /*Picasso.with(getContext()).load(model.getThumbImage()).placeholder(R.drawable.blankprofile).into(holder.adpterimageview);
                holder.textViewEmail.setText(model.getEmail());
                holder.textViewStatus.setText(model.getStatus());
                holder.textViewGender.setText(model.getGender());
                holder.textViewDob.setText(model.getDOB());*/
                Log.w("in onBindViewHolder","167");
                final String list_user_id=getRef(position).getKey();

                mUsersDatbase.child(list_user_id).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        final String user_name=dataSnapshot.child("Name").getValue().toString();
                        String userThumb=dataSnapshot.child("ThumbImage").getValue().toString();
                        if(dataSnapshot.hasChild("Online"))
                        {
                            String userOnline=dataSnapshot.child("Online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }
                        holder.setName(user_name);
                        Picasso.with(getContext()).load(userThumb).placeholder(R.drawable.blankprofile).into(holder.userImageView);

                        holder.mview.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};

                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        if(i==0)
                                        {
                                            Intent profileIntent=new Intent(getContext(),ProfileActivity.class);
                                            profileIntent.putExtra("user_id",list_user_id);
                                            startActivity(profileIntent);
                                        }
                                        if(i==1)
                                        {
                                            Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("user_id",list_user_id);
                                            chatIntent.putExtra("user_name",user_name);
                                            startActivity(chatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });

                holder.userStatusView.setText(model.getDateOfFriendship());

                //final  String  uuid=getRef(position).getKey();

                /*holder.mview.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        Intent profileIntent=new Intent(FriendsFragement.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",uuid);
                        startActivity(profileIntent);

                    }
                });*/

            }
        };
        firebaseRecyclerAdapter.startListening();
        mFriendList.setAdapter(firebaseRecyclerAdapter);




    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        ImageView userImageView;
        TextView userNameView,userStatusView;

        public FriendsViewHolder(@NonNull View itemView)
        {

            super(itemView);
            mview=itemView;

            userImageView=(ImageView) itemView.findViewById(R.id.user_single_image);
            userNameView=(TextView)itemView.findViewById(R.id.user_single_name);
            userStatusView=(TextView)itemView.findViewById(R.id.user_single_status);

        }

        public  void setName(String name)
        {
            userNameView=(TextView)itemView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setUserOnline(String online_status)
        {
            ImageView userOnlineView=itemView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true"))
            {
                userOnlineView.setVisibility(View.VISIBLE);
            }
            else
            {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    
}

