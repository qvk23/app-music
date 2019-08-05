package com.example.music_app.Contract

import android.content.Context
import com.example.music_app.Model.Entity.Song

interface LoadPlaylistContract {
    interface View {
        fun showList(songs: ArrayList<Song>)
    }
    interface Presenter{
        fun loadPlaylist(context: Context)
    }
}