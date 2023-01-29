package com.iseven.alcosafe

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log

class DisableReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "hhhh okkkk")
        notifState = intent.getBooleanExtra("notifState", true)
        sharedEditor?.putBoolean("notifState", intent.getBooleanExtra("notifState", true))
        sharedEditor?.commit()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }
}