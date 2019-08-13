package com.example.music_app.Service

import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.Presenter.MainPresenter
import java.io.IOException


class MusicPlayService : Service() {
    private val mIBinder: IBinder = LocalBinder()
    var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var isPlay: Boolean = true
    private lateinit var musicNotificationManager: Notification

    override fun onBind(intent: Intent): IBinder {
        return mIBinder
    }
    fun getNotification(): Notification? {
        return musicNotificationManager
    }



    override fun onCreate() {
        super.onCreate()
        mediaPlayer?.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        musicNotificationManager = Notification(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    inner class LocalBinder: Binder(){
        fun getService() = this@MusicPlayService
    }
    fun playMusic(idOfSong: Int?){
        prepareSong(idOfSong)
        mediaPlayer?.start()
    }

    fun pause() {
        if (mediaPlayer?.isPlaying!!){
            mediaPlayer?.pause()
            isPlay = false
        } else{
            mediaPlayer?.start()
            isPlay = true
        }
    }
    fun getState(): Boolean {
        return isPlay
    }

    fun prepareSong(idOfSong: Int?){
        mediaPlayer?.reset()
        val idOfSongPlayed = idOfSong?.toLong()
        val songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, idOfSongPlayed!!)
        try {
            mediaPlayer?.setDataSource(applicationContext, songUri)
            mediaPlayer?.prepare()
        } catch (e: IOException) {
            Log.e("MusicPlayService", "I/O error")
            mediaPlayer?.reset() // Reset again to idle state
        } catch (e: IllegalArgumentException) {
            Log.e("MusicPlayService", "Argument error")
            mediaPlayer?.reset() // Reset again to idle state
        } catch (e: NullPointerException){
            mediaPlayer?.reset()
        }
    }
    fun getPos(): Int? {
        return mediaPlayer?.currentPosition
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }
    fun getMediaPlayerState(): Boolean {
        return mediaPlayer!!.isPlaying
    }

}
