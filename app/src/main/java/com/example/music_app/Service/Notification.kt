package com.example.music_app.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.music_app.AppConstant.CHANNEL_ID
import com.example.music_app.AppConstant.MEDIA_STATE
import com.example.music_app.AppConstant.NEXT_ACTION
import com.example.music_app.AppConstant.NOTIFICATION_ID
import com.example.music_app.AppConstant.PLAY_ACTION
import com.example.music_app.AppConstant.PREV_ACTION
import com.example.music_app.AppConstant.REQUEST_CODE
import com.example.music_app.MainActivity
import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.R

class Notification(service: MusicPlayService) {
    private lateinit var notificationManager: NotificationManager
    private val context: Context
    private val musicPlayService: MusicPlayService = service
    init {
        context = musicPlayService.baseContext
    }

    private fun createNotificationChanel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val name = Resources.getSystem().getString(R.string.channel_name)
            val descriptionText = Resources.getSystem().getString(R.string.channel_description)
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager =
                musicPlayService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(song: Song): Notification {
        createNotificationChanel()
        val openPlayerIntent = Intent(musicPlayService, MainActivity::class.java)
        openPlayerIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivities(musicPlayService, 0, arrayOf(openPlayerIntent),0)
        val builder = NotificationCompat.Builder(musicPlayService, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_music_video_black_24dp)
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_close_black_24dp))
            .setColor(Color.GREEN)
            .addAction(notificationAction(PREV_ACTION))
            .addAction(notificationAction(PLAY_ACTION))
            .addAction(notificationAction(NEXT_ACTION))
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setContentIntent(pendingIntent)
            .setShowWhen(false)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0,1,2))
            .build()
        musicPlayService.startForeground(NOTIFICATION_ID,builder)
        return builder
    }

    fun notificationAction(action: String): NotificationCompat.Action{
        var icon = -1
        Log.i("abc", "${musicPlayService.getMediaPlayerState()}")
        when(action){
            PREV_ACTION -> icon = R.drawable.ic_chevron_left_black_24dp
            PLAY_ACTION -> icon = if (musicPlayService.getState() != MEDIA_STATE) {
                R.drawable.pause_circle_outline
            }else R.drawable.play_circle_outline
            NEXT_ACTION -> icon = R.drawable.ic_chevron_right_black_24dp
        }

        return NotificationCompat.Action.Builder(icon,action,intentAction(action)).build()
    }
    private fun intentAction(action: String): PendingIntent? {
        val intentPlayer = Intent()
        intentPlayer.action = action
        return PendingIntent.getBroadcast(musicPlayService, REQUEST_CODE,intentPlayer,PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
