package com.ksh428.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.ksh428.firebasedemo.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView close;
    CircleImageView imageprofile;
    TextView save;
    TextView changephoto;
    MaterialEditText fullname;
    MaterialEditText username;
    MaterialEditText bio;
    FirebaseUser  fuser;
    Uri mImageUri;
    StorageTask uploadTask;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        close=findViewById(R.id.close);
        imageprofile=findViewById(R.id.imageprofile);
        changephoto=findViewById(R.id.changephoto);
        save=findViewById(R.id.save);
        fullname=findViewById(R.id.fullname);
        username=findViewById(R.id.username);
        bio=findViewById(R.id.bio);
        storageReference= FirebaseStorage.getInstance().getReference().child("Uploads");
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("users").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {//initial values
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                fullname.setText(user.getName());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                Picasso.get().load(user.getImageurl()).into(imageprofile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });
        imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile();
            }
        });

    }

    private void updateprofile() {
        HashMap<String,Object> map=new HashMap<>();
        map.put("fullname",fullname.getText().toString());
        map.put("username",username.getText().toString());
        map.put("bio",bio.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("users").child(fuser.getUid()).updateChildren(map);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mImageUri=result.getUri();
            uploadimage();
        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadimage() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();
        if(mImageUri!=null){
            final StorageReference fileref=storageReference.child(System.currentTimeMillis()+".jpeg");
            uploadTask=fileref.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return  fileref.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloaduri=task.getResult();
                        String url=downloaduri.toString();
                        FirebaseDatabase.getInstance().getReference().child("users").child(fuser.getUid()).child("imageurl").setValue(url);
                        pd.dismiss();

                    }else{
                        Toast.makeText(EditProfileActivity.this, "upload failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else{
            Toast.makeText(this, "no image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
