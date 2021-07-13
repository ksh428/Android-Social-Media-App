package com.ksh428.firebasedemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ksh428.firebasedemo.Fragments.PostDetailFragment;
import com.ksh428.firebasedemo.Model.Post;
import com.ksh428.firebasedemo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends  RecyclerView.Adapter<PhotoAdapter.viewHolder>{
    private Context mcontext;
    private List<Post> mposts;

    public PhotoAdapter(Context mcontext, List<Post> mposts) {
        this.mcontext = mcontext;
        this.mposts = mposts;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.photo_item,parent,false);

        return new PhotoAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final Post post=mposts.get(position);
        Picasso.get().load(post.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.postimage);
        holder.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postid",post.getPostId()).apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment()).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mposts.size();
    }

    public  class  viewHolder extends RecyclerView.ViewHolder{
        ImageView postimage;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            postimage=itemView.findViewById(R.id.postimage);
        }
    }
}
