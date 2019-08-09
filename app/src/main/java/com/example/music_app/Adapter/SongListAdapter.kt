package com.example.music_app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.R
import kotlinx.android.synthetic.main.song_display.view.*


class SongListAdapter(var context: Context?, var listMusic: ArrayList<Song>):
    RecyclerView.Adapter<SongListAdapter.MyViewHolder>() {


    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvTitle  = view.textView1
        val tvArtists = view.textView2

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListAdapter.MyViewHolder {
        val view: View?
        val layoutInflater = LayoutInflater.from(context)
        view = layoutInflater.inflate(R.layout.song_display,null)
        return MyViewHolder(view)
    }

    override fun getItemCount() = listMusic.size

    override fun onBindViewHolder(holder: SongListAdapter.MyViewHolder, position: Int) {
        val song: Song = listMusic.get(position)
        holder.tvTitle.text = song.title
        holder.tvArtists.text = song.artist


    }
}