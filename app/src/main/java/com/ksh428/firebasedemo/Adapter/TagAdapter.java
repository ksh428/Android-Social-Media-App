package com.ksh428.firebasedemo.Adapter;

import android.content.Context;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ksh428.firebasedemo.R;

import java.util.List;

public class TagAdapter extends  RecyclerView.Adapter<TagAdapter.viewHolder>{
    private Context mcontext;
    List<String> mtags;
    List<String> mtagscount;

    public TagAdapter(Context mcontext, List<String> mtags, List<String> mtagscount) {
        this.mcontext = mcontext;
        this.mtags = mtags;
        this.mtagscount = mtagscount;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.tag_item,parent,false);
        return new TagAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.tag.setText("#"+mtags.get(position));
        holder.no_of_posts.setText((mtagscount.get(position))+"posts");

    }

    @Override
    public int getItemCount() {
        return mtags.size();
    }

    public  class viewHolder extends RecyclerView.ViewHolder{
        TextView tag;
        TextView no_of_posts;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tag=itemView.findViewById(R.id.hashtag);
            no_of_posts=itemView.findViewById(R.id.no_of_posts);

        }
    }
    public  void filter(List<String>filtertags,List<String>filtertagscount){
        this.mtags=filtertags;
        this.mtagscount=filtertagscount;
        notifyDataSetChanged();

    }
}
