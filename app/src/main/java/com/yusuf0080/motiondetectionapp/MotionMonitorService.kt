package com.yusuf0080.motiondetectionapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MotionMonitorService : Service() {

    private val CHANNEL_ID = "MotionAlertChannel"
    private var isFirstLoad = true

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        startForeground(1, createRunningNotification())

        listenToFirebase()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun listenToFirebase() {
        val database = FirebaseDatabase.getInstance()
        val motionRef = database.getReference("door1/motion")

        motionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isMotion = snapshot.getValue(Boolean::class.java) ?: false

                if (isMotion && !isFirstLoad) {
                    showAlarmNotification()
                }
                isFirstLoad = false
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun createRunningNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Smart Door Monitor Aktif")
            .setContentText("Memantau keamanan rumah di latar belakang...")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun showAlarmNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("⚠️ BAHAYA: GERAKAN TERDETEKSI!")
            .setContentText("Seseorang ada di depan pintu! Ketuk untuk melihat kamera.")
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Motion Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel untuk notifikasi deteksi gerakan"
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}