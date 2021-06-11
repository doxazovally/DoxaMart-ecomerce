package com.team.my_gorcery.com.team.my_gorcery.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team.my_gorcery.R;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class    Register_SellerActivity extends AppCompatActivity implements LocationListener {
    private ImageButton S_backBtn, S_gpsBtn;
    private CircleImageView S_profile;
    private EditText S_name, S_shop, S_phone, S_deliveryFee, S_countey, S_state, S_city, S_address, S_email, S_password, S_cpassword;
    private Button S_RegBtn;
    private RelativeLayout seller_regHead;

    // permission constants
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //Image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // permission arrays
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //Image picked uri
    private Uri image_uri;

    private double latitude = 0.0, longitude = 0.0;

    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    Animation topAnim, bottomAnim, middleAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__seller);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);


        S_backBtn = findViewById(R.id.S_backBtn);
        S_gpsBtn = findViewById(R.id.S_gpsBtn);
        S_profile = findViewById(R.id.S_profileImage);
        S_name = findViewById(R.id.S_name);
        S_shop = findViewById(R.id.Shop);
        S_deliveryFee = findViewById(R.id.deliveryFee);
        S_phone = findViewById(R.id.S_phone);
        S_countey = findViewById(R.id.S_country);
        S_state = findViewById(R.id.S_state);
        S_city = findViewById(R.id.S_city);
        S_address = findViewById(R.id.S_address);
        S_email = findViewById(R.id.S_Reg_email);
        S_password = findViewById(R.id.S_Reg_password);
        S_cpassword = findViewById(R.id.S_c_password);
        S_RegBtn = findViewById(R.id.S_Reg_btn);
        seller_regHead = findViewById(R.id.seller_reg_head);

        S_profile.setAnimation(bottomAnim);
        S_name.setAnimation(topAnim);
        S_shop.setAnimation(topAnim);
        S_deliveryFee.setAnimation(topAnim);
        S_phone.setAnimation(topAnim);
        S_countey.setAnimation(topAnim);
        S_state.setAnimation(topAnim);
        S_city.setAnimation(topAnim);
        S_address.setAnimation(topAnim);
        S_email.setAnimation(topAnim);
        S_password.setAnimation(topAnim);
        S_cpassword.setAnimation(topAnim);
        S_RegBtn.setAnimation(topAnim);
        //seller_regHead.setAnimation(bottomAnim);

        // Initialize permission
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
       // progressDialog.setMessage("Profile Image Updating...");
        progressDialog.setCanceledOnTouchOutside(false);
       // progressDialog.show();


        S_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                //overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_from_top);
            }
        });

        S_gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // detect current location
                if(checkLocationPermission()){
                    //already allowed
                    detectLocation();
                }
                else {
                    //not allowed, request
                    requestLocationPermission();
                }

            }
        });

        S_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pick seller Image
                showImagedDialog();
            }
        });

        S_RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // register seller
                inputData();
            }
        });

    }

    private String fullName, shopName, phoneNumber, deliveryFee, country, state, city, address, email, password, confirmPassword;
    private void inputData() {
        //Input data
        fullName = S_name.getText().toString().trim();
        shopName = S_shop.getText().toString().trim();
        phoneNumber = S_phone.getText().toString().trim();
        deliveryFee = S_deliveryFee.getText().toString().trim();
        country = S_countey.getText().toString().trim();
        state = S_state.getText().toString().trim();
        city = S_city.getText().toString().trim();
        address = S_address.getText().toString().trim();
        email = S_email.getText().toString().trim();
        password = S_password.getText().toString().trim();
        confirmPassword = S_cpassword.getText().toString().trim();

        //Validate data
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Please enter your Name...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(shopName)){
            Toast.makeText(this, "Please enter your Shop Name...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Please enter your Phone Number...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(deliveryFee)){
            Toast.makeText(this, "Please enter your Delivery Fee...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(country)){
            Toast.makeText(this, "Please enter your Country...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(state)){
            Toast.makeText(this, "Please enter your State...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(city)){
            Toast.makeText(this, "Please enter your City...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email pattern...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length()<6){
            Toast.makeText(this, "Password must be atleast 6 characters...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(confirmPassword)){
            Toast.makeText(this, "Password dosen't match...", Toast.LENGTH_SHORT).show();

        }

        createAccount();

        if(latitude == 0.0 || longitude == 0.0){
           // Toast.makeText(this, "Please click GPS button to detect location...", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private void createAccount() {
        progressDialog.setMessage("Creating Your Account");
        progressDialog.show();

        //Create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Account created
                        saverFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Failure in creating account
                        progressDialog.dismiss();
                        Toast.makeText(Register_SellerActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void saverFirebaseData() {
        progressDialog.setMessage("Saving Account Info...");

        final String timestamp = ""+System.currentTimeMillis();

        if(image_uri == null){
            //Save info without image

            //Setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" +firebaseAuth.getUid());
            hashMap.put("email", "" +email);
            hashMap.put("name", "" +fullName);
            hashMap.put("shop", "" +shopName);
            hashMap.put("phone", "" +phoneNumber);
            hashMap.put("deliveryFee", "" +deliveryFee);
            hashMap.put("country", "" +country);
            hashMap.put("state", "" +state);
            hashMap.put("city", "" +city);
            hashMap.put("address", "" +address);
            hashMap.put("latitude", "" +latitude);
            hashMap.put("longitude", "" +longitude);
            hashMap.put("timestamp", "" +timestamp);
            hashMap.put("accountType", "Seller");
            hashMap.put("Online", "true");
            hashMap.put("shopOpen", "true");
            hashMap.put("profileImage", "");

            //Save to Firebase Database
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Database updated
                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                Toast.makeText(Register_SellerActivity.this,"Please check your email for verification", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Register_SellerActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(Register_SellerActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to update to Firebase Database
                            progressDialog.dismiss();
                            startActivity(new Intent(Register_SellerActivity.this, MainSellerActivity.class));
                            finish();

                        }
                    });

        }
        else{
            //Save info with image

            //Name and path of image
            String filePathAndName = "profile_images/" + ""+firebaseAuth.getUid();
            //Upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                //Setup data to save
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", "" +firebaseAuth.getUid());
                                hashMap.put("email", "" +email);
                                hashMap.put("name", "" +fullName);
                                hashMap.put("shop", "" +shopName);
                                hashMap.put("phone", "" +phoneNumber);
                                hashMap.put("deliveryFee", "" +deliveryFee);
                                hashMap.put("country", "" +country);
                                hashMap.put("state", "" +state);
                                hashMap.put("city", "" +city);
                                hashMap.put("address", "" +address);
                                hashMap.put("latitude", "" +latitude);
                                hashMap.put("longitude", "" +longitude);
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("accountType", "Seller");
                                hashMap.put("Online", "true");
                                hashMap.put("shopOpen", "true");
                                hashMap.put("profileImage", "" +downloadImageUri); // Url of the uploaded image

                                //Save to Firebase Database
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Database updated
                                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    Toast.makeText(Register_SellerActivity.this,"Please check your email for verification", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(Register_SellerActivity.this, LoginActivity.class));
                                                                    finish();
                                                                }
                                                                else {
                                                                    Toast.makeText(Register_SellerActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to update to Firebase Database
                                                progressDialog.dismiss();
                                                startActivity(new Intent(Register_SellerActivity.this, MainSellerActivity.class));
                                                finish();

                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Register_SellerActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void showImagedDialog() {
        //option to display in dialog
        String [] options = {"Camera", "Gallery"};

        //Image dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Handle clicks
                        if(which == 0){

                            //Camera clicked
                            if(checkCameraPermission()){
                                //camera permission allowed
                                pickFromCamera();
                            }
                            else {
                                //camera permission not allowed, request
                                requestCameraPermission();
                            }
                        }
                        else {
                            //Gallery clicked
                            if(checkStoragePermission()){
                                //Storage permission allowed
                                pickFromGallery();

                            }
                            else {
                                //Storage permission not allowed, request
                                requestStoragePermission();

                            }

                        }
                    }

                })
                .show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);


    }

    private void detectLocation() {
       Toast.makeText(this, "Please wait....", Toast.LENGTH_LONG).show();
       locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
       locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void findAddress() {
        //find address, country, state, city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0); //complete adderss
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            //set addresses
            S_countey.setText(country);
            S_state.setText(state);
            S_city.setText(city);
            S_address.setText(address);

        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);

    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onLocationChanged(Location location) {
        //location detection
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        //gps location disabled
        Toast.makeText(this, "Please turn on your phone location...", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE && data!= null){

                /* Get picked image */
               image_uri = data.getData();
                //Set to image
                S_profile.setImageURI(image_uri);
            } else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //Set to image
                S_profile.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}