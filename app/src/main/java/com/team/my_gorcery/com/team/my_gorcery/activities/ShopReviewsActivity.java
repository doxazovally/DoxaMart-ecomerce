package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.team.my_gorcery.R;
import com.team.my_gorcery.adapters.AdapterReview;
import com.team.my_gorcery.model.ModelReview;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopReviewsActivity extends AppCompatActivity {

    private String shopUid;
    private ImageButton shop_backBtn;
    private TextView sR_shopName, sR_Rb_Num;
    private RatingBar ratingBar;
    private RecyclerView sR_Rv;
    private CircleImageView sR_profileImage;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelReview> reviewList;
    private AdapterReview adapterReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_reviews);

        shop_backBtn = findViewById(R.id.shop_backBtn);
        sR_Rb_Num = findViewById(R.id.sR_Rb_Num);
        sR_shopName = findViewById(R.id.sR_shopName);
        ratingBar = findViewById(R.id.ratingBar);
        sR_Rv = findViewById(R.id.sR_Rv);
        sR_profileImage = findViewById(R.id.sR_profileImage);

        // Get shop uid rom intent
        shopUid = getIntent().getStringExtra("shopUid");

        firebaseAuth = FirebaseAuth.getInstance();
        loadShopDetails(); // This is for the shop name, image.
        loadReviews(); // This is foe reviews and avg rating.

        shop_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private float ratingSum = 0;
    private void loadReviews() {
        // Initializing list
        reviewList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Clear list before adding data into it
                        reviewList.clear();
                        ratingSum = 0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue()); // e.g 3.5
                            ratingSum = ratingSum +rating;

                            ModelReview modelReview = ds.getValue(ModelReview.class);
                            reviewList.add(modelReview);

                        }
                        // Setup adapter
                         adapterReview = new AdapterReview(ShopReviewsActivity.this, reviewList);

                        sR_Rv.setAdapter(adapterReview);

                        long numberOfReview = dataSnapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReview;

                        // Setting the average into the rating textview (sR_Rb_Num)
                        sR_Rb_Num.setText(String.format("%.2f", avgRating) + " [" +numberOfReview+"]");
                        ratingBar.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadShopDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get user data
                        String shopName = ""+dataSnapshot.child("shop").getValue();
                        String Image = ""+dataSnapshot.child("profileImage").getValue();

                        // Set user data
                        sR_shopName.setText(shopName);

                        try {
                            Picasso.get().load(Image).placeholder(R.drawable.ic_store).into(sR_profileImage);
                        }

                        catch (Exception e){
                            sR_profileImage.setImageResource(R.drawable.ic_store);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
