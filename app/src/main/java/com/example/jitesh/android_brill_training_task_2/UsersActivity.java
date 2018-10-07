package com.example.jitesh.android_brill_training_task_2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class UsersActivity extends AppCompatActivity
{

    private RecyclerView mUsersList;
    private Toolbar mtoolbar;
    private DatabaseReference musersdatabase;
    FirebaseRecyclerOptions<Users> options;
    private FirebaseRecyclerAdapter<Users,UsersViewHolder>firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mtoolbar=findViewById(R.id.userappbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUsersList=findViewById(R.id.usersList);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        musersdatabase= FirebaseDatabase.getInstance().getReference().child("Users");





    }

    @Override
    protected void onStart()
    {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(musersdatabase, Users.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options)
        {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
            {
                Log.w("in oncreateviewholder","167");
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);
                return new UsersViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, Users model)
            {

                Picasso.with(UsersActivity.this).load(model.getThumbImage()).placeholder(R.drawable.blankprofile).into(holder.adpterimageview);
                holder.textViewEmail.setText(model.getEmail());
                holder.textViewStatus.setText(model.getStatus());
                holder.textViewGender.setText(model.getGender());
                holder.textViewDob.setText(model.getDOB());
                holder.textViewName.setText(model.getName());

               final  String  uuid=getRef(position).getKey();

                holder.mview.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        Intent profileIntent=new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",uuid);
                        startActivity(profileIntent);

                    }
                });

            }
        };
        firebaseRecyclerAdapter.startListening();
        mUsersList.setAdapter(firebaseRecyclerAdapter);


    }


    public class UsersViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        ImageView adpterimageview;
        TextView textViewEmail,textViewGender,textViewDob,textViewName,textViewStatus;

        public UsersViewHolder(@NonNull View itemView)
        {

            super(itemView);
            mview=itemView;

            adpterimageview=(ImageView) itemView.findViewById(R.id.adpterimageview);
            textViewEmail=(TextView)itemView.findViewById(R.id.textViewEmail);
            textViewGender=(TextView)itemView.findViewById(R.id.textViewGender);
            textViewDob=(TextView)itemView.findViewById(R.id.textViewDob);
            textViewName=(TextView)itemView.findViewById(R.id.textViewName);
            textViewStatus=(TextView)itemView.findViewById(R.id.textViewStatus);

        }
    }


}
