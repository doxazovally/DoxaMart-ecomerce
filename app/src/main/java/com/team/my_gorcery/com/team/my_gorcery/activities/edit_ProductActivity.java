package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.team.my_gorcery.Constants;
import com.team.my_gorcery.R;

import java.util.HashMap;

public class edit_ProductActivity extends AppCompatActivity {

    private ImageView productImage_icon;
    private ImageButton edit_back_btn;
    private EditText prodTitle, prodDescription, prodQuanti, prodPrice, prodPriceDis, prodDisNote, prod_deliveryDays, prod_return_days;
    private TextView prodCate;
    private Button update_prod_btn;
    private SwitchCompat ProdSwitchBtn;
    private RelativeLayout prod_headding;

    private  String productId;

    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    Animation topAnim, bottomAnim, middleAnim;

    //Permission Constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //Image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //Image picked uri
    private Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__product);

        prodTitle = findViewById(R.id.prod_Title);
        productImage_icon = findViewById(R.id.productIcon);
        prodDescription = findViewById(R.id.prod_Description);
        prodCate = findViewById(R.id.prod_Category);
        prodQuanti = findViewById(R.id.prod_Quantity);
        prodPrice = findViewById(R.id.prod_Price);
        prodDisNote = findViewById(R.id.prod_Dis_price_Note);
        prodPriceDis = findViewById(R.id.prod_Dis_Price);
        update_prod_btn = findViewById(R.id.update_prod_Btn);
        ProdSwitchBtn = findViewById(R.id.switchBtn);
        prod_headding = findViewById(R.id.add_prod_headding);
        edit_back_btn = findViewById(R.id.edit_prod_backBtn);
        prod_deliveryDays = findViewById(R.id.prod_deliveryDays);
        prod_return_days = findViewById(R.id.prod_return_days);

        // Get id of the product from intent
        productId = getIntent().getStringExtra("productId");

        // On start, Unchecked, hide discount note and price
        prodPriceDis.setVisibility(View.GONE);
        prodDisNote.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        loadProductDetails(); // to set on views

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        //Init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // if the switch is checked, show the other forms else don't show
        ProdSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // Checked. show discount price and note
                    prodPriceDis.setVisibility(View.VISIBLE);
                    prodDisNote.setVisibility(View.VISIBLE);
                } else {
                    // On start, Unchecked, hide
                    prodPriceDis.setVisibility(View.GONE);
                    prodDisNote.setVisibility(View.GONE);

                }
            }
        });

        productImage_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show dialog to pick Image
                showImagedPick();

            }
        });

        edit_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        prodCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pick from category
                categoryDialog();

            }
        });

        update_prod_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Flow:
                // 1. Input data
                // 2. Validate data
                // 3. Update data to db
                inputData();
            }
        });

    }

    private void loadProductDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get data
                        String productId = ""+dataSnapshot.child("productId").getValue();
                        String productName = ""+dataSnapshot.child("productName").getValue();
                        String productDescription = ""+dataSnapshot.child("productDescription").getValue();
                        String productCategory = ""+dataSnapshot.child("productCategory").getValue();
                        String productQuantity = ""+dataSnapshot.child("productQuantity").getValue();
                        String productPrice = ""+dataSnapshot.child("productPrice").getValue();
                        String priceDiscount = ""+dataSnapshot.child("priceDiscount").getValue();
                        String discountNote = ""+dataSnapshot.child("discountNote").getValue();
                        String productImage = ""+dataSnapshot.child("productImage").getValue();
                        String deliveryDate = ""+dataSnapshot.child("deliveryDate").getValue();
                        String returnDate = ""+dataSnapshot.child("returnDate").getValue();
                        String discountAvailability = ""+dataSnapshot.child("discountAvailability").getValue();
                        String timestamp = ""+dataSnapshot.child("timestamp").getValue();
                        String uid = ""+dataSnapshot.child("uid").getValue();

                        // Set to views
                        if(discountAvailability.equals("true")){
                            ProdSwitchBtn.setChecked(true);

                            prodPriceDis.setVisibility(View.VISIBLE);
                            prodDisNote.setVisibility(View.VISIBLE);
                        }
                        else {

                                ProdSwitchBtn.setChecked(false);

                                prodPriceDis.setVisibility(View.GONE);
                                prodDisNote.setVisibility(View.GONE);
                        }

                        prodTitle.setText(productName);
                        prodDescription.setText(productDescription);
                        prodCate.setText(productCategory);
                        prodQuanti.setText(productQuantity);
                        prodPrice.setText(productPrice);
                        prodPriceDis.setText(priceDiscount);
                        prodDisNote.setText(discountNote);
                        prod_deliveryDays.setText(deliveryDate);
                        prod_return_days.setText(returnDate);

                        try {
                            Picasso.get().load(productImage).placeholder(R.drawable.ic_add_shopping).into(productImage_icon);
                        }
                        catch (Exception e){
                            productImage_icon.setImageResource(R.drawable.ic_add_shopping);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String productTitle, productDescription, productCategory, productQuantity, OriginalPrice, discountPrice, discountNote, returnDate, DeliveryDate;
    private boolean discountAvailable = false;

    private void inputData() {
        // (1) Input data
        productTitle = prodTitle.getText().toString().trim();
        productDescription = prodDescription.getText().toString().trim();
        productQuantity = prodQuanti.getText().toString().trim();
        productCategory = prodCate.getText().toString().trim();
        OriginalPrice = prodPrice.getText().toString().trim();
        returnDate = prod_return_days.getText().toString().trim();
        DeliveryDate = prod_deliveryDays.getText().toString().trim();
        discountAvailable = ProdSwitchBtn.isChecked(); // true or false

        // (2) Validate data
        if (TextUtils.isEmpty(productTitle)) {
            Toast.makeText(this, "Enter Product Name...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(productDescription)) {
            Toast.makeText(this, "Describe your product...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(productQuantity)) {
            Toast.makeText(this, "What is your product quantity...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(productCategory)) {
            Toast.makeText(this, "Your product category is important...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(OriginalPrice)) {
            Toast.makeText(this, "Product price is missing...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(DeliveryDate)) {
            Toast.makeText(this, "Please add delivery days...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(returnDate)) {
            Toast.makeText(this, "Return date is missing...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (discountAvailable) {
            discountPrice = prodPriceDis.getText().toString().trim();
            discountNote = prodDisNote.getText().toString().trim();

            if (TextUtils.isEmpty(discountPrice)) {
                Toast.makeText(this, "You may need to add your discount price...", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // If the product does not have discount
            discountPrice = "0";
            discountNote = "";
        }

        updateProductToDB();
    }

    private void updateProductToDB() {
        // Updating edited product data to db
        progressDialog.setTitle("Updating Product...");
        progressDialog.show();

        if(image_uri == null){
            //Update without image

            // Setup data to updated
            HashMap<String, Object> hashMap = new HashMap<>();
           // hashMap.put("productId", "" +timestamp);
            hashMap.put("productName", "" +productTitle);
            hashMap.put("productDescription", "" +productDescription);
            hashMap.put("productCategory", "" +productCategory);
            hashMap.put("productQuantity", "" +productQuantity);
            hashMap.put("productPrice", "" +OriginalPrice);
            hashMap.put("deliveryDate", "" +DeliveryDate);
            hashMap.put("returnDate", "" +returnDate);
            hashMap.put("priceDiscount", "" +discountPrice);
            hashMap.put("discountNote", "" +discountNote);
            hashMap.put("productImage", ""); // no image uploaded
            hashMap.put("discountAvailability", "" +discountAvailable);
           // hashMap.put("timestamp", "" +timestamp);
           // hashMap.put("uid", "" +firebaseAuth.getUid());

            // Update to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(productId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Product updated to db
                            progressDialog.dismiss();
                            Toast.makeText(edit_ProductActivity.this,"Product has been updated successfully...", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to update data to Database
                            progressDialog.dismiss();
                            Toast.makeText(edit_ProductActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                            finish();

                        }
                    });
        }
        else {
            // Upload with image

            // First upload image to db

            // Name and path of image
            String filePathAndName = "product_images/" + ""+productId; // Overide previous image using same id
            //Upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Imaged uploaded
                            // Get the uploaded image url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                // Image url received, upload to db

                                // Setup data to be uploaded
                                HashMap<String, Object> hashMap = new HashMap<>();
                               // hashMap.put("productId", "" +timestamp);
                                hashMap.put("productName", "" +productTitle);
                                hashMap.put("productDescription", "" +productDescription);
                                hashMap.put("productCategory", "" +productCategory);
                                hashMap.put("productQuantity", "" +productQuantity);
                                hashMap.put("productPrice", "" +OriginalPrice);
                                hashMap.put("deliveryDate", "" +DeliveryDate);
                                hashMap.put("returnDate", "" +returnDate);
                                hashMap.put("priceDiscount", "" +discountPrice);
                                hashMap.put("discountNote", "" +discountNote);
                                hashMap.put("productImage", "" +downloadImageUri); // Url of the uploaded product image
                                hashMap.put("discountAvailability", "" +discountAvailable);

                                // Update to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Products").child(productId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Product updated to db
                                                progressDialog.dismiss();
                                                Toast.makeText(edit_ProductActivity.this,"Product has been updated successfully...", Toast.LENGTH_SHORT).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to update data to Database
                                                progressDialog.dismiss();
                                                Toast.makeText(edit_ProductActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                                finish();

                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to upload product image
                            progressDialog.dismiss();
                            Toast.makeText(edit_ProductActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

  //  private void clearData() {
        // Clear data after uploading last product data
      //  prodTitle.setText("");
      //  prodDescription.setText("");
       // prodCate.setText("");
       // prodQuanti.setText("");
       // prodPrice.setText("");
       // prodPriceDis.setText("");
       // prodDisNote.setText("");
       // productImage_icon.setImageResource(R.drawable.ic_add_shopping_cart_accent);
       // image_uri = null;
   // }

    private void categoryDialog() {
        //Cate. Dialoge
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productcategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //Get picked category
                        String category = Constants.productcategory[which];

                        // Set picked category
                        prodCate.setText(category);
                    }
                })
                .show();
    }

    private void showImagedPick() {
        //option to display in dialog
        String[] options = {"Camera", "Gallery"};

        //Image dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Handle clicks
                        if (which == 0) {

                            //Camera clicked
                            if (checkCameraPermission()) {
                                //camera permission allowed
                                pickFromCamera();
                            } else {
                                //camera permission not allowed, request
                                requestCameraPermission();
                            }
                        } else {
                            //Gallery clicked
                            if (checkStoragePermission()) {
                                //Storage permission allowed
                                pickFromGallery();

                            } else {
                                //Storage permission not allowed, request
                                requestStoragePermission();

                            }

                        }
                    }

                })
                .show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    //Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        // permission allowed
                        pickFromCamera();
                    } else {
                        // permission denied
                        Toast.makeText(this, "Camera permissions are important...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        // permission allowed
                        pickFromGallery();
                    } else {
                        // permission denied
                        Toast.makeText(this, "Storage permission is important...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    //Handle image pick result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE && data != null) {

                /* Get picked image */
                image_uri = data.getData();
                //Set to image
                productImage_icon.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //Set to image
                productImage_icon.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
