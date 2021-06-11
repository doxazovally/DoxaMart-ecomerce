package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
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
import com.team.my_gorcery.Constants;
import com.team.my_gorcery.R;
import com.team.my_gorcery.adapters.AdapterOrderShop;
import com.team.my_gorcery.adapters.AdapterProductSeller;
import com.team.my_gorcery.model.ModelOrderShop;
import com.team.my_gorcery.model.ModelProduct;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {
    private TextView nameTv, Sm_shop, Sm_email, tabProducts, tabOrders, filteredProductTv, filterOrderTv;
    private ImageView  filterSearchBtn ;
    private ImageButton logout, editProfileBtn, add_productBtn, filterOrderBtn, s_ShopReview_Btn, settings_Btn;
    private EditText searchView;
    private CircularImageView Sm_profileImage;
    private RelativeLayout productsRl, ordersRl;
    private RecyclerView productRv, S_ordersRv;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    private ArrayList<ModelOrderShop> orderShopList;
    private AdapterOrderShop adapterOrderShop;


    Animation topAnim, bottomAnim, middleAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller_home_page);

        nameTv = findViewById(R.id.sM_Name);
        // logout = findViewById(R.id.s_logoutBtn);
       // editProfileBtn = findViewById(R.id.s_editBtn);
        Sm_shop = findViewById(R.id.sM_shop);
        Sm_email = findViewById(R.id.sM_email);
        add_productBtn = findViewById(R.id.sM_Product_Btn);
        Sm_profileImage= findViewById(R.id.sM_profileImage);
        tabProducts = findViewById(R.id.tabProduct);
        tabOrders = findViewById(R.id.tabOrders);
        productsRl = findViewById(R.id.productRl);
        ordersRl = findViewById(R.id.ordersRl);
        searchView = findViewById(R.id.searchProductView);
        filterSearchBtn = findViewById(R.id.filterProductBtn);
        filterOrderBtn = findViewById(R.id.filterOrderBtn);
        filteredProductTv = findViewById(R.id.filteredProductTv);
        filterOrderTv = findViewById(R.id.filterOrderTv);
        productRv = findViewById(R.id.productsRv);
        S_ordersRv = findViewById(R.id.S_ordersRv);
       // s_ShopReview_Btn = findViewById(R.id.s_ShopReview_Btn);
        //settings_Btn = findViewById(R.id.settings_Btn);



        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        productRv.setAnimation(middleAnim);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        // progressDialog.setMessage("Profile Image Updating...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadAllProduct();
        loadAllOrders();
        showProductsUI();

        // Search
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductSeller.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

      //  logout.setOnClickListener(new View.OnClickListener() {
           // @Override
          //  public void onClick(View view) {
                //Make offline
                //Sign out
                //Go to login activity
           //     makeMeOffline();

       //     }
       // });

       // editProfileBtn.setOnClickListener(new View.OnClickListener() {
         //   @Override
        //    public void onClick(View view) {
                //Open edit page
        //        startActivity(new Intent(MainSellerActivity.this, Edit_Profile_page_Seller_Activity.class ));
        //    }
       // });

        add_productBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Edit add_product activity
                startActivity(new Intent(getApplicationContext(), add_ProductActivity.class));
                overridePendingTransition(R.anim.from_left, R.anim.to_right_slide);

            }
        });

        tabProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load products
                showProductsUI();

            }
        });

        tabOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load orders
                showOrdersUI();

            }
        });

        filterSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Filter Product:")
                        .setItems(Constants.productcategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get selected item
                                String selected = Constants.productcategory1[which];
                                filteredProductTv.setText(selected);
                                if (selected.equals("All")){
                                    //load all
                                    loadAllProduct();
                                }
                                else {
                                    // Load filtered
                                    loadFilteredProduct(selected);
                                }
                            }
                        })
                .show();
            }
        });

        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options = {"All", "In Progress", "Completed", "Cancelled"};
                // Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Filter Orders:")
                      .setItems(options, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              if(which == 0){
                                  // All checked
                                  filterOrderTv.setText("Showing All Orders");
                                  adapterOrderShop.getFilter().filter(""); // Shows all orders
                              }

                              else {
                                  String optionClicked = options[which];
                                  filterOrderTv.setText("Showing "+optionClicked+ "Orders");
                                  adapterOrderShop.getFilter().filter(optionClicked);

                              }

                          }
                      }).show();

            }
        });

       // s_ShopReview_Btn.setOnClickListener(new View.OnClickListener() {
          //  @Override
          //  public void onClick(View view) {
                // Open the same review activity as in the main user
            //    Intent intent = new Intent(MainSellerActivity.this, ShopReviewsActivity.class);
             //   intent.putExtra("shopUid", ""+firebaseAuth.getUid());
            //    startActivity(intent);
          //  }
       // });

       // settings_Btn.setOnClickListener(new View.OnClickListener() {
           // @Override
           // public void onClick(View view) {
                // Open settings
           //     startActivity(new Intent(MainSellerActivity.this, SettingActivity.class));
           // }
       // });


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

        ImageView sr = new ImageView(this);
        sr.setImageResource(R.drawable.ic_star);
        SubActionButton shopReview = itemBuilder.setContentView(sr).build();

        ImageView lo = new ImageView(this);
        lo.setImageResource(R.drawable.ic_logout);
        SubActionButton logout = itemBuilder.setContentView(lo).build();

        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(edit)
                .addSubActionView(settings)
                .addSubActionView(logout)
                .addSubActionView(shopReview)
                .attachTo(actionButton)
                .build();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                //Open edit page
                startActivity(new Intent(MainSellerActivity.this, Edit_Profile_page_Seller_Activity.class ));

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                startActivity(new Intent(MainSellerActivity.this, SettingActivity.class));

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                makeMeOffline();

            }
        });

        shopReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                // Open the same review activity as in the main user
                Intent intent = new Intent(MainSellerActivity.this, ShopReviewsActivity.class);
                intent.putExtra("shopUid", ""+firebaseAuth.getUid());
                startActivity(intent);

            }
        });
    }



    private void loadAllOrders() {
        //Initiating order list for seller
        orderShopList = new ArrayList<>();

        //Seller get orders
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Clear list before adding new data
                        orderShopList.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);

                            // Adding to list
                            orderShopList.add(modelOrderShop);
                        }

                        // SetUp adapter
                        adapterOrderShop = new AdapterOrderShop(MainSellerActivity.this, orderShopList);

                        // set to recyclerview
                        S_ordersRv.setAdapter(adapterOrderShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadFilteredProduct(final String selected) {
        productList = new ArrayList<>();

        //Get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Before getting reset list
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            String productCategory = ""+ds.child("productCategory").getValue();

                            // If selected category matches product category, then add in list
                            if(selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }

                        }

                        // Setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        // Set adapter
                        productRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadAllProduct() {
        productList = new ArrayList<>();

        //Get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Before getting reset list
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }

                        // Setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        // Set adapter
                        productRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void showProductsUI() {
        // Show products UI and hide orders UI
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProducts.setTextColor(getResources().getColor(R.color.colorBrown));
        tabProducts.setBackgroundResource(R.drawable.shape_rec_05);

        tabOrders.setTextColor(getResources().getColor(R.color.brownLightcolor));
        tabOrders.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        // Show products UI and hide orders UI
        ordersRl.setVisibility(View.VISIBLE);
        productsRl.setVisibility(View.GONE);

        tabProducts.setTextColor(getResources().getColor(R.color.brownLightcolor));
        tabProducts.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrders.setTextColor(getResources().getColor(R.color.colorBrown));
        tabOrders.setBackgroundResource(R.drawable.shape_rec_05);

    }

    private void makeMeOffline() {

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
                            Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class ));
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
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                           // String accountType = ""+ds.child("accountType").getValue();
                            String email = ""+ds.child("email").getValue();
                            String shop = ""+ds.child("shop").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();

                            nameTv.setText(name);
                            Sm_shop.setText(shop);
                            Sm_email.setText(email);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(Sm_profileImage);
                            }

                            catch (Exception e){
                                Sm_profileImage.setImageResource(R.drawable.ic_store);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
