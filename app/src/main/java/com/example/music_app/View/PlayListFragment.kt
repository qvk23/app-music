package com.example.music_app.View


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music_app.Adapter.OnItemClickListener
import com.example.music_app.Adapter.SongListAdapter
import com.example.music_app.Adapter.addOnItemClickListener
import com.example.music_app.Contract.MainContract
import com.example.music_app.Model.DateTimeFormatUtils
import com.example.music_app.Model.Entity.Entity.Song
import com.example.music_app.Presenter.MainPresenter
import com.example.music_app.R
import com.example.music_app.Service.MusicPlayService

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PlayListFragment : Fragment(), MainContract.View, View.OnClickListener, Runnable {
    override fun run() {
        updateSeekbar()
    }

    fun onClickSeekbar() {
        seekbarSongDuration.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            var positionSelected = 0
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2){
                    positionSelected = p1
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                userisSeeking = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                userisSeeking = false
                presenter.seekTo(positionSelected*1000)
            }

        })
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.play_next -> presenter.playNext()
            R.id.play_prev -> presenter.playPrev()
            R.id.button_play -> {
                var isPlay = presenter.pause()
                if(isPlay){
                    btnPlay.setImageResource(R.drawable.pause)
                }else {
                    btnPlay.setImageResource(R.drawable.play)
                }
            }
        }
    }


    private lateinit var presenter: MainPresenter
    private lateinit var lvSong: RecyclerView
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var btnPlay: ImageButton
    private lateinit var songTitle: TextView
    private lateinit var seekbarSongDuration: SeekBar
    private lateinit var tvSongDuration: TextView
    private var musicPlayService: MusicPlayService? = null
    private var serviceIntent: Intent? = null
    private var userisSeeking = false




    override fun selectSong(){
        lvSong.addOnItemClickListener(object: OnItemClickListener{
            override fun onItemClicked(position: Int, view: View) = presenter.playSong(position)
        })

    }






    override fun showList(songs: ArrayList<Song>) {
        lvSong.layoutManager = LinearLayoutManager(activity)
        lvSong.adapter = SongListAdapter(activity, songs)
    }
    private val mConnection: ServiceConnection = object: ServiceConnection{
        override fun onServiceDisconnected(p0: ComponentName?) {

        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.i("click service"," onServiceConnected")
            musicPlayService = (p1 as MusicPlayService.LocalBinder).getService()
            presenter.setService(musicPlayService!!)
        }

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play_list, container, false)
        presenter = MainPresenter(this)
        lvSong = view.findViewById(R.id.listSong)
        btnNext = view.findViewById(R.id.play_next)
        btnPrev = view.findViewById(R.id.play_prev)
        btnPlay = view.findViewById(R.id.button_play)
        songTitle = view.findViewById(R.id.textviewSongTitle)
        tvSongDuration = view.findViewById(R.id.tv_song_duration)
        seekbarSongDuration = view.findViewById(R.id.seek_bar_song)
        seekbarSongDuration.isEnabled = false
        btnNext.setOnClickListener(this)
        btnPrev.setOnClickListener(this)
        btnPlay.setOnClickListener(this)

        activity?.let { presenter.loadPlaylist(it) }

        return view
    }
    override fun showPlayer(title: String,duration: Int){
        songTitle.text = title
        tvSongDuration.text = DateTimeFormatUtils().formatDuration(duration.toLong())
        tvSongDuration.text
        btnPlay.setImageResource(R.drawable.pause)
        seekbarSongDuration.isEnabled = true
        seekbarSongDuration.max = duration/1000
        updateSeekbar()
        onClickSeekbar()
    }

    private fun updateSeekbar() {
        seekbarSongDuration.setProgress(presenter.getPos()!!/1000)
        seekbarSongDuration.postDelayed(this,1000)

    }

    override fun onStart() {
        super.onStart()

        if (serviceIntent == null) {
            serviceIntent = Intent(activity, MusicPlayService::class.java)
            activity?.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE)
            activity?.startService(serviceIntent)
        }
        selectSong()

    }
}
