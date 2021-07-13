package com.ksh428.firebasedemo.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ksh428.firebasedemo.Adapter.PostAdapter;
import com.ksh428.firebasedemo.Model.Post;
import com.ksh428.firebasedemo.R;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {
    String postId;
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    List<Post> postList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_post_detail, container, false);
        postId=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("postid","none");
        recyclerView=view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(postAdapter);
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                postList.add(snapshot.getValue(Post.class));
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
}
}
