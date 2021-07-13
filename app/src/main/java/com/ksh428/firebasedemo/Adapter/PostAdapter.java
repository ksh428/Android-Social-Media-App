package com.ksh428.firebasedemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.ksh428.firebasedemo.CommentActivity;
import com.ksh428.firebasedemo.FollowersActivity;
import com.ksh428.firebasedemo.Fragments.PostDetailFragment;
import com.ksh428.firebasedemo.Fragments.ProfileFragment;
import com.ksh428.firebasedemo.Model.Post;
import com.ksh428.firebasedemo.Model.User;
import com.ksh428.firebasedemo.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.viewHolder>{

    private Context mcontext;
    List<Post> mposts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mcontext, List<Post> mposts) {
        this.mcontext = mcontext;
        this.mposts = mposts;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {

        final Post post=mposts.get(position);
        Picasso.get().load(post.getImageurl()).into(holder.postImage);
        holder.description.setText(post.getDescription());
        FirebaseDatabase.getInstance().getReference().child("users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(user.getImageurl().equals("default")){
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(user.getImageurl()).into(holder.imageProfile);

                }

                holder.username.setText(user.getUsername());
                holder.author.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        isliked(post.getPostId(),holder.like);
        nooflikes(post.getPostId(),holder.noOfLikes);
        getcomments(post.getPostId(),holder.noOfComments);
        issaved(post.getPostId(),holder.save);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                    //show notification
                    addnotification(post.getPostId(),post.getPublisher());
                }else{
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mcontext.startActivity(intent);

            }
        });
        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mcontext.startActivity(intent);

            }
        });
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).removeValue();

                }
                Snackbar snackbar=Snackbar.make(v,"saved",Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcontext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcontext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

            }
        });
        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcontext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

            }
        });
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId",post.getPostId()).apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment()).commit();

            }
        });
        holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, FollowersActivity.class);
                intent.putExtra("id",post.getPublisher());
                intent.putExtra("title","likes");
                mcontext.startActivity(intent);
            }
        });
    }

    private void addnotification(String postId, String publisherId) {
        HashMap<String,Object>map=new HashMap<>();
        map.put("userid",publisherId);
        map.put("text","liked ur post");
        map.put("postid",postId);
        map.put("ispost",true);
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);

    }


    @Override
    public int getItemCount() {
        return mposts.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        public ImageView imageProfile;
        public ImageView postImage;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;

        public TextView username;
        public TextView noOfLikes;
        public TextView author;
        public TextView noOfComments;
        SocialAutoCompleteTextView description;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);

            username = itemView.findViewById(R.id.username);
            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            author = itemView.findViewById(R.id.author);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            description = itemView.findViewById(R.id.description);


        }
    }
    private void issaved(final String postId, final ImageView image) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).exists()){
                    image.setImageResource(R.drawable.ic_saveblack_foreground);
                    image.setTag("saved");


                }else {
                    image.setImageResource(R.drawable.ic_save_foreground);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void isliked(String postid, final ImageView imageView){
        FirebaseDatabase.getInstance().getReference().child("likes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){ //already liked
                    imageView.setImageResource(R.drawable.ic_liked_foreground);
                    imageView.setTag("liked");//imp
                }else{
                    imageView.setImageResource(R.drawable.ic_like_foreground);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void nooflikes(String postid, final TextView text){
        FirebaseDatabase.getInstance().getReference().child("likes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>1)
                text.setText(snapshot.getChildrenCount()+"likes");
                else text.setText(snapshot.getChildrenCount()+"like");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void getcomments(String posstId, final TextView text){
        FirebaseDatabase.getInstance().getReference().child("comments").child(posstId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText("view all "+snapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
