package com.ksh428.firebasedemo.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ksh428.firebasedemo.Adapter.PostAdapter;
import com.ksh428.firebasedemo.Model.Post;
import com.ksh428.firebasedemo.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    RecyclerView recyclerviewposts;
    PostAdapter postAdapter;
    List<Post> postlist;
    List<String> followinglist;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerviewposts=view.findViewById(R.id.recyclerviewposts);
        recyclerviewposts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);//latest post available on top
        linearLayoutManager.setReverseLayout(true);
        recyclerviewposts.setLayoutManager(linearLayoutManager);
        postlist=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postlist);
        recyclerviewposts.setAdapter(postAdapter);
        followinglist=new ArrayList<>();
        checkfollowinguser();//all the users that are followed by the current user.to see the posts of the current user + all the users followed by the curr user
        return view;

    }

    private void checkfollowinguser() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followinglist.clear();
                for(DataSnapshot snap:snapshot.getChildren()){
                    followinglist.add(snap.getKey());
                }
                followinglist.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                readposts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readposts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlist.clear();
                for(DataSnapshot snap:snapshot.getChildren()){
                    Post post=snap.getValue(Post.class);
                    for(String id:followinglist){
                        if(post.getPublisher().equals(id)){
                            postlist.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
