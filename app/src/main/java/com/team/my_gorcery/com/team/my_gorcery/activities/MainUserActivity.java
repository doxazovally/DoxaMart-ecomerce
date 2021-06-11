package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.picasso.Picasso;
import com.team.my_gorcery.R;
import com.team.my_gorcery.adapters.AdapterOrderUser;
import com.team.my_gorcery.adapters.AdapterShop;
import com.team.my_gorcery.model.ModelOrderUser;
import com.team.my_gorcery.model.ModelShop;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {
    private TextView nameVT, phoneVT, EmailvT, tabShopVT, tabOrdersVT;
    private ImageButton logout, editProfileBtn,  settings_Btn;
    private ImageView user_image;
    private RelativeLayout shopsRL, ordersRL;
    private RecyclerView shopRv, ordersRv;

    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    private ArrayList<ModelShop> shopList;
    private AdapterShop adapterShop;

   private ArrayList<ModelOrderUser> orderList;
   private AdapterOrderUser adapterOrderUser;

    Animation topAnim, bottomAnim, middleAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_home_page);

        nameVT = findViewById(R.id.uM_Name);
        phoneVT = findViewById(R.id.uM_phone);
        EmailvT = findViewById(R.id.sM_email);
        user_image = findViewById(R.id.uM_profileImage);
        tabShopVT = findViewById(R.id.tabShop);
        tabOrdersVT = findViewById(R.id.tabOrders);
        shopsRL = findViewById(R.id.shopsRl);
        ordersRL = findViewById(R.id.ordersRl);
        shopRv = findViewById(R.id.shopRV);
        ordersRv = findViewById(R.id.ordersRv);
       // settings_Btn = findViewById(R.id.settings_Btn);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth = FirebaseAuth.getInstance();

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        shopRv.setAnimation(topAnim);
        ordersRv.setAnimation(middleAnim);



        checkUser();

        // At start, show shop UI
        showShop();

      //  logout.setOnClickListener(new View.OnClickListener() {
       //     @Override
       //     public void onClick(View view) {
                //Make offline
                //Sign out
                //Go to login activity
         //       makeMeoffline();
        //    }
       // });

       // editProfileBtn.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View view) {
                //Open edit page
       //         startActivity(new Intent(MainUserActivity.this, Edit_Profile_page_User_Activity.class ));
       //     }
       // });

        tabShopVT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load shops
                showShop();

            }
        });

        tabOrdersVT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load shops
                showOrder();

            }
        });

      //  settings_Btn.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View view) {
                // Open settings
       //         startActivity(new Intent(MainUserActivity.this, SettingActivity.class));
       //     }
      //  });


        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.ic_favorite_black);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView ed = new ImageView(this);
        ed.setImageResource(R.drawable.ic_edit);
        SubActionButton edit = itemBuilder.setContentView(ed).build();

        ImageView st = new ImageView(this);
        st.setImageResource(R.drawable.ic_settings);
        SubActionButton settings = itemBuilder.setContentView(st).build();

        ImageView lo = new ImageView(this);
        lo.setImageResource(R.drawable.ic_logout);
        SubActionButton logout = itemBuilder.setContentView(lo).build();

        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(edit)
                .addSubActionView(settings)
                .addSubActionView(logout)
                .attachTo(actionButton)
                .build();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                startActivity(new Intent(MainUserActivity.this, Edit_Profile_page_User_Activity.class ));

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                startActivity(new Intent(MainUserActivity.this, SettingActivity.class));

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                makeMeoffline();

            }
        });
    }

    private void showShop() {
        // Show shop UI, hide orders UI
        shopsRL.setVisibility(View.VISIBLE);
        ordersRL.setVisibility(View.GONE);

        tabShopVT.setTextColor(getResources().getColor(R.color.colorBrown));
        tabShopVT.setBackgroundResource(R.drawable.shape_rec_05);

        tabOrdersVT.setTextColor(getResources().getColor(R.color.brownLightcolor));
        tabOrdersVT.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrder() {
        // Show orders UI, hide shop UI
        ordersRL.setVisibility(View.VISIBLE);
        shopsRL.setVisibility(View.GONE);

        tabShopVT.setTextColor(getResources().getColor(R.color.brownLightcolor));
        tabShopVT.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersVT.setTextColor(getResources().getColor(R.color.colorBrown));
        tabOrdersVT.setBackgroundResource(R.drawable.shape_rec_05);
    }

    private void makeMeoffline() {

        //Make user online after logging in
        progressDialog.setMessage("Logging Out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online", "false");

        //Updating info to database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Update successful
                        firebaseAuth.signOut();
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Update failed
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class ));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Get user data
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String email = ""+ds.child("email").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String country = ""+ds.child("country").getValue();
                            String state = ""+ds.child("state").getValue();
                            String city = ""+ds.child("city").getValue();
                            String accountType = ""+ds.child("accountType").getValue();

                            // Set user data
                            nameVT.setText(name);
                            phoneVT.setText(phone);
                            EmailvT.setText(email);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_profile).into(user_image);
                            }

                            catch (Exception e){
                                user_image.setImageResource(R.drawable.ic_profile);
                            }

                            // Loading shops that are in the user's city
                            loadShop(country);
                            loadOrders();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadOrders() {
        // Initiate order List
        orderList = new ArrayList<>();

        // Buyer Get orders
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);

                                            // Add to list
                                            orderList.add(modelOrderUser);
                                        }

                                        // Set to adapter
                                        adapterOrderUser = new AdapterOrderUser(MainUserActivity.this, orderList);

                                        // set to recyclerview
                                        ordersRv.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadShop(final String myCountry) {
        //initialise list
        shopList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Clear list before adding shop
                        shopList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            String shopCountry = ""+ds.child("country").getValue();

                            // Show only shops in the user's city
                           // if(shopCity.equals(myCity)){
                          //      shopList.add(modelShop);
                          //  }

                            // displaying all registered shops from anywhere
                            shopList.add(modelShop);

                        }

                        // Setup Adapter
                        adapterShop = new AdapterShop(MainUserActivity.this, shopList);

                        // Setup Adapter for recyclerView
                        shopRv.setAdapter(adapterShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
