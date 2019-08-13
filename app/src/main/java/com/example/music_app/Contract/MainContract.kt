package com.example.music_app.Contract

import android.content.Context
import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.Service.MusicPlayService


interface MainContract {
    interface View {
        fun showList(songs: ArrayList<Song>)
        fun selectSong()
        fun showPlayer(title: String,duration: Int)
        fun updateStatusPlay()


    }
    interface Presenter{
        fun loadPlaylist(context: Context)
        fun setService(service: MusicPlayService)
        fun playSong(position: Int)
        fun playNext()
        fun pause()
        fun playPrev()
        fun seekTo(position: Int)
    }
}