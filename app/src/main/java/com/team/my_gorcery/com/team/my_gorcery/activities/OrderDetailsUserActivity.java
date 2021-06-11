package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team.my_gorcery.R;
import com.team.my_gorcery.adapters.AdapterOrderedItem;
import com.team.my_gorcery.model.ModelOrderedItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderDetailsUserActivity extends AppCompatActivity {

    private String orderTo, orderId;
    private TextView orderIdTv, orderDateTv, orderStatusTv, orderShopNameTv, orderTotalItemTv, orderAmountTv, orderD_AddressTv;
    private ImageButton itemOrderPage_backBtn, writeReviewBtn;
    private RecyclerView itemRv;
    private CardView card_OI;

    private FirebaseAuth firebaseAuth;

    Animation topAnim, bottomAnim, middleAnim;

    private ArrayList<ModelOrderedItem> orderedItemList;
    private AdapterOrderedItem adapterOrderedItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_user);

        orderIdTv = findViewById(R.id.orderIdTv);
        orderDateTv = findViewById(R.id.orderDateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        orderShopNameTv = findViewById(R.id.orderShopNameTv);
        orderTotalItemTv = findViewById(R.id.orderTotalItemTv);
        orderAmountTv = findViewById(R.id.orderAmountTv);
        orderD_AddressTv = findViewById(R.id.orderD_AddressTv);
        itemRv = findViewById(R.id.itemRv);
        itemOrderPage_backBtn = findViewById(R.id.itemOrderPage_backBtn);
        writeReviewBtn = findViewById(R.id.writeReviewBtn);
        card_OI = findViewById(R.id.card_OI);


        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        card_OI.setAnimation(middleAnim);
        itemRv.setAnimation(bottomAnim);



        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo"); // This contains the uid of the shop where we placed order
        orderId = intent.getStringExtra("orderId");


        firebaseAuth = FirebaseAuth.getInstance();
        loadShopInfo();
        loadOrderDetails();
        loadOrderedItems();


        itemOrderPage_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent review = new Intent(OrderDetailsUserActivity.this, WriteReviewActivity.class);
                review.putExtra("shopUid", orderTo); // To write the review to any shop we must have the uid of that shop
                startActivity(review);

            }
        });

    }

    private void loadOrderedItems() {
        orderedItemList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderedItemList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);
                            orderedItemList.add(modelOrderedItem);

                            adapterOrderedItem = new AdapterOrderedItem(OrderDetailsUserActivity.this, orderedItemList);

                            itemRv.setAdapter(adapterOrderedItem);

                            orderTotalItemTv.setText(""+dataSnapshot.getChildrenCount());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadOrderDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // First get data
                        String orderBy = ""+dataSnapshot.child("orderBy").getValue();
                        String orderCost = ""+dataSnapshot.child("orderCost").getValue();
                        String orderId = ""+dataSnapshot.child("orderId").getValue();
                        String orderStatus = ""+dataSnapshot.child("orderStatus").getValue();
                        String orderTime = ""+dataSnapshot.child("orderTime").getValue();
                        String orderTo = ""+dataSnapshot.child("orderTo").getValue();
                        String deliveryFee = ""+dataSnapshot.child("deliveryFee").getValue();
                        String latitude = ""+dataSnapshot.child("latitude").getValue();
                        String longitude = ""+dataSnapshot.child("longitude").getValue();

                        // Converting timestamp to proper format
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy  hh:mm a", calendar).toString();

                        if (orderStatus.equals("In Progress")) {
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        } else if (orderStatus.equals("Completed")) {
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorGreen));
                        } else if (orderStatus.equals("Cancelled")) {
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorRed));
                        }

                        // Set data
                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        orderAmountTv.setText("$"+orderCost+ " [Including delivery fee: $" +deliveryFee+ "]");
                        orderDateTv.setText(formatedDate);

                        findAddress(latitude, longitude);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadShopInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String shopName = ""+dataSnapshot.child("shop").getValue();
                        orderShopNameTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void findAddress(String latitude, String longitude) {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        // Find address, country, state, city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

            String address = addresses.get(0).getAddressLine(0); // Complete address
            orderD_AddressTv.setText(address);
        }
        catch (Exception e){

        }
    }
}
