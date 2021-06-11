package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team.my_gorcery.R;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private EditText m_email, m_password;
    private TextView forgotPass, noAccount;
    private Button loginbtn;

    Animation topAnim, bottomAnim, middleAnim;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        m_email = findViewById(R.id.email1);
        m_password = findViewById(R.id.password1);
        forgotPass = findViewById(R.id.fp);
        noAccount = findViewById(R.id.noAcc);
        loginbtn = findViewById(R.id.L_loginbtn);


        m_email.setAnimation(bottomAnim);
        m_password.setAnimation(bottomAnim);
        loginbtn.setAnimation(middleAnim);
        forgotPass.setAnimation(middleAnim);
        noAccount.setAnimation(bottomAnim);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        findViewById(R.id.noAcc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
               overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_from_top);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();

            }
        });


    }

    private String email, password;

    private void loginUser() {
        email = m_email.getText().toString().trim();
        password = m_password.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email pattern...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging In.... ");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Logging in successful
                        if(firebaseAuth.getCurrentUser().isEmailVerified()){

                            makeMeOnline();
                        }
                       else{
                           Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Logging in not successful
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void makeMeOnline() {
        //Make user online after logging in
        progressDialog.setMessage("Confirming User...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online", "true");

        //Updating info to database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Update successful
                        checkUserType();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Update failed
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUserType() {
        //If user is seller, start seller main screen
        //If user is buyer, start user main screen

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();
                            if(accountType.equals("Seller")){
                                progressDialog.dismiss();
                                //User is seller
                                startActivity (new Intent(LoginActivity.this, MainSellerActivity.class));
                                finish();

                            }
                            else {
                                    //User is buyer
                                progressDialog.dismiss();
                                startActivity (new Intent(LoginActivity.this, MainUserActivity.class));
                                finish();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
