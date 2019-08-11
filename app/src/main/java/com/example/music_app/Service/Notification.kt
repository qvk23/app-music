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
import android.media.session.MediaSessionManager
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.music_app.AppConstant.CHANNEL_ID
import com.example.music_app.AppConstant.NEXT_ACTION
import com.example.music_app.AppConstant.NOTIFICATION_ID
import com.example.music_app.AppConstant.PLAY_ACTION
import com.example.music_app.AppConstant.PREV_ACTION
import com.example.music_app.AppConstant.REQUEST_CODE
import com.example.music_app.MainActivity
import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.R
import com.example.music_app.View.PlayListFragment

class Notification(service: MusicPlayService) {
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var transportControl: MediaControllerCompat.TransportControls
    private val context: Context
    private val musicPlayService: MusicPlayService = service
    init {
        context = musicPlayService.baseContext
    }
//    companion object{
//        const val CHANNEL_ID = "2315"
//        const val REQUEST_CODE = 23
//        const val NOTIFICATION_ID = 23
//        const val PREV_ACTION = "prev_action"
//        const val PLAY_ACTION = "play_action"
//        const val NEXT_ACTION = "next_action"
//    }
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
//        initMediaSession(song)
        val builder = NotificationCompat.Builder(musicPlayService, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_music_video_black_24dp)
            .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
            .addAction(notificationAction(PREV_ACTION))
            .addAction(notificationAction(PLAY_ACTION))
            .addAction(notificationAction(NEXT_ACTION))
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setContentIntent(pendingIntent)

            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0,1,2))
//                .setMediaSession(mediaSessionCompat.sessionToken))
            .build()
        musicPlayService.startForeground(NOTIFICATION_ID,builder)
        return builder
    }

//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    private fun initMediaSession(song: Song) {
//        mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
//        mediaSessionCompat = MediaSessionCompat(context,"AudioPlayer")
//        transportControl = mediaSessionCompat.controller.transportControls
//        mediaSessionCompat.isActive = true
//        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
//        updateMetadata(song)
//    }

    private fun updateMetadata(song: Song) {
        mediaSessionCompat.setMetadata(MediaMetadataCompat.Builder().run {
                        putString(MediaMetadataCompat.METADATA_KEY_TITLE,song.title)
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST,song.artist)
            build()
        })
    }


    fun notificationAction(action: String): NotificationCompat.Action{
        var icon = -1
        when(action){
            PREV_ACTION -> icon = R.drawable.ic_chevron_left_black_24dp
            PLAY_ACTION -> icon = if (musicPlayService.getMediaPlayerState()) {
                R.drawable.pause_circle_outline
            }else R.drawable.play_circle_outline
            NEXT_ACTION -> icon = R.drawable.ic_chevron_right_black_24dp
        }

        return NotificationCompat.Action.Builder(icon,action,intentAction(action)).build()
    }
    private fun intentAction(action: String): PendingIntent? {
        val intentPlayer = Intent()
        intentPlayer.action = action
        Log.i("abc","$action")
        return PendingIntent.getBroadcast(musicPlayService, REQUEST_CODE,intentPlayer,PendingIntent.FLAG_UPDATE_CURRENT)
    }
}