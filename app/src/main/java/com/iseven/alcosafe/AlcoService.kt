package com.iseven.alcosafe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*

var stopNotif = true

class AlcoService: Service() {

    var timer = Timer()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                refresh()
                if(globalAlco > 0.0){
                    stopNotif = false
                    showNotification()
                }else if (stopNotif == false){
                    stopNotif = true
                    showNotification()
                }
            }
        }, 0, 1000)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "alco_channel"
        val channelName = "Alcoolemie"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alcoolémie" + " | " + driveString())
            .setContentText(alcoolemieToString() + " | " + sobreString())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
