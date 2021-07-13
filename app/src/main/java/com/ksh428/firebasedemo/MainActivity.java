package com.ksh428.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ksh428.firebasedemo.Fragments.HomeFragment;
import com.ksh428.firebasedemo.Fragments.NotificationFragment;
import com.ksh428.firebasedemo.Fragments.ProfileFragment;
import com.ksh428.firebasedemo.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorfRagment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                         selectorfRagment=new HomeFragment();
                         break;
                    case R.id.nav_search:
                        selectorfRagment= new SearchFragment();
                        break;
                    case R.id.nav_add:
                        selectorfRagment=null;
                        startActivity(new Intent(MainActivity.this,PostActivity.class));
                        break;
                    case R.id.nav_heart:
                        selectorfRagment= new NotificationFragment();
                        break;
                    case R.id.nav_profile:
                        //
                      //  int no=getSharedPreferences("CHECK",MODE_PRIVATE).getInt("checkno",0)
                            selectorfRagment=new ProfileFragment();
                        break;
                }
                if(selectorfRagment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorfRagment).commit();
                }
                return true;
            }
        });
        //bundle is used to extract intent data
        Bundle intent =getIntent().getExtras();
        if(intent!=null){
            String profileid=intent.getString("publisherId");
            //sharedpref is the only way to transfer data from activity to fragment
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileid).apply();//REVIEW THIS
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        }

    }
}
