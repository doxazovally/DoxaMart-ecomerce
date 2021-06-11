package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team.my_gorcery.R;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make fullscreen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();


        //start login activity in after 3sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    //User not logged in, start login activity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
             //   else if(user.isEmailVerified()){
                    //User is logged in, check user type

               //     checkUserType();
               // }
                else{
                  //  startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                   // Toast.makeText(SplashActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                   // finish();
                    checkUserType();
                }



            }
        }, 5000);
    }


    private void checkUserType() {
        //If user is seller, start seller main screen
        //If user is buyer, start user main screen

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String accountType = ""+dataSnapshot.child("accountType").getValue();
                        if(accountType.equals("Seller")){

                            //User is seller
                            startActivity (new Intent(SplashActivity.this, MainSellerActivity.class));
                            finish();
                        }
                        else {
                            //User is buyer

                            startActivity (new Intent(SplashActivity.this, MainUserActivity.class));
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
