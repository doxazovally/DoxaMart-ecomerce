package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team.my_gorcery.Constants;
import com.team.my_gorcery.R;
import com.team.my_gorcery.adapters.AdapterOrderedItem;
import com.team.my_gorcery.model.ModelOrderedItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailsSeller extends AppCompatActivity {

    String orderId, orderBy;
    private TextView s_orderIdTv, s_orderDateTv, s_orderStatusTv, s_orderBuyerEmailTv, s_orderPhoneTv, s_orderTotalItemTv, s_orderAmountTv, s_orderD_AddressTv;
    private RecyclerView s_orderItemRv;
    private ImageButton s_OrderDetails_BackBtn, s_OrderDetails_MapBtn, s_OrderDetails_EditBtn;
    private CardView card_oI;

    private FirebaseAuth firebaseAuth;

    Animation topAnim, bottomAnim, middleAnim;

    private ArrayList<ModelOrderedItem> orderedItemList;
    private AdapterOrderedItem adapterOrderedItem;

    // To open destination in map
    String sourceLatitude, sourceLongitude, destinationLongitude, destinationLatitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);

        s_orderIdTv = findViewById(R.id.s_orderIdTv);
        s_orderDateTv = findViewById(R.id.s_orderDateTv);
        s_orderStatusTv = findViewById(R.id.s_orderStatusTv);
        s_orderBuyerEmailTv = findViewById(R.id.s_orderBuyerEmailTv);
        s_orderPhoneTv = findViewById(R.id. s_orderPhoneTv);
        s_orderTotalItemTv = findViewById(R.id.s_orderTotalItemTv);
        s_orderAmountTv = findViewById(R.id.s_orderAmountTv);
        s_orderD_AddressTv = findViewById(R.id.s_orderD_AddressTv);
        s_orderItemRv = findViewById(R.id.s_orderItemRv);
        s_OrderDetails_BackBtn = findViewById(R.id.s_OrderDetails_BackBtn);
        s_OrderDetails_EditBtn = findViewById(R.id.s_OrderDetails_EditBtn);
        s_OrderDetails_MapBtn = findViewById(R.id.s_OrderDetails_MapBtn);
        card_oI = findViewById(R.id.card_oI);


        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        card_oI.setAnimation(middleAnim);
        s_orderItemRv.setAnimation(bottomAnim);


        // Get data from intent
        orderBy = getIntent().getStringExtra("orderBy");
        orderId = getIntent().getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadBuyerInfo();
        loadOrderDetails();
        loadOrderedItems();

        s_OrderDetails_BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        s_OrderDetails_MapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });

        s_OrderDetails_EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Edit the Order status
                editOrderStatusDialog();
            }
        });


    }

    private void editOrderStatusDialog() {
        // Displaying options in dialog
        final String[] options = {"In Progress", "Completed", "Cancelled"};
        // Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Order Status:")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       String selectedOption = options[which];
                       editOrderStatus(selectedOption);
                    }
                }).show();
    }

    private void editOrderStatus(final String selectedOption) {
        // Setting up data to put in FB
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", ""+selectedOption);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = "Order is now "+selectedOption;
                        // Status updated
                        Toast.makeText(OrderDetailsSeller.this, message,  Toast.LENGTH_SHORT).show();

                        prepareNotificationMessage(orderId, message);

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Status failed to updated
                        Toast.makeText(OrderDetailsSeller.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void openMap() {
            String address = "https://maps.google.com/maps?saddr=" + sourceLatitude + "," + sourceLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
            startActivity(intent);
    }

    private void loadMyInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       sourceLatitude = ""+dataSnapshot.child("latitude").getValue();
                       sourceLongitude = ""+dataSnapshot.child("longitude").getValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadBuyerInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        destinationLatitude = ""+dataSnapshot.child("latitude").getValue();
                        destinationLongitude = ""+dataSnapshot.child("longitude").getValue();
                        String email = ""+dataSnapshot.child("email").getValue();
                        String phone = ""+dataSnapshot.child("phone").getValue();

                        s_orderBuyerEmailTv.setText(email);
                        s_orderPhoneTv.setText(phone);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadOrderDetails() {
        // Load detailed info of the buyer based on tha userId
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                            s_orderStatusTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        } else if (orderStatus.equals("Completed")) {
                            s_orderStatusTv.setTextColor(getResources().getColor(R.color.colorGreen));
                        } else if (orderStatus.equals("Cancelled")) {
                            s_orderStatusTv.setTextColor(getResources().getColor(R.color.colorRed));
                        }

                        s_orderIdTv.setText(orderId);
                        s_orderStatusTv.setText(orderStatus);
                        s_orderAmountTv.setText("$"+orderCost+ " [Including delivery fee: $" +deliveryFee+ "]");
                        s_orderDateTv.setText(formatedDate);


                        findAddress(latitude, longitude); // To find delivery location

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
            s_orderD_AddressTv.setText(address);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void loadOrderedItems(){
        // Load the ordered products
        orderedItemList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderedItemList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);

                            // Adding list
                            orderedItemList.add(modelOrderedItem);

                            adapterOrderedItem = new AdapterOrderedItem(OrderDetailsSeller.this, orderedItemList); // Setting up adapter

                            s_orderItemRv.setAdapter(adapterOrderedItem); // Setting adapter to recyclerview

                            s_orderTotalItemTv.setText(""+dataSnapshot.getChildrenCount()); // Total number of items/products in order
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void prepareNotificationMessage(String orderId, String message){
        // When seller changes order status ie In progress/Completed/Cancelled, send notification to the buyer

        // Prepare data for notification
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;  // Must be the same as subscribed by user (as in Constants)
        String NOTIFICATION_TITLE = "Your Order "+ orderId;
        String NOTIFICATION_MESSAGE = "" +message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        // Prepare json (what to send and where to send)
        JSONObject notificationJS = new JSONObject();
        JSONObject notificationBodyJS = new JSONObject();

        try{
            // What to send
            notificationBodyJS.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJS.put("buyerUid", orderBy);
            notificationBodyJS.put("sellerUid", firebaseAuth.getUid()); // Since we will logging as seller to change order status, so current user uid is seller uid
            notificationBodyJS.put("orderId", orderId);
            notificationBodyJS.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJS.put("notificationMessage", NOTIFICATION_MESSAGE);

            // Where to send
            notificationJS.put("to", NOTIFICATION_TOPIC); // To all who subscribed to this topic
            notificationJS.put("data", notificationBodyJS);

        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJS);

    }

    private void sendFcmNotification(JSONObject notificationJS){
        // Send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googlepis.com/fcm/send", notificationJS, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Notification sent

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Notification not sent
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                // Put required headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" +Constants.FCM_KEY);
                return headers;
            }
        };

        // Enque the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}
