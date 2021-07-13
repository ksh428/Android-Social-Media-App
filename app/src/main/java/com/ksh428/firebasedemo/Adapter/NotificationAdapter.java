package com.ksh428.firebasedemo.Adapter;

import android.app.Notification;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ksh428.firebasedemo.Fragments.PostDetailFragment;
import com.ksh428.firebasedemo.Fragments.ProfileFragment;
import com.ksh428.firebasedemo.Model.Notifications;
import com.ksh428.firebasedemo.Model.Post;
import com.ksh428.firebasedemo.Model.User;
import com.ksh428.firebasedemo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends  RecyclerView.Adapter<NotificationAdapter.viewHolder>{
    Context mcontext;
    List<Notifications> mnotifications;

    public NotificationAdapter(Context mcontext, List<Notifications> mnotifications) {
        this.mcontext = mcontext;
        this.mnotifications = mnotifications;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.notification_item,parent,false);
        return new NotificationAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final Notifications notifications=mnotifications.get(position);
        getUser(holder.imageprofile,holder.username,notifications.getUserid());
        holder.comment.setText(notifications.getText());
        if(notifications.isIspost()){
            holder.postimage.setVisibility(View.VISIBLE);
            getpostimage(holder.postimage,notifications.getPostid());
        }else{
            holder.postimage.setVisibility(View.GONE);//in GONE the space is also freed but not in INVISIBLE
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notifications.isIspost()){
                    mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postid",notifications.getPostid()).apply();
                    //METHOD TO CALL A FRAGMENT FROM ANOTHER FRGMANET
                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment()).commit();

                }else{
                    mcontext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",notifications.getUserid()).apply();
                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

                }
            }
        });

    }

    private void getpostimage(final ImageView imageView, String postid) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post=snapshot.getValue(Post.class);
                Picasso.get().load(post.getImageurl()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser(final ImageView imageView, final TextView textView, String userid) {
        FirebaseDatabase.getInstance().getReference().child("users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);
                if(user.getImageurl().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
                else Picasso.get().load(user.getImageurl()).into(imageView);

                textView.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mnotifications.size();
    }

    public class  viewHolder extends RecyclerView.ViewHolder{
        public ImageView imageprofile;
        ImageView postimage;
        TextView username;
        TextView comment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageprofile=itemView.findViewById(R.id.imageprofile);
            postimage=itemView.findViewById(R.id.post_image);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);


        }
    }
}
