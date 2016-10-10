package com.titter.past3.titter.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.titter.past3.titter.MainActivity;
import com.titter.past3.titter.R;

public class TitterService extends Service {
    String message = "Connected";
    IntentFilter filter;
    Context c;
    InternetCheck in;
    int mNotificationId = 002;
    public TitterService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        c = this;
        filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        in = new InternetCheck();
        registerReceiver(in, filter);
        /*Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification.Builder builder =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(message);


        Intent resultIntent = new Intent(this, MainActivity.class);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification


        startForeground(mNotificationId,builder.getNotification());*/
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class InternetCheck extends BroadcastReceiver {
        ConnectivityManager cm;


        public InternetCheck() {

            //this.ws = ws;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(result)) {
                Log.d("InternetCheck", "started");
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    //boolean isMOBILE = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                    message = "Titter Service connected";

                }else {
                    message = "Titter Service Offline";
                }

                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                Notification.Builder builder =
                        new Notification.Builder(c)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(message);


                Intent resultIntent = new Intent(c, MainActivity.class);

                // Because clicking the notification opens a new ("special") activity, there's
                // no need to create an artificial back stack.
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                c,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                builder.setContentIntent(resultPendingIntent);

                // Sets an ID for the notification


                startForeground(mNotificationId,builder.getNotification());

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(in);
    }
}
