package com.example.vartalap.firebase


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.vartalap.R
import com.example.vartalap.models.User
import com.example.vartalap.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val a = token
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val user = User(
            remoteMessage.data[Constants.KEY_USER_ID] ?: "",
            remoteMessage.data[Constants.KEY_NAME] ?: "",
            "", "",
            remoteMessage.data[Constants.KEY_FCM_TOKEN] ?: "",
        )

        val notificationId = Random().nextInt()
        val channelId = "chat_message"

        val args = Bundle()
        args.putParcelable("user", user)

        val pendingIntent = NavDeepLinkBuilder(application)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.chatFragment)
            .setArguments(args)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(user.name)
            .setContentText(remoteMessage.data[Constants.KEY_MESSAGE])
            .setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage.data[Constants.KEY_MESSAGE]))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Chat Message"
            val channelDescription = "This notification channel is used for chat message notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.description = channelDescription

            getSystemService(NotificationManager::class.java).createNotificationChannel(notificationChannel)
        }

        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
    }

}