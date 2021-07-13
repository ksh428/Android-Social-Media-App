package com.ksh428.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ksh428.firebasedemo.Adapter.CommentAdapter;
import com.ksh428.firebasedemo.Model.Comment;
import com.ksh428.firebasedemo.Model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    private EditText addcomment;
    private CircleImageView imageprofile;
    private TextView post;
    RecyclerView recyclerView;
    CommentAdapter commentAdapter;
    List<Comment>commentlist;

    private String postId;
    private String authorId;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentlist=new ArrayList<>();
        //get the values from the intent from postadapter
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        authorId=intent.getStringExtra("authorId");
        commentAdapter=new CommentAdapter(this,commentlist,postId);
        recyclerView.setAdapter(commentAdapter);

        addcomment=findViewById(R.id.addcomment);
        imageprofile=findViewById(R.id.imageprofile);
        post=findViewById(R.id.post);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        getUserImage();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addcomment.getText().toString())){
                    Toast.makeText(CommentActivity.this, "No comment added", Toast.LENGTH_SHORT).show();
                }else{
                    putcomment();
                }
            }
        });
        getcomment();


    }

    private void getcomment() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentlist.clear();
                for(DataSnapshot snap:snapshot.getChildren()){
                    Comment comment=snap.getValue(Comment.class);
                    commentlist.add(comment);

                }
                commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putcomment() {
        HashMap<String,Object> map=new HashMap<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        String id=ref.push().getKey();
        map.put("id",id);
        map.put("comment",addcomment.getText().toString());
        map.put("publish",fuser.getUid());
        addcomment.setText("");
        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CommentActivity.this, "comment added", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference().child("users").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(user.getImageurl().equals("default")){
                    imageprofile.setImageResource(R.mipmap.ic_launcher);
                }else Picasso.get().load(user.getImageurl()).into(imageprofile);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
}
