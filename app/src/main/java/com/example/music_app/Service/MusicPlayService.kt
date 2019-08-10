package com.example.music_app.Service

import android.app.NotificationManager
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.Presenter.MainPresenter
import java.io.IOException


class MusicPlayService() : Service() {
    private val mIBinder: IBinder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlay: Boolean = true
    private var musicNotificationManager: MusicNotification? = null
    private lateinit var presenter: MainPresenter


    override fun onBind(intent: Intent): IBinder {
        musicNotificationManager = MusicNotification(this)
        return mIBinder
    }
    fun getMusicNotification(): MusicNotification? {
        return musicNotificationManager
    }
    fun getSong(): Song {
        return presenter.getSong()
    }
    override fun onCreate() {
        super.onCreate()
        presenter.getPresenter()
        val mp = MediaPlayer()
        mp.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        this.mediaPlayer = mp

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
    inner class LocalBinder: Binder(){
        fun getService() = this@MusicPlayService
    }
    fun playMusic(idOfSong: Int?){
        prepareSong(idOfSong)
        mediaPlayer?.start()
    }

    fun pause(): Boolean {
        if (mediaPlayer?.isPlaying!!){
            mediaPlayer?.pause()
            isPlay = false
        } else{
            mediaPlayer?.start()
            isPlay = true
        }
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
        }
    }
    fun getPos(): Int? {
        return mediaPlayer?.currentPosition
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }
    fun getMediaPlayerState(): Boolean {
        if(mediaPlayer!!.isPlaying){
            return true
        }else{
            return false
        }
    }
}
