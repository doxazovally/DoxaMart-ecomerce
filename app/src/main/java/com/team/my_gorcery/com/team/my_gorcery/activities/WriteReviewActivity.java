package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.team.my_gorcery.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class WriteReviewActivity extends AppCompatActivity {

    private ImageButton R_backBtn;
    private TextView R_shopName;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private CircleImageView R_storeImage;
    private FloatingActionButton submitReviewBtn;

    private  String shopUid;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        R_backBtn = findViewById(R.id.R_backBtn);
        R_shopName = findViewById(R.id.R_shopName);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEt = findViewById(R.id.reviewEt);
        R_storeImage = findViewById(R.id.R_storeImage);
        submitReviewBtn = findViewById(R.id.submitReviewBtn);

        //Get shop uid from intent
        shopUid = getIntent().getStringExtra("shopUid");

        firebaseAuth = FirebaseAuth.getInstance();
        // If a user has written review to this shop, load it
        loadShopInfo();
        // If a user has written review to this shop, load it
        loadMyReview();

        R_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        submitReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDate();
            }
        });

    }

    private void loadShopInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Getting shop info
                String shopName = ""+dataSnapshot.child("shop").getValue();
                String shopImage = ""+dataSnapshot.child("profileImage").getValue();

                // Setting shop info
                R_shopName.setText(shopName);
                try {
                    Picasso.get().load(shopImage).placeholder(R.drawable.ic_store).into(R_storeImage);
                }

                catch (Exception e){
                    R_storeImage.setImageResource(R.drawable.ic_store);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMyReview() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            // My review is in this shop
                            // Bet review details
                            String uid = ""+dataSnapshot.child("uid").getValue();
                            String ratings = ""+dataSnapshot.child("ratings").getValue();
                            String review = ""+dataSnapshot.child("review").getValue();
                            String timestamp = ""+dataSnapshot.child("timestamp").getValue();

                            // Set review details to our ui
                            float myRating = Float.parseFloat(ratings);
                            ratingBar.setRating(myRating);
                            reviewEt.setText(review);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void inputDate() {
        String ratings = ""+ratingBar.getRating();
        String review = reviewEt.getText().toString().trim();

        // For time of review
        String timestamp = ""+System.currentTimeMillis();

        // Setup data to hashmap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+firebaseAuth.getUid());
        hashMap.put("ratings", ""+ratings);
        hashMap.put("review", ""+review);
        hashMap.put("timestamp", ""+timestamp);

        // Adding to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Review added to db
                        Toast.makeText(WriteReviewActivity.this, "Review submission successfully....", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Review adding to db failed
                        Toast.makeText(WriteReviewActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
