package com.ksh428.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.hendraanggrian.appcompat.widget.SocialEditText;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private Uri imageuri;
    private  String imageUrl;
    private ImageView close;
    ImageView image_added;
    TextView post;
    SocialAutoCompleteTextView description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close=findViewById(R.id.close);
        image_added=findViewById(R.id.image_added);
        post=findViewById(R.id.post);
        description=findViewById(R.id.description);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        CropImage.activity().start(PostActivity.this);// this return an imageuri.//so no need to call implicit intent//opens the gallery
    }

    private void upload() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();
        if(imageuri!=null){
            final StorageReference filepath= FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis()+"."+getfileextension(imageuri));
            StorageTask uploadtask=filepath.putFile(imageuri);
            //we can do it in the other way also...
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloaduri=task.getResult();
                    imageUrl=downloaduri.toString();
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
                    String postId=ref.push().getKey();//creates a new id
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("postId",postId);
                    map.put("imageurl",imageUrl);
                    map.put("description",description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(postId).setValue(map);

                    DatabaseReference hashref=FirebaseDatabase.getInstance().getReference("Hashtags");
                    List<String> hashtags=description.getHashtags();//downloaded we are using ist since the func return list
                    if(!hashtags.isEmpty()){
                        for(String tag:hashtags){
                            map.clear();
                            map.put("tag",tag.toLowerCase());
                            map.put("postId",postId);

                            hashref.child(tag.toLowerCase()).child(postId).setValue(map);

                        }

                    }
                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(this, "No image is selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getfileextension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//we override this to get the image url
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri=result.getUri();
           // image_added.setImageURI(imageuri);//we could have also used picasso
            Picasso.get().load(imageuri).into(image_added);


        }else{
            Toast.makeText(this, "try again!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getApplicationContext());//default woth autocompletetetview
        FirebaseDatabase.getInstance().getReference().child("Hashtags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap:snapshot.getChildren()){
                    hashtagAdapter.add(new Hashtag(snap.getKey() , (int) snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        description.setHashtagAdapter(hashtagAdapter);
    }
}
