package com.ksh428.firebasedemo.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ksh428.firebasedemo.MainActivity;
import com.ksh428.firebasedemo.Model.Comment;
import com.ksh428.firebasedemo.Model.User;
import com.ksh428.firebasedemo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder>{
    Context mcontext;
    List<Comment> mcomments;
    FirebaseUser fuser;
    String postId;


    public CommentAdapter(Context mcontext, List<Comment> mcomments,String postId) {
        this.mcontext = mcontext;
        this.mcomments = mcomments;
        this.postId=postId;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment=mcomments.get(position);
        holder.comment.setText(comment.getComment());
        FirebaseDatabase.getInstance().getReference().child("users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                holder.username.setText(user.getUsername());
                if(user.getImageurl().equals("default")){
                    holder.imageprofile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(user.getImageurl()).into(holder.imageprofile);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            //tricky part: we want the user to be redirected to the profile (fragment) of the user that put the comment
            // so we first go to the mainactivity using intent and then store thr id using sharedpref as activity to fragment using intent is not possible
            //then begin the transaction
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                mcontext.startActivity(intent);


            }
        });
        holder.imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                mcontext.startActivity(intent);


            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getPublisher().endsWith(fuser.getUid())) { ///.equals also
                    AlertDialog ad = new AlertDialog.Builder(mcontext).create();
                    ad.setTitle("Do u want to delete");
                    ad.setButton(AlertDialog.BUTTON_NEUTRAL, "no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.setButton(AlertDialog.BUTTON_POSITIVE, "yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).child(comment.getId()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(mcontext, "Comment deleted successfully", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });

                        }
                    });
                    ad.show();
                }
                return  true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mcomments.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        CircleImageView imageprofile;
        TextView username;
        TextView comment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageprofile=itemView.findViewById(R.id.imageprofile);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);

        }
    }
}
