package com.ksh428.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ksh428.firebasedemo.Adapter.UserAdapter;
import com.ksh428.firebasedemo.Model.User;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {
    String id;
    String title;
    List<String> idlist;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> musers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        //////intent from profilefragment
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        musers = new ArrayList<>();
        userAdapter = new UserAdapter(this, musers, false);
        recyclerView.setAdapter(userAdapter);
        idlist = new ArrayList<>();
        switch (title) { //////
            case "followers":
                getfollowers();
                break;
            case "following":
                getfollowing();
                break;
            case "likes":
                getlikes();
                break;
        }


    }
    private void getfollowers() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    idlist.add(snapshot.getKey());///

                }
                showusers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getfollowing() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    idlist.add(snapshot.getKey());///

                }
                showusers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getlikes() {
        FirebaseDatabase.getInstance().getReference().child("likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idlist.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    idlist.add(snapshot.getKey());///

                }
                showusers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showusers() {
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musers.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    for (String id : idlist) {
                        if (user.getId().equals(id)) {
                            musers.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
