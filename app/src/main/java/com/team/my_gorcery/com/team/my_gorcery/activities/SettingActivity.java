package com.team.my_gorcery.com.team.my_gorcery.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.team.my_gorcery.Constants;
import com.team.my_gorcery.R;

public class SettingActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private TextView notificationStatus;
    private SwitchCompat notification_switchBtn;

    private  static final String enabledMessage = "Notifications are enabled";
    private  static final String disabledMessage = "Notifications are disabled";

    private  boolean isChecked = false;

    private FirebaseAuth firebaseAuth;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        backBtn = findViewById(R.id.backBtn);
        notification_switchBtn = findViewById(R.id.notification_switchBtn);
        notificationStatus = findViewById(R.id.notificationStatus);

        firebaseAuth = FirebaseAuth.getInstance();

        sp = getSharedPreferences("SETTINGTS_SP", MODE_PRIVATE);

        // Check last selected option true/false
        isChecked = sp.getBoolean("FCM_ENABLED", false);
        notification_switchBtn.setChecked(isChecked);
        if (isChecked){
            notificationStatus.setText(enabledMessage);
        }
        else {
            notificationStatus.setText(disabledMessage);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        notification_switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // Checked. enable notifications
                    subscribeToTopic();
                }
                else {
                    // Unchecked. disable notifications
                    unsubscribeToTopic();
                }
            }
        });

    }

    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // subscribed successfully
                        // Save settings
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", false);
                        spEditor.apply();

                        Toast.makeText(SettingActivity.this, ""+enabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatus.setText(enabledMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to subscribed
                        Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unsubscribeToTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Unsubscribed successfully
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", true);
                        spEditor.apply();

                        Toast.makeText(SettingActivity.this, ""+disabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatus.setText(disabledMessage);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to unsubscribed
                        Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
