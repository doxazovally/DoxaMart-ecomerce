package com.team.my_gorcery.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.team.my_gorcery.R;
import com.team.my_gorcery.com.team.my_gorcery.activities.shop_DetailsActivity;
import com.team.my_gorcery.model.ModelShop;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.shopHolder> {

    private Context context;
    public ArrayList<ModelShop> shopList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopList) {
        this.context = context;
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public shopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout row_shop xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_shopnew, parent, false);
        return new shopHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull shopHolder holder, int position) {
        // Get data
        ModelShop modelShop = shopList.get(position);
        final String uid = modelShop.getUid();
        String name = modelShop.getName();
        String phone = modelShop.getPhone();
        String email = modelShop.getEmail();
        String country = modelShop.getCountry();
        String state = modelShop.getState();
        String city = modelShop.getCity();
        String address = modelShop.getAddress();
        String profileImage = modelShop.getProfileImage();
        String accountType = modelShop.getAccountType();
        String deliveryFee = modelShop.getDeliveryFee();
        String longitude = modelShop.getLongitude();
        String latitude = modelShop.getLatitude();
        String timestamp = modelShop.getTimestamp();
        String online = modelShop.getOnline();
        String shopOpen = modelShop.getShopOpen();
        String shopName = modelShop.getShop();

        loadReviews(modelShop, holder);


        // Set data
        holder.shopName.setText(shopName);
        holder.shopPhone.setText(phone);
        holder.shopAddress.setText(address);

        // Show shop owner is online
        if(online.equals("true")){
            holder.onlineIv.setVisibility(View.VISIBLE);
        }
        // Show shop owner is offline
        else {
            holder.onlineIv.setVisibility(View.GONE);
        }

        // Check if shop is open
        if(shopOpen.equals("true")){
            holder.shopClosed.setVisibility(View.GONE);
        }
        // Show shop is closed
        else {
            holder.shopClosed.setVisibility(View.VISIBLE);
        }


        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(holder.shopIcon_Image);
        }
        catch (Exception e){
            holder.shopIcon_Image.setImageResource(R.drawable.ic_store);
        }

        // Handle clicks listener, show shop details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, shop_DetailsActivity.class);
                intent.putExtra("shopUid", uid);
                context.startActivity(intent);

            }
        });


    }

    private float ratingSum = 0;
    private void loadReviews(ModelShop modelShop, final shopHolder holder) {

        String shopUid = modelShop.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ratingSum = 0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue()); // e.g 3.5
                            ratingSum = ratingSum +rating;


                        }


                        long numberOfReview = dataSnapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReview;

                        // Setting the average into the rating textview (sR_Rb_Num)
                        holder.ratingBar.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return shopList.size(); // Returns number of records
    }

    // View holder
    class shopHolder extends RecyclerView.ViewHolder{

        // UI views of row_shop
        private ImageView shopIcon_Image, onlineIv;
        private TextView shopClosed, shopName, shopPhone, shopAddress;
        private RatingBar ratingBar;

        public shopHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize uid views
            shopIcon_Image = itemView.findViewById(R.id.shopIcon_Image);
            onlineIv = itemView.findViewById(R.id.onlineIv);
            shopClosed = itemView.findViewById(R.id.shopClosed);
            shopName = itemView.findViewById(R.id.Shop_name);
            shopPhone = itemView.findViewById(R.id.Shop_phone);
            shopAddress = itemView.findViewById(R.id.Shop_Address);
            ratingBar = itemView.findViewById(R.id.ratingBar);


        }
    }
}
