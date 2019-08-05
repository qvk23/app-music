package com.example.music_app.View


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.music_app.Adapter.SongListAdapter
import com.example.music_app.Contract.LoadPlaylistContract
import com.example.music_app.Model.Entity.Song
import com.example.music_app.Presenter.LoadPlaylistPresenter

import com.example.music_app.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PlayListFragment : Fragment(), LoadPlaylistContract.View {
    internal lateinit var presenter: LoadPlaylistPresenter
    internal lateinit var lvSong: ListView

    override fun showList(songs: ArrayList<Song>) {
        lvSong.adapter = SongListAdapter(activity, songs)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play_list, container, false)
        presenter = LoadPlaylistPresenter(this)
        lvSong = view.findViewById(R.id.listSong)
        activity?.let { presenter.loadPlaylist(it) }
        return view
    }

}
