package com.example.music_app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.music_app.Model.Entity.Song
import com.example.music_app.R

class SongListAdapter(var context: Context?, var listMusic: ArrayList<Song>): BaseAdapter() {
    class ViewHolder(row: View){
        val textViewId: TextView
        val textViewArtist: TextView
        init {
            textViewId = row.findViewById(R.id.textView1)
            textViewArtist = row.findViewById(R.id.textView2)
        }
    }
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder
        if(p1 == null){
            val layoutinflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutinflater.inflate(R.layout.song_display, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else {
            view = p1
            viewHolder = p1.tag as ViewHolder
        }
        val song: Song = getItem(p0) as Song
        viewHolder.textViewId.text = song.title
        viewHolder.textViewArtist.text = song.artist
        return view as View
    }

    override fun getItem(p0: Int): Any {
        return listMusic.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return listMusic.size
    }
}