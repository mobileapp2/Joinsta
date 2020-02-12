package in.oriange.joinsta.fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import java.util.Random;

import in.oriange.joinsta.R;
import in.oriange.joinsta.activities.ContactUs_Activity;
import in.oriange.joinsta.activities.Enquiries_Activity;
import in.oriange.joinsta.activities.Favourites_Activity;
import in.oriange.joinsta.activities.HowToUse_Activity;
import in.oriange.joinsta.activities.MyEvents_Activity;
import in.oriange.joinsta.activities.Notification_Activity;
import in.oriange.joinsta.activities.Policy_Activity;
import in.oriange.joinsta.activities.ProfileDetails_Activity;
import in.oriange.joinsta.activities.Requirements_Activity;
import in.oriange.joinsta.activities.Settings_Activity;

public class More_Fragment extends Fragment {

    private Context context;
    private CardView cv_profiledetails, cv_myevents, cv_favourite, cv_notifications, cv_requirements,
            cv_settings, cv_enquires, cv_policies, cv_contactus, cv_howto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        cv_profiledetails = rootView.findViewById(R.id.cv_profiledetails);
        cv_myevents = rootView.findViewById(R.id.cv_myevents);
        cv_favourite = rootView.findViewById(R.id.cv_favourite);
        cv_notifications = rootView.findViewById(R.id.cv_notifications);
        cv_requirements = rootView.findViewById(R.id.cv_requirements);
        cv_settings = rootView.findViewById(R.id.cv_settings);
        cv_enquires = rootView.findViewById(R.id.cv_enquires);
        cv_policies = rootView.findViewById(R.id.cv_policies);
        cv_contactus = rootView.findViewById(R.id.cv_contactus);
        cv_howto = rootView.findViewById(R.id.cv_howto);
    }

    private void setDefault() {

    }

    private void setEventHandler() {
        cv_profiledetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileDetails_Activity.class));
            }
        });

        cv_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Favourites_Activity.class));
            }
        });

        cv_myevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MyEvents_Activity.class));
            }
        });

        cv_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Notification_Activity.class));
            }
        });

        cv_requirements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Requirements_Activity.class));
            }
        });

        cv_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Settings_Activity.class));
            }
        });

        cv_enquires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Enquiries_Activity.class));
            }
        });

        cv_policies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Policy_Activity.class));
            }
        });

        cv_contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ContactUs_Activity.class));
            }
        });

        cv_howto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, HowToUse_Activity.class));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menus_notification, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_notification:
                startActivity(new Intent(context, Notification_Activity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEnquiryNotification(Context mContext, Intent notificationIntent, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        PendingIntent pendingIntent = PendingIntent.getActivity((mContext), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(mContext, channelId);
        } else {
            builder = new NotificationCompat.Builder(mContext);
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + "8149115089"));
        PendingIntent callPendingIntent = PendingIntent.getActivity(context, 0, callIntent, 0);

        Notification notification = builder
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentTitle(title)
                .setSmallIcon(R.drawable.icon_notification_logo)
                .setSound(notificationSound)
                .setLights(Color.YELLOW, 1000, 1000)
                .setVibrate(new long[]{500, 500})
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .addAction(R.drawable.icon_call, "CALL", callPendingIntent)
                .build();

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        notificationManager.notify(m, notification);
    }


}
