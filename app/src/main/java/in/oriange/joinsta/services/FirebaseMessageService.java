package in.oriange.joinsta.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Random;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.GroupNotifications_Activity;
import in.oriange.joinsta.activities.Notification_Activity;

public class FirebaseMessageService extends FirebaseMessagingService {

    private final static String TAG = FirebaseMessageService.class.getSimpleName();

    public static final int NOTIFICATION_REQUEST_CODE = 100;
    private static PendingIntent pendingIntent;
    private static Notification.Builder builder;
    private static NotificationManagerCompat notificationManager;
    private static Uri notificationSound;
    private static Bitmap iconBitmap;
    private static Random random;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.wtf(TAG, remoteMessage.getData().get("title") + " " +
                remoteMessage.getData().get("message") + " " +
                remoteMessage.getData().get("image") + " " +
                remoteMessage.getData().get("icon") + " " +
                remoteMessage.getData().get("type") + " " +
                remoteMessage.getData().get("userId") + " " +
                remoteMessage.getData().get("taskId"));

        if (remoteMessage.getData() == null) {
            return;
        }

        Intent notificationIntent = null;

        if (remoteMessage.getData().get("notification_type").equals("2")) {
            notificationIntent = new Intent(getApplicationContext(), GroupNotifications_Activity.class)
                    .putExtra("groupId", remoteMessage.getData().get("group_id"))
                    .putExtra("msg_id", remoteMessage.getData().get("msg_id"))
                    .putExtra("groupName", "");
        } else if (remoteMessage.getData().get("notification_type").equals("1")) {
            notificationIntent = new Intent(getApplicationContext(), Notification_Activity.class);
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (remoteMessage.getData().get("image") != null && remoteMessage.getData().get("image").isEmpty()) {
            showNewNotification(
                    getApplicationContext(),
                    notificationIntent,
                    remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("message"),
                    remoteMessage.getData().get("image"),
                    remoteMessage.getData().get("icon"),
                    remoteMessage.getData().get("type"),
                    remoteMessage.getData().get("userId"),
                    remoteMessage.getData().get("taskId"),
                    remoteMessage.getData().get("notification_type"),
                    remoteMessage.getData().get("group_id"),
                    remoteMessage.getData().get("msg_id"));
        } else {
            generatepicture(
                    getApplicationContext(),
                    null,
                    remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("message"),
                    remoteMessage.getData().get("image"),
                    remoteMessage.getData().get("notification_type"),
                    remoteMessage.getData().get("group_id"),
                    remoteMessage.getData().get("msg_id"));
        }
    }

    public static void showNewNotification(Context context, Intent intent, String title, String msg, String image, String icon, String type, String userId,
                                           String taskId, String notification_type, String group_id, String msg_id) {

        Log.wtf(TAG, "showNewNotification: ");

        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder = new Notification.Builder(context);
        notificationManager = NotificationManagerCompat.from(context);

        int m1 = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        Intent notificationIntent = null;
        if (intent != null) {
            notificationIntent = intent;
            Bundle data = new Bundle();
            data.putString("action", "notificationfromfcm");
            data.putString("taskId", taskId);
            data.putString("type", type);
            notificationIntent.putExtras(data);
        } else {
            if (notification_type.equals("2")) {
                notificationIntent = new Intent(context, GroupNotifications_Activity.class)
                        .putExtra("groupId", group_id)
                        .putExtra("msg_id", msg_id)
                        .putExtra("groupName", "");
            } else if (notification_type.equals("1")) {
                notificationIntent = new Intent(context, Notification_Activity.class);
            }

            Bundle data = new Bundle();
            data.putString("action", "notificationfromfcm");
            data.putString("taskId", taskId);
            data.putString("type", type);
            notificationIntent.putExtras(data);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel("my_notification", "n_channel", NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("description");
            notificationChannel.setName("Channel Name");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        pendingIntent = PendingIntent.getActivity((context), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification;
        notification = builder
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(R.drawable.icon_notification_logo)
                .setSound(notificationSound)
                .setLights(Color.YELLOW, 1000, 1000)
                .setVibrate(new long[]{500, 500})
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(m1, notification);
    }

    public static void generatepicture(Context context, Intent notificationIntent, String title, String message, String imageUrl,
                                       String notification_type, String group_id, String msg_id) {
        Intent intent = null;
        if (notificationIntent != null) {
            intent = notificationIntent;
        } else {
            if (notification_type.equals("2")) {
                intent = new Intent(context, GroupNotifications_Activity.class)
                        .putExtra("groupId", group_id)
                        .putExtra("msg_id", msg_id)
                        .putExtra("groupName", "");
            } else if (notification_type.equals("1")) {
                intent = new Intent(context, Notification_Activity.class);
            }
        }
        new generatePictureStyleNotification(context, intent, title, message,
                imageUrl).execute();

    }

    public static class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, Intent notificationIntent, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;

            pendingIntent = PendingIntent.getActivity((context), 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            Log.wtf(TAG, "generatePictureStyleNotification: ");
            builder = new Notification.Builder(mContext);
            notificationManager = NotificationManagerCompat.from(mContext);

            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
                builder = new Notification.Builder(mContext, channelId);
            } else {
                builder = new Notification.Builder(mContext);
            }


            Notification notification;
            if (result == null || this.imageUrl == null || this.imageUrl.isEmpty()) {
                notification = builder
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.icon_notification_logo)
                        .setSound(notificationSound)
                        .setLights(Color.YELLOW, 1000, 1000)
                        .setVibrate(new long[]{500, 500})
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build();
            } else {
                notification = builder
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.icon_notification_logo)
                        .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                        .setSound(notificationSound)
                        .setLights(Color.YELLOW, 1000, 1000)
                        .setVibrate(new long[]{500, 500})
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build();
            }
            random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;

            notificationManager.notify(m, notification);
        }
    }


}