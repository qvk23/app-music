package com.example.music_app.Service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.session.MediaSessionManager
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.R
import com.example.music_app.View.PlayListFragment


class MusicNotification(service: MusicPlayService) {

    private val musicPlayService: MusicPlayService = service
    private val context: Context
    private lateinit var transportControl: MediaControllerCompat.TransportControls
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaSessionManager: MediaSessionManager
    companion object{
        const val CHANNEL_ID = "2315"
        const val PREV_ACTION = "prev_action"
        const val PLAY_ACTION = "play_action"
        const val NEXT_ACTION = "next_action"
        const val REQUEST_CODE = 315
        const val NOTIFICATION_ID = 23
    }
    init {
        context = musicPlayService.baseContext
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initMediaSession(song: Song){
        mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSessionCompat = MediaSessionCompat(context,"AudioPlayer")
        transportControl = mediaSessionCompat.controller.transportControls
        mediaSessionCompat.isActive = true
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        updateMetadata(song)

    }
    fun getNotification(): NotificationManager {
        return notificationManager
    }

    private fun updateMetadata(song: Song) {
        mediaSessionCompat.setMetadata(MediaMetadataCompat.Builder().run {
            putString(MediaMetadataCompat.METADATA_KEY_TITLE,song.title)
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST,song.artist)
            build()
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
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
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun createNotification(): Notification? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel()
        }
        val song = musicPlayService.getSong()

        initMediaSession(song)

        val openPlayerIntent = Intent(musicPlayService,PlayListFragment::class.java)
        openPlayerIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivities(musicPlayService, REQUEST_CODE, arrayOf(openPlayerIntent),0)
        return NotificationCompat.Builder(musicPlayService, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.music_circle_outline)
            .addAction(notificationAction(PREV_ACTION))
            .addAction(notificationAction(PLAY_ACTION))
            .addAction(notificationAction(NEXT_ACTION))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1 /* #1: pause button \*/)
                .setMediaSession(mediaSessionCompat.sessionToken))
            .setContentTitle("Music")
            .setContentText("Artis")
            .setContentIntent(pendingIntent)
            .build()
    }

    fun notificationAction(action: String): NotificationCompat.Action{
        var icon = -1
        when(action){
            PREV_ACTION -> icon = R.drawable.chevron_left_circle_outline
            PLAY_ACTION -> icon = if (musicPlayService.getMediaPlayerState()) {
                R.drawable.pause_circle_outline
            }else R.drawable.play_circle_outline
            NEXT_ACTION -> icon = R.drawable.chevron_right_circle_outline
        }

        return NotificationCompat.Action.Builder(icon,action,intentAction(action)).build()
    }

    private fun intentAction(action: String): PendingIntent? {
        val intentPlayer = Intent()
        intentPlayer.action = action
        return PendingIntent.getBroadcast(musicPlayService, REQUEST_CODE,intentPlayer,PendingIntent.FLAG_UPDATE_CURRENT)
    }

}


