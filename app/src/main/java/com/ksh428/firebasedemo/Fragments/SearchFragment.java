package com.ksh428.firebasedemo.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.hendraanggrian.appcompat.widget.SocialEditText;
import com.ksh428.firebasedemo.Adapter.TagAdapter;
import com.ksh428.firebasedemo.Adapter.UserAdapter;
import com.ksh428.firebasedemo.Model.User;
import com.ksh428.firebasedemo.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<User> musers;

    private SocialEditText search_bar;

    private UserAdapter userAdapter;
    private RecyclerView recyclerviewtags;
    private List<String> mHashTags;
    private List<String> mHashTagsCount;
    private TagAdapter tagAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView=view.findViewById(R.id.recyclerviewusers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        search_bar=view.findViewById(R.id.searchbar);
        musers=new ArrayList<>();

        mHashTags=new ArrayList<>();
        mHashTagsCount= new ArrayList<>();

        tagAdapter=new TagAdapter(getContext(),mHashTags,mHashTagsCount);
       // recyclerviewtags.setAdapter(tagAdapter);
        userAdapter=new UserAdapter(getContext(),musers,true);
        recyclerView.setAdapter(userAdapter);

        recyclerviewtags=view.findViewById(R.id.recyclerviewtags);
        recyclerviewtags.setHasFixedSize(true);
        recyclerviewtags.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerviewtags.setAdapter(tagAdapter);



        readusers();//only works if there is no text on searchbar
        readTags();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });

        return view;
    }

    private void readTags() {
        FirebaseDatabase.getInstance().getReference().child("Hashtags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHashTags.clear();
                mHashTagsCount.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    mHashTags.add(snapshot1.getKey());
                    mHashTagsCount.add(snapshot1.getChildrenCount()+"");//getChildrencount returns long so use this way of converting it to string
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readusers() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(search_bar.getText().toString())){
                    musers.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        User user=snapshot1.getValue(User.class);
                        musers.add(user);//adds
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void  searchUser(String s){
        Query query=FirebaseDatabase.getInstance().getReference().child("users")
                .orderByChild("username").startAt(s).endAt(s +"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musers.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    User user=snapshot1.getValue(User.class);
                    musers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void filter(String text){
        List<String> msearchtags=new ArrayList<>();
        List<String> msearchtagscount=new ArrayList<>();
        for(String s:mHashTags){
            if(s.toLowerCase().contains(text.toLowerCase())){
                msearchtags.add(s);
               msearchtagscount.add(mHashTagsCount.get(mHashTags.indexOf(s)));
            }
        }
        tagAdapter.filter(msearchtags,msearchtagscount);
    }
}
