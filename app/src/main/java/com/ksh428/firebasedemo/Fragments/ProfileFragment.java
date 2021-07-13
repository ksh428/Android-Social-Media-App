package com.ksh428.firebasedemo.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ksh428.firebasedemo.Adapter.PhotoAdapter;
import com.ksh428.firebasedemo.Adapter.PostAdapter;
import com.ksh428.firebasedemo.EditProfileActivity;
import com.ksh428.firebasedemo.FollowersActivity;
import com.ksh428.firebasedemo.Model.Post;
import com.ksh428.firebasedemo.Model.User;
import com.ksh428.firebasedemo.OptionsActivity;
import com.ksh428.firebasedemo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {
    private RecyclerView recyclerviewsaves;
    private PhotoAdapter postAdaptersaves;
    List<Post> mysaveposts;

    private RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    List<Post> myphotolist;
    private CircleImageView imageProfile;
    private ImageView options;
    private TextView followers;
    private TextView following;
    private TextView posts;
    private TextView fullname;
    private TextView bio;
    private TextView username;
    private Button editprofile;

    private ImageView myPictures;
    private ImageView savedPictures;
    FirebaseUser fuser;
    String profileid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String data =getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId","none");//
        //int no=getContext().getSharedPreferences("CHECK1",Context.MODE_PRIVATE).getInt("checkno1",0);

        if(data.equals("none")){//current user profile
            profileid=fuser.getUid();
        }else{
            profileid=data;//profile of the user whose comment has been clicked by the current user
        }
        imageProfile = view.findViewById(R.id.imageprofile);
        options = view.findViewById(R.id.options);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        posts = view.findViewById(R.id.posts);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.mypictures);
        savedPictures = view.findViewById(R.id.savepictures);
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        editprofile=view.findViewById(R.id.editprofile);
        recyclerView=view.findViewById(R.id.recyclerviewpictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new GridLayoutManager(getContext(),3));
        myphotolist=new ArrayList<>();
        recyclerviewsaves=view.findViewById(R.id.recyclerviewsaved);
        recyclerviewsaves.setHasFixedSize(true);
        recyclerviewsaves.setLayoutManager(new GridLayoutManager(getContext(),3));
        mysaveposts=new ArrayList<>();
        postAdaptersaves=new PhotoAdapter(getContext(),mysaveposts);
        recyclerviewsaves.setAdapter(postAdaptersaves);
        photoAdapter=new PhotoAdapter(getContext(),myphotolist);
        recyclerView.setAdapter(photoAdapter);


        userinfo();
        getfollowersandfollwoingcount();
        getpostcount();
        myPhotos();//gets the photos of current user
        getsavedposts();
        if(profileid.equals(fuser.getUid())){//own profile
            editprofile.setText("EDIT PROFILE");
        }else{
            checkfollowingstatus();
        }

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btntext=editprofile.getText().toString();
                if(btntext.equals("EDIT PROFILE")){
                                       startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else{
                    if(btntext.equals("Follow")){
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fuser.getUid()).child("Following").
                                child(profileid).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("Followers").child(fuser.getUid()).setValue(true);
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fuser.getUid()).child("Following").
                                child(profileid).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("Followers").child(fuser.getUid()).removeValue();

                    }
                }
            }
        });
        recyclerView.setVisibility(View.VISIBLE);
        recyclerviewsaves.setVisibility(View.GONE);
        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerviewsaves.setVisibility(View.GONE);

            }
        });
        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerviewsaves.setVisibility(View.VISIBLE);
            }
        });
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","following");
                startActivity(intent);
            }
        });
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });

        return view;
    }

    private void getsavedposts() {
        final ArrayList<String> savedids=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap:snapshot.getChildren()){
                    savedids.add(snap.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        mysaveposts.clear();
                        for(DataSnapshot snap1:snapshot1.getChildren()){
                            Post post=snap1.getValue(Post.class);
                            for(String id:savedids){
                                if(post.getPostId().equals(id)){
                                    mysaveposts.add(post);
                                }
                            }

                        }
                        postAdaptersaves.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myphotolist.clear();
                for(DataSnapshot snap:snapshot.getChildren()){
                    Post post=snap.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        myphotolist.add(post);
                    }
                }
                Collections.reverse(myphotolist);//used to reverse a list/arraylist
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkfollowingstatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(fuser.getUid()).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileid).exists()){
                    editprofile.setText("Following");
                }else{
                    editprofile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getpostcount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int c=0;
                for(DataSnapshot snap:snapshot.getChildren()){
                    Post post=snap.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        c++;
                    }
                }
                posts.setText(String.valueOf(c));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getfollowersandfollwoingcount() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid);
        ref.child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(snapshot.getChildrenCount()+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userinfo() {
        FirebaseDatabase.getInstance().getReference().child("users").child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).into(imageProfile);
                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
