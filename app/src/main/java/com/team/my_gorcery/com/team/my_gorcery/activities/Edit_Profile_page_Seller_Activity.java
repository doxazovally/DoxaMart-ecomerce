package com.team.my_gorcery.com.team.my_gorcery.activities;

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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.team.my_gorcery.R;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_Profile_page_Seller_Activity extends AppCompatActivity implements LocationListener {

    private ImageButton backBtn, GpsBtn;
    private CircleImageView S_edit_profile;
    private EditText sName, sPhone, sShopName, sDeliveryFee, sCountry, sState, sCity, sAddress;
    private Button sUpdateBtn;
    private SwitchCompat shopOpenSwitch;
    private RelativeLayout seller_heading;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    Animation topAnim, bottomAnim, middleAnim;

    //Permission constants
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page__seller_);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        backBtn = findViewById(R.id.eS_backBtn);
        GpsBtn = findViewById(R.id.eS_gpsBtn);
        S_edit_profile = findViewById(R.id.Sedit_Profile_Image);
        sName = findViewById(R.id.sp_name);
        sShopName = findViewById(R.id.sp_Shop);
        sDeliveryFee = findViewById(R.id.sp_deliveryFee);
        sPhone = findViewById(R.id.sp_phone);
        sCountry = findViewById(R.id.sp_country);
        sState = findViewById(R.id.sp_state);
        sCity = findViewById(R.id.sp_city);
        sAddress = findViewById(R.id.sp_address);
        sUpdateBtn = findViewById(R.id.sP_Reg_btn);
        shopOpenSwitch = findViewById(R.id.switchBtn);
        seller_heading = findViewById(R.id.seller_profile_head);


        sName.setAnimation(middleAnim);
        sShopName.setAnimation(middleAnim);
        sDeliveryFee.setAnimation(middleAnim);
        sPhone.setAnimation(middleAnim);
        sCountry.setAnimation(middleAnim);
        sState.setAnimation(middleAnim);
        sCity.setAnimation(middleAnim);
        sAddress.setAnimation(middleAnim);
        sUpdateBtn.setAnimation(bottomAnim);
        shopOpenSwitch.setAnimation(middleAnim);
        S_edit_profile.setAnimation(bottomAnim);
        seller_heading.setAnimation(middleAnim);



        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Updating profile
                inputData();
            }
        });

        S_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagedDialog();
            }
        });

        GpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // detect current location
                if (checkLocationPermission()) {
                    //already allowed
                    detectLocation();
                } else {
                    //not allowed, request
                    requestLocationPermission();
                }

            }
        });
    }

    String fullName, shopName, phoneNumber, deliveryFee, country, state, city, address;
    private boolean shopOpen;

    private void inputData() {
        fullName = sName.getText().toString().trim();
        shopName = sShopName.getText().toString().trim();
        phoneNumber = sPhone.getText().toString().trim();
        deliveryFee = sDeliveryFee.getText().toString().trim();
        country = sCountry.getText().toString().trim();
        state = sState.getText().toString().trim();
        city = sCity.getText().toString().trim();
        address = sAddress.getText().toString().trim();
        shopOpen = shopOpenSwitch.isChecked(); // True of False

        sUpdateBtn();
    }

    private void sUpdateBtn() {
        progressDialog.setMessage("Updating Profile...");
        progressDialog.show();

        if(image_uri == null){
            //Save info without image

            //Setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
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
            hashMap.put("shopOpen", ""+shopOpen);

            //Save to Firebase Database
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Database updated
                            progressDialog.dismiss();
                            Toast.makeText(Edit_Profile_page_Seller_Activity.this,"Profile Updated Successfully....", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to update to Firebase Database
                            progressDialog.dismiss();
                            Toast.makeText(Edit_Profile_page_Seller_Activity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
                                hashMap.put("shopOpen", ""+shopOpen);
                                hashMap.put("profileImage", "" +downloadImageUri); // Url of the uploaded image

                                //Save to Firebase Database
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Database updated
                                                progressDialog.dismiss();
                                                Toast.makeText(Edit_Profile_page_Seller_Activity.this,"Profile Updated Successfully....", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to update to Firebase Database
                                                progressDialog.dismiss();
                                                Toast.makeText(Edit_Profile_page_Seller_Activity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Edit_Profile_page_Seller_Activity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class ));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String uid = ""+ds.child("uid").getValue();
                            String email = ""+ds.child("email").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String Online = ""+ds.child("Online").getValue();
                            String address = ""+ds.child("address").getValue();
                            String country = ""+ds.child("country").getValue();
                            String state = ""+ds.child("state").getValue();
                            String city = ""+ds.child("city").getValue();
                            String deliveryFee = ""+ds.child("deliveryFee").getValue();
                            latitude = Double.parseDouble(""+ds.child("latitude").getValue());
                            longitude = Double.parseDouble(""+ds.child("longitude").getValue());
                            String name = ""+ds.child("name").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String shop = ""+ds.child("shop").getValue();
                            String shopOpen = ""+ds.child("shopOpen").getValue();


                            sName.setText(name);
                            sPhone.setText(phone);
                            sShopName.setText(shop);
                            sDeliveryFee.setText(deliveryFee);
                            sCountry.setText(country);
                            sState.setText(state);
                            sCity.setText(city);
                            sAddress.setText(address);

                            if(shopOpen.equals("true")){
                                shopOpenSwitch.setChecked(true);
                            }
                            else {
                                shopOpenSwitch.setChecked(false);
                            }
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(S_edit_profile);

                            }
                            catch (Exception e){
                                S_edit_profile.setImageResource(R.drawable.ic_profile);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
    }

    private void showImagedDialog() {
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

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);

    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);


    }

    private void detectLocation() {
        Toast.makeText(this, "Please wait....", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
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
            sCountry.setText(country);
            sState.setText(state);
            sCity.setText(city);
            sAddress.setText(address);

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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


    @Override
    public void onLocationChanged(Location location) {
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
        Toast.makeText(this, "Please turn on your phone location...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        // permission allowed
                        detectLocation();
                    } else {
                        // permission denied
                        Toast.makeText(this, "Location permission is important...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

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

    //Handle Image clicks
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE && data != null) {

                /* Get picked image */
                image_uri = data.getData();
                //Set to image
                S_edit_profile.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //Set to image
                S_edit_profile.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
