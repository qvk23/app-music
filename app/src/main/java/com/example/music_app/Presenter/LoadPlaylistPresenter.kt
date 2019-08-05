package com.example.music_app.Presenter

import android.content.Context
import android.provider.MediaStore
import com.example.music_app.Contract.LoadPlaylistContract
import com.example.music_app.Model.Entity.Song

class LoadPlaylistPresenter(view: LoadPlaylistContract.View): LoadPlaylistContract.Presenter {
    private var view: LoadPlaylistContract.View? = view
    val songs = ArrayList<Song>()
    override fun loadPlaylist(context: Context) {
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )
        var cursor = resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null)
        if(cursor != null && cursor.moveToFirst()){
            val songTitle: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songId: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            do{
                var currentTitle: String = cursor.getString(songTitle)
                var currentArtist: String = cursor.getString(songArtist)
                var currentId: Int = cursor.getInt(songId)
                songs.add(Song(currentId, currentTitle, currentArtist))
            }while(cursor.moveToNext())
        }
        view?.showList(songs)
    }

}