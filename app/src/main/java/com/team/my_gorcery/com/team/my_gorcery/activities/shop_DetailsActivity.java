package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.squareup.picasso.Picasso;
import com.team.my_gorcery.Constants;
import com.team.my_gorcery.R;
import com.team.my_gorcery.adapters.AdapterCartItem;
import com.team.my_gorcery.adapters.AdapterProductUser;
import com.team.my_gorcery.model.ModelCartItem;
import com.team.my_gorcery.model.ModelProduct;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class shop_DetailsActivity extends AppCompatActivity {

    private ImageView shop_full_Iv;
    private TextView shop_full_name, shop_full_phone, shop_full_email, shop_full_address, shop_Open_Closed_Tv, shop_full_deliveryFee, shop_filteredTv, cartCounterTv;
    private ImageButton shop_call_btn, shop_map_btn, shop_cart, shop_backbtn, shop_filterProduct, reviewsBtn;
    private EditText shop_searchProduct;
    private RatingBar ratingBar;
    private RecyclerView productsRv;
    public String deliveryFee;


    private String shopUid;
    private String myLATitude, myLONgitude, myPhone;
    private String shopName, shopPhone, shopEmail, shopAddress, shopLATitude, shopLONgitude;

    private FirebaseAuth firebaseAuth;

    //Process dialog
    private ProgressDialog progressDialog;

    // Product
    private ArrayList<ModelProduct> productList;
    private AdapterProductUser adapterProductUser;

    // Cart
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    Animation topAnim, bottomAnim, middleAnim;

    private  EasyDB easyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop__details);

        // Initializing views
        shop_full_Iv = findViewById(R.id.shop_full_Iv);
        shop_full_name = findViewById(R.id.shop_full_name);
        shop_full_phone = findViewById(R.id.shop_full_phone);
        shop_full_email = findViewById(R.id.shop_full_email);
        shop_full_address = findViewById(R.id.shop_full_address);
        shop_Open_Closed_Tv = findViewById(R.id.shop_Open_Closed_Tv);
        shop_full_deliveryFee = findViewById(R.id.shop_full_deliveryFee);
        shop_call_btn = findViewById(R.id.shop_call_btn);
        shop_map_btn = findViewById(R.id.shop_map_btn);
        shop_cart = findViewById(R.id.shop_cart);
        shop_backbtn = findViewById(R.id.shop_backBtn);
        shop_filterProduct = findViewById(R.id.shop_filterProductBtn);
        shop_searchProduct = findViewById(R.id.shop_searchProduct);
        shop_filteredTv = findViewById(R.id.shop_filteredProductTv);
        productsRv = findViewById(R.id.productsRv);
        cartCounterTv = findViewById(R.id.cartCounterTv);
        reviewsBtn = findViewById(R.id.reviewsBtn);
        ratingBar = findViewById(R.id.ratingBar);


        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        productsRv.setAnimation(bottomAnim);

        // Getting the shop from intent
        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        loadReviews();

        // Declare it to class level and initiate in onCreate
        easyDB = EasyDB.init(this, "ITEM_DB")
                .setTableName("Item_Table")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        // Each shop has its own products and orders, so if user add items and goes to open another cart in another shop, then cart should be different
        // So delete cart data whenever user open another activity
        deleteCartData();
        cartCount();

        shop_searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductUser.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        shop_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        shop_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show cart dialog
                showCartDialog();


            }
        });

        shop_call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhone();
            }
        });

        shop_map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });

        shop_filterProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(shop_DetailsActivity.this);
                builder.setTitle("Filter Product:")
                        .setItems(Constants.productcategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get selected item
                                String selected = Constants.productcategory1[which];
                                shop_filteredTv.setText(selected);
                                if (selected.equals("All")) {
                                    //load all
                                    loadShopProducts();
                                } else {
                                    // Load filtered
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();

            }
        });

        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass shop uid to show its reviews
                Intent intent = new Intent(shop_DetailsActivity.this, ShopReviewsActivity.class);
                intent.putExtra("shopUid", shopUid);
                startActivity(intent);

            }
        });

    }

    private float ratingSum = 0;
    private void loadReviews() {

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
                        ratingBar.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void deleteCartData() {
        easyDB.deleteAllDataFromTable(); // This deletes all records from cart
    }

    public void cartCount(){
        // Making public so that it can be accessed in the adapter
        // Getting cart count
        int count = easyDB.getAllData().getCount();
        if(count <= 0){
            // No item in cart, hide count
            cartCounterTv.setVisibility(View.GONE);
        }
        else {
            // when there is/are item(s) in cart, show count and set count
            cartCounterTv.setVisibility(View.VISIBLE);
            cartCounterTv.setText(""+count); // concatenate with string, cause we can't set integer to textview
        }
    }

    public double allTotalPrice = 0.00;
    // These views are public because they need to be accessed in the adapter
    public TextView sTotalTv, dFeeTv, allTotalPriceTv;

    private void showCartDialog() {

        // Initialize list
        cartItemList = new ArrayList<>();

        // Inflate cart layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);

        // Init Views
        RecyclerView cartItemsRv = view.findViewById(R.id.cartItemsRV);
        TextView shopNameTv = view.findViewById(R.id.shopNameTv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        allTotalPriceTv = view.findViewById(R.id.totalTv);
        Button checkOutBtn = view.findViewById(R.id.checkOutBtn);

        // Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set view to dialog
        builder.setView(view);

        shopNameTv.setText(shopName);

        EasyDB easyDB = EasyDB.init(this, "ITEM_DB")
                .setTableName("Item_Table")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "text not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "text not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "text not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "text not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "text not null"}))
                .doneTableColumn();

        // Getting all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem(
                    "" + id,
                    "" + pId,
                    "" + price,
                    "" + cost,
                    "" + quantity,
                    "" + name

            );
            cartItemList.add(modelCartItem);
        }

        // Setup adapter
        adapterCartItem = new AdapterCartItem(this, cartItemList);
        cartItemsRv.setAdapter(adapterCartItem);

        dFeeTv.setText("$" + deliveryFee);
        sTotalTv.setText("$" + String.format("%.2f", allTotalPrice));
        allTotalPriceTv.setText("$" + (allTotalPrice + Double.parseDouble(deliveryFee.replace("$", ""))));

        // show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Reset total price on dialog dismiss
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                allTotalPrice = 0.00;
            }
        });

        // Place order
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First validate delivery address
                if (myLATitude.equals("") || myLATitude.equals("null") || myLONgitude.equals("") || myLONgitude.equals("null")) {
                    // User didn't enter address in profile
                    Toast.makeText(shop_DetailsActivity.this, "Please enter your address in your profile before placing order.....",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (myPhone.equals("") || myPhone.equals("null")) {
                    // User didn't enter phone number in profile
                    Toast.makeText(shop_DetailsActivity.this, "Please drop your phone number in your profile before placing order.....",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cartItemList.size() == 0) {
                    // Cart list is empty
                    Toast.makeText(shop_DetailsActivity.this, "No item in your cart.....", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitOrder();
            }
        });
    }

    private void submitOrder() {
        // Show progress dialog
        progressDialog.setMessage("Please order....");

        // For order id and order time
        final String timestamp = "" +System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("$", ""); // Remove $ if contains

        // Add latitude, longitude of user to each order || delete previous orders from FB or add manually to them

        // Set data order
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timestamp);
        hashMap.put("orderTime", "" + timestamp);
        hashMap.put("orderStatus", "In Progress"); // In progress, Completed, Cancelled
        hashMap.put("orderCost", "" + cost);
        hashMap.put("orderBy", "" + firebaseAuth.getUid());
        hashMap.put("orderTo", "" + shopUid);
        hashMap.put("latitude", "" + myLATitude);
        hashMap.put("longitude", "" + myLONgitude);
        hashMap.put("deliveryFee", "" + deliveryFee);

        // Add to db
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        reference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Order info added now, add order items
                        for (int i = 0; i < cartItemList.size(); i++) {
                            String pId = cartItemList.get(i).getpId();
                            String id = cartItemList.get(i).getId();
                            String name = cartItemList.get(i).getName();
                            String cost = cartItemList.get(i).getCost();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("cost", cost);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);

                            reference.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }
                        progressDialog.dismiss();
                        Toast.makeText(shop_DetailsActivity.this, "Order Placed Successfully.....", Toast.LENGTH_SHORT).show();

                        prepareNotificationMessage(timestamp);
                        // After you place order, open order details page
                      //  Intent intent = new Intent(shop_DetailsActivity.this, OrderDetailsUserActivity.class);
                      //  intent.putExtra("orderTo", shopUid);
                      //  intent.putExtra("orderId", timestamp);
                       // startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed order
                        progressDialog.dismiss();
                        Toast.makeText(shop_DetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void callPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(shopPhone))));
        Toast.makeText(this, "" + shopPhone, Toast.LENGTH_SHORT).show();
    }

    private void openMap() {           // https://maps.google.com/maps?saddr
        String address = "http://maps.google.com/maps?hl=en&t=h&mra=ls&z=13&view=map?saddr=" + myLATitude + "," + myLONgitude + "&daddr=" + shopLATitude + "," + shopLONgitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void loadMyInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Get user data
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            myPhone = "" + ds.child("phone").getValue();
                            String email = "" + ds.child("email").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String country = "" + ds.child("country").getValue();
                            String state = "" + ds.child("state").getValue();
                            String city = "" + ds.child("city").getValue();
                            String accountType = "" + ds.child("accountType").getValue();
                            myLATitude = "" + ds.child("latitude").getValue();
                            myLONgitude = "" + ds.child("longitude").getValue();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadShopDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get data
                String name = "" + dataSnapshot.child("name").getValue();
                shopName = "" + dataSnapshot.child("shop").getValue();
                shopPhone = "" + dataSnapshot.child("phone").getValue();
                shopEmail = "" + dataSnapshot.child("email").getValue();
                shopAddress = "" + dataSnapshot.child("address").getValue();
                shopLATitude = "" + dataSnapshot.child("latitude").getValue();
                shopLONgitude = "" + dataSnapshot.child("longitude").getValue();
                deliveryFee = "" + dataSnapshot.child("deliveryFee").getValue();
                String profileImage = "" + dataSnapshot.child("profileImage").getValue();
                String shopOpen = "" + dataSnapshot.child("shopOpen").getValue();


                // Set data
                shop_full_name.setText(shopName);
                shop_full_phone.setText(shopPhone);
                shop_full_email.setText(shopEmail);
                shop_full_address.setText(shopAddress);
                shop_full_deliveryFee.setText("Delivery Fee: $" + deliveryFee);

                if (shopOpen.equals("true")) {
                    shop_Open_Closed_Tv.setText("Open");
                    shop_Open_Closed_Tv.setTextColor(Color.GREEN);
                } else {
                    shop_Open_Closed_Tv.setText("Closed");
                    shop_Open_Closed_Tv.setTextColor(Color.RED);
                }

                try {
                    Picasso.get().load(profileImage).into(shop_full_Iv);

                } catch (Exception e) {
                    shop_full_Iv.setImageResource(R.drawable.ic_store);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadShopProducts() {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Clear list before adding items
                        productList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }

                        // SetUp adapter
                        adapterProductUser = new AdapterProductUser(shop_DetailsActivity.this, productList);
                        // Set adapter
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void prepareNotificationMessage(String orderId){
        // When user places an order, send notification to the seller
        // Prepare data for notification
        String NOTIFICATION_TOPIC = "/topics/" +Constants.FCM_TOPIC;  // Must be the same as subscribed by user (as in Constants)
        String NOTIFICATION_TITLE = "New Order "+ orderId;
        String NOTIFICATION_MESSAGE = "Congratulations...! You have a new order.";
        String NOTIFICATION_TYPE = "NewOrder";

        // Prepare json (what to send and where to send)
        JSONObject notificationJS = new JSONObject();
        JSONObject notificationBodyJS = new JSONObject();

        try{
             // What to send
            notificationBodyJS.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJS.put("buyerUid", firebaseAuth.getUid()); // Since we will logging as buyer to place order, so current user uid is buyer uid
            notificationBodyJS.put("sellerUid", shopUid);
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

        sendFcmNotification(notificationJS, orderId);

    }

    private void sendFcmNotification(JSONObject notificationJS, final String orderId){
        // Send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJS, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // After sending fcm start order details activity
                Intent intent = new Intent(shop_DetailsActivity.this, OrderDetailsUserActivity.class);
                intent.putExtra("orderTo", shopUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If failed, in sending fcm, still start order details activity
                Intent intent = new Intent(shop_DetailsActivity.this, OrderDetailsUserActivity.class);
                intent.putExtra("orderTo", shopUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
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