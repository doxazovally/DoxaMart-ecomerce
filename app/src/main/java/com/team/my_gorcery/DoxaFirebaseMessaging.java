package com.team.my_gorcery;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.team.my_gorcery.com.team.my_gorcery.activities.OrderDetailsSeller;
import com.team.my_gorcery.com.team.my_gorcery.activities.OrderDetailsUserActivity;

import java.util.Random;

public class DoxaFirebaseMessaging extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID";  // Required for 0 and above

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // All notifications will be received here

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Get data notification
        String notificationType = remoteMessage.getData().get("notificationType");
        if(notificationType.equals("NewOrder")){
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationDescription");

            if(firebaseUser !=null && firebaseAuth.getUid().equals(sellerUid)){
                // user is signed in and is the same user to which notification is to be sent to
                showNotification(orderId, sellerUid, buyerUid, notificationTitle, notificationDescription, notificationType);
            }

        }
        if(notificationType.equals("OrderStatusChanged")){
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationMessage");

            if(firebaseUser !=null && firebaseAuth.getUid().equals(buyerUid)){
                // user is signed in and is the same user to which notification is to be sent to
                showNotification(orderId, sellerUid, buyerUid, notificationTitle, notificationDescription, notificationType);
            }
        }
    }

    private  void showNotification(String orderId, String sellerUid, String buyerUid, String notificationTitle, String notificationDescription, String notificationType){
        // Notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        // Id for notification, random
        int notificationID = new Random().nextInt(3000);

        // Check if android is Oreo/O or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setupNotificationChannel(notificationManager);
        }

        // Handle notification clicks, start order activity
        Intent intent = null;
        if(notificationType.equals("NewOrder")){
            // Open OrderDetailsSellerActivity
            intent = new Intent(this, OrderDetailsSeller.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("orderBy", buyerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }
        else if (notificationType.equals("OrderStatusChanged")){
            // Open OrderDetailsUserActivity
            intent = new Intent(this, OrderDetailsUserActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("orderTo", sellerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Large icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.download);

        // Sound of notification
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.download)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSound(notificationSoundUri)
                .setAutoCancel(true) // cancel/dismiss when clicked
                .setContentIntent(pendingIntent); // add intent

        // Show notification
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "Some Sample Text";
        String channelDescription = "Channel Description here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
