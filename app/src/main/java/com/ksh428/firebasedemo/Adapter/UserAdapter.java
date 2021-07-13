package com.ksh428.firebasedemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ksh428.firebasedemo.Fragments.ProfileFragment;
import com.ksh428.firebasedemo.MainActivity;
import com.ksh428.firebasedemo.Model.User;
import com.ksh428.firebasedemo.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends  RecyclerView.Adapter<UserAdapter.viewHolder> {
    private Context mcontext;
    private List<User> musers;
    private boolean isfragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mcontext, List<User> musers, boolean isfragment) {
        this.mcontext = mcontext;
        this.musers = musers;
        this.isfragment = isfragment;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final User user=musers.get(position);
        holder.buttonfollow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getName());
        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageprofile); //placeholder->stores iclauncher until img is loaded...->into target
        isfollowed(user.getId(),holder.buttonfollow);
        if(user.getId().equals(firebaseUser.getUid())){//cuurentuser.
            holder.buttonfollow.setVisibility(View.GONE);
        }
        holder.buttonfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.buttonfollow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("Followers").child(firebaseUser.getUid()).setValue(true);
                    addnotification(user.getId());
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isfragment){//isfragment is false when it is called from follwoers activity
                    mcontext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",user.getId()).apply();
                 //   mcontext.getSharedPreferences("CHECK",Context.MODE_PRIVATE).edit().putInt("checkno",1).apply();
                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

                }else{
                    Intent intent=new Intent(mcontext, MainActivity.class);
                    intent.putExtra("publisherId",user.getId());
                    mcontext.startActivity(intent);
                    
                }
            }
        });


    }

    private void addnotification(String userid) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("userid",userid);
        map.put("text","followed u");
        map.put("postid","");
        map.put("ispost",false);
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);


    }

    private void isfollowed(final String id, final Button buttonfollow) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");//checks whether the user is followed by the current user
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists()){
                    buttonfollow.setText("Following");
                }else{
                    buttonfollow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return musers.size();
    }

    public  class viewHolder extends RecyclerView.ViewHolder{
        public CircleImageView imageprofile;
        public TextView username;
        public TextView fullname;
        public Button buttonfollow;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageprofile=itemView.findViewById(R.id.imageprofile);
            username=itemView.findViewById(R.id.username);
            fullname=itemView.findViewById(R.id.fullname);
            buttonfollow=itemView.findViewById(R.id.button_follow);
        }
    }
}
