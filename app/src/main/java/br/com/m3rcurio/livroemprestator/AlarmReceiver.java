package br.com.m3rcurio.livroemprestator;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Date;

/**
 * Created by bruno on 14/09/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    String tituloLivro;

    @Override
    public void onReceive(Context context, Intent intent) {

        tituloLivro = (String) intent.getSerializableExtra("tituloLivro");
        Log.i("ALARME", "O alarme executou as: "+new Date());
        Resources res = context.getResources();
        String nomeApp = res.getString(R.string.app_name);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_local_library_black_24dp)
                        .setContentTitle(nomeApp)
                        .setContentText("5 dias com: "+tituloLivro);
        Intent resultIntent = new Intent(context, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());

    }


}
