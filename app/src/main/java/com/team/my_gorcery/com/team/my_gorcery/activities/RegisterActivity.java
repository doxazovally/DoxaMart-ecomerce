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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class RegisterActivity extends AppCompatActivity implements LocationListener {
    private ImageButton backBtn, gpsBtn;
    private CircleImageView Uprofile;
    private EditText Uname, Uphone, Ucountry, Ustate, Ucity, Uaddress, Uemail, Upassword, Ucpassword;
    private Button RegBtn, non_seller;
    private RelativeLayout relativeLayout;

    // permission constants
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //Image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    //Image picked url
    private Uri image_uri;

    // permission arrays
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    Animation topAnim, bottomAnim, middleAnim;

    private double latitude = 0.0, longitude = 0.0;

    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        middleAnim = AnimationUtils.loadAnimation(this,R.anim.middle_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        backBtn = findViewById(R.id.backBtn);
        gpsBtn = findViewById(R.id.gpsBtn);
        Uprofile = findViewById(R.id.profileImage);
        Uname = findViewById(R.id.u_name);
        Uphone = findViewById(R.id.u_phone);
        Ucountry = findViewById(R.id.u_country);
        Ustate = findViewById(R.id.u_state);
        Ucity = findViewById(R.id.u_city);
        Uaddress = findViewById(R.id.u_address);
        Uemail = findViewById(R.id.Reg_email);
        Upassword = findViewById(R.id.Reg_password);
        Ucpassword = findViewById(R.id.U_c_password);
        RegBtn = findViewById(R.id.Reg_btn);
        non_seller = findViewById(R.id.U_L);
        relativeLayout = findViewById(R.id.userHeaddings);

        Uprofile.setAnimation(bottomAnim);
        //relativeLayout.setAnimation(middleAnim);
        Uname.setAnimation(middleAnim);
        Uphone.setAnimation(middleAnim);
        Ucountry.setAnimation(middleAnim);
        Ustate.setAnimation(middleAnim);
        Ucity.setAnimation(middleAnim);
        Uaddress.setAnimation(middleAnim);
        Uemail.setAnimation(middleAnim);
        Upassword.setAnimation(middleAnim);
        Ucpassword.setAnimation(middleAnim);
        RegBtn.setAnimation(middleAnim);
        non_seller.setAnimation(bottomAnim);

        // Initialize permission
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                //overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_from_top);
            }
        });
        gpsBtn.setOnClickListener(new View.OnClickListener() {
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

        Uprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pick Image
                showImagedDialog();
            }
        });

        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // register user
                inputData();
            }
        });

        findViewById(R.id.U_L).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register_SellerActivity.class));
                overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_from_top);
            }
        });
    }

    private String fullName, phoneNumber, country, state, city, address, email, password, confirmPassword;
    private void inputData() {
        //Input data
        fullName = Uname.getText().toString().trim();
        phoneNumber = Uphone.getText().toString().trim();
        country = Ucountry.getText().toString().trim();
        state = Ustate.getText().toString().trim();
        city = Ucity.getText().toString().trim();
        address = Uaddress.getText().toString().trim();
        email = Uemail.getText().toString().trim();
        password = Upassword.getText().toString().trim();
        confirmPassword = Ucpassword.getText().toString().trim();

        //Validate data
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Please enter your Name...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Please enter your Phone Number...", Toast.LENGTH_SHORT).show();
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

       // if(latitude == 0.0 || longitude == 0.0){
       //     Toast.makeText(this, "Please click GPS button to detect location...", Toast.LENGTH_SHORT).show();
      //      return;
     //   }

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
                 Toast.makeText(this, "Please click GPS button to detect location...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
            hashMap.put("phone", "" +phoneNumber);
            hashMap.put("country", "" +country);
            hashMap.put("state", "" +state);
            hashMap.put("city", "" +city);
            hashMap.put("address", "" +address);
            hashMap.put("latitude", "" +latitude);
            hashMap.put("longitude", "" +longitude);
            hashMap.put("timestamp", "" +timestamp);
            hashMap.put("accountType", "Buyer");
            hashMap.put("Online", "true");
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

                                                Toast.makeText(RegisterActivity.this,"Please check your email for verification", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                            startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
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
                                hashMap.put("phone", "" +phoneNumber);
                                hashMap.put("country", "" +country);
                                hashMap.put("state", "" +state);
                                hashMap.put("city", "" +city);
                                hashMap.put("address", "" +address);
                                hashMap.put("latitude", "" +latitude);
                                hashMap.put("longitude", "" +longitude);
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("accountType", "Buyer");
                                hashMap.put("Online", "true");
                                hashMap.put("shopOpen", "true");
                                hashMap.put("profileImage", "" +downloadImageUri); // Url of the uploaded image

                                //Save to Firebase Database
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    Toast.makeText(RegisterActivity.this,"Please check your email for verification", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                    finish();
                                                                }
                                                                else {
                                                                    Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                //Database updated
                                                progressDialog.dismiss();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Failed to update to Firebase Database
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
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
                           // Toast.makeText(RegisterActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
                                pickFromGallary();

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

    private void pickFromGallary(){
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
            Ucountry.setText(country);
            Ustate.setText(state);
            Ucity.setText(city);
            Uaddress.setText(address);

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
                        pickFromGallary();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE && data!= null){

                //Get picked image
                image_uri = data.getData();
                //Set to image
                Uprofile.setImageURI(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //Set to image
                Uprofile.setImageURI(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
