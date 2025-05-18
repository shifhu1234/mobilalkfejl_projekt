package com.example.mobil_alkfejl_proj;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Objects;

public class NotificationHandler {
    private static final String CHANNEL_ID = "buy_notification_channel";
    private final int NOTIFICATION_ID = 0;

    private final NotificationManager mNotifyManager;
    private final Context mContext;


    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel
                (CHANNEL_ID, "Friss kosár", NotificationManager.IMPORTANCE_HIGH);

//        channel.enableLights(true);
//        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setDescription("Köszönjük a vásárlást! ;)");

        mNotifyManager.createNotificationChannel(channel);
    }

    public void send(String message) {
        Intent intent = new Intent(mContext, CategoryActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Köszönjük a vásárlást!")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launch)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);;

        if (Objects.equals(message, "Nézd meg mai napi kínálatunkat!")) {
            builder.setContentTitle("Készen állsz?");
            try {
                CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
                String cameraId = cameraManager.getCameraIdList()[0];

                cameraManager.setTorchMode(cameraId, true);
                Thread.sleep(300);
                cameraManager.setTorchMode(cameraId, false);
                Thread.sleep(300);
                cameraManager.setTorchMode(cameraId, true);
                Thread.sleep(300);
                cameraManager.setTorchMode(cameraId, false);


            } catch (CameraAccessException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        mNotifyManager.notify(NOTIFICATION_ID, builder.build());
    }


    public void cancel() {
        mNotifyManager.cancel(NOTIFICATION_ID);
    }
}
