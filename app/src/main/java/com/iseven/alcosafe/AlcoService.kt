package com.iseven.alcosafe

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

var stopNotif = true
var notifState = true
var exSobreString = ""

class AlcoService: Service() {

    var timer = Timer()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                refresh()
                if (notifState == true){
                    if(globalAlco > 0.0){
                        stopNotif = false
                        if (exSobreString != sobreString()){
                            exSobreString = sobreString()
                            showNotification()
                        }
                    }else if (stopNotif == false){
                        stopNotif = true
                        showNotification()
                    }
                }
            }
        }, 0, 1000)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun showNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        val notificationId = 1
        val channelId = "alco_channel"
        val channelName = "Alcoolemie"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val disableIntent = Intent(this, DisableReceiver::class.java)
        disableIntent.putExtra("notifState", false)
        val disablePendingIntent = PendingIntent.getBroadcast(this, 1, disableIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alcoolémie" + " | " + driveString())
            .setContentText(alcoolemieToString() + " | " + sobreString())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .addAction(0 , "Désactiver", disablePendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}