package com.iseven.alcosafe

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DisableReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        notifState = intent.getBooleanExtra("notifState", true)
        sharedEditor?.putBoolean("notifState", intent.getBooleanExtra("notifState", true))
        sharedEditor?.commit()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }
}