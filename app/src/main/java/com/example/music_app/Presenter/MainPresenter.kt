package com.example.music_app.Presenter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.MediaStore
import androidx.core.app.NotificationManagerCompat
import com.example.music_app.AppConstant.NEXT_ACTION
import com.example.music_app.AppConstant.NOTIFICATION_ID
import com.example.music_app.AppConstant.PLAY_ACTION
import com.example.music_app.AppConstant.PREV_ACTION
import com.example.music_app.Contract.MainContract
import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.Service.MusicPlayService

class MainPresenter(private var view: MainContract.View) : MainContract.Presenter {

    private lateinit var musicPlayservice: MusicPlayService
    private val songs = ArrayList<Song>()
    private var currentPos: Int? = 0
    private var mediaState = false

    fun register(context: Context) {
        val filter = IntentFilter()
        filter.addAction(PREV_ACTION)
        filter.addAction(PLAY_ACTION)
        filter.addAction(NEXT_ACTION)
        context.registerReceiver(ActionReceive(), filter)
    }
    private inner class ActionReceive : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action

            when (action) {
                "prev_action" -> playPrev()
                "play_action" -> {
                    pause()
                    view.updateStatusPlay()
                }
                "next_action" -> playNext()
            }

        }

    }

    override fun seekTo(position: Int) {
        musicPlayservice.seekTo(position)
    }

    fun getPos(): Int? {
        return musicPlayservice.getPos()
    }

    override fun setService(service: MusicPlayService) {
        musicPlayservice = service

    }

    override fun playSong(position: Int) {
        currentPos = position
        val nextSong = position + 1
        val songId = songs[currentPos!!].id
        val songTitle = songs[currentPos!!].title
        val songDuration = songs[currentPos!!].duration
        musicPlayservice.playMusic(songId)
        notification()
        view.showPlayer(songTitle, songDuration)
        musicPlayservice.mediaPlayer!!.setOnCompletionListener {
            if (currentPos == songs.size - 1) {

                if (currentPos == songs.size - 1) {

                    stopPlaying()
                } else {
                    playSong(position + 1)
                }
                playSong(nextSong)

            }

        }
    }

    private fun stopPlaying() {
        musicPlayservice.mediaPlayer!!.stop()
    }

    override fun loadPlaylist(context: Context) {
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val songTitle: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songId: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            do {
                val currentTitle: String = cursor.getString(songTitle)
                val currentArtist: String = cursor.getString(songArtist)
                val currentId: Int = cursor.getInt(songId)
                val currentDuration = cursor.getInt(songDuration)
                songs.add(Song(currentId, currentTitle, currentArtist, currentDuration))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        view.showList(songs)
    }

    override fun playNext() {
        if (currentPos == songs.size - 1) {
            currentPos = 0
        } else {
            currentPos = currentPos?.plus(1)
        }
        playSong(currentPos!!)
    }

    override fun playPrev() {
        if (currentPos == 0) {
            currentPos = songs.size - 1
        } else {
            currentPos = currentPos?.minus(1)
        }
        playSong(currentPos!!)
    }

    override fun pause() {
        musicPlayservice.pause()
        mediaState = musicPlayservice.getState()
        musicPlayservice.getNotification()!!.createNotification(songs[currentPos!!])
    }

    fun getState(): Boolean {
        return mediaState
    }

    fun notification() {
        val notification = musicPlayservice.getNotification()!!.createNotification(songs[currentPos!!])
        with(NotificationManagerCompat.from(musicPlayservice)) {
            notify(NOTIFICATION_ID, notification)
        }
    }
}
