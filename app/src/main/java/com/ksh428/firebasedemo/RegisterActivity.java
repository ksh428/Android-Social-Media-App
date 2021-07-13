package com.ksh428.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ksh428.firebasedemo.Model.User;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    EditText username;
    EditText name;
    EditText email;
    EditText password;
    DatabaseReference mrootref;
    Button register;
    TextView loginuser;
    FirebaseAuth mauth;
    ProgressDialog pd;
    List<String>usernames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username=findViewById(R.id.username);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        mrootref= FirebaseDatabase.getInstance().getReference();
        loginuser=findViewById(R.id.loginuser);
        register=findViewById(R.id.register);
        mauth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername=username.getText().toString();
                String txtName=name.getText().toString();
                String txtEmail=email.getText().toString();
                String txtPassword=password.getText().toString();
                if(TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtName) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(RegisterActivity.this, "empty credentials", Toast.LENGTH_SHORT).show();
                }else if(txtPassword.length()<6){
                    Toast.makeText(RegisterActivity.this, "password too short", Toast.LENGTH_SHORT).show();

                }else{
                    registerUser(txtUsername,txtName,txtEmail,txtPassword);
                }

            }
        });

    }




    private void registerUser(final String username, final String name, final String email, String password) {
        pd.setMessage("please wait");
        pd.show();
        mauth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("name",name);
                map.put("email",email);
                map.put("username",username);
                map.put("id",mauth.getCurrentUser().getUid());
                map.put("bio","");
                map.put("imageurl","default");
                usernames.add(username);
                mrootref.child("users").child(mauth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "update the profile in settings", Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //FLAG_ACTIVITY_CLEAR_TASK : will clear all the other activities already in the task and launch mainactivity as new activity
                            //the use of cleartop is if the mainactivity if already running in the task then  then instead of launching a new instance of that activity, all of
                            // the other activities on top of it are destroyed and this intent is delivered to the resumed instance of the activity (now on top)
                            startActivity(intent);
                            finish();
                        }

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
