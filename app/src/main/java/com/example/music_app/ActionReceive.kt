package com.example.music_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.music_app.Contract.MainContract
import com.example.music_app.Presenter.MainPresenter

class ActionReceive: BroadcastReceiver() {
    private lateinit var presenter: MainPresenter
    init {
        presenter = presenter.getPresenter()
    }
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action
        if(AppConstant.PREV_ACTION.equals(action)){
            Log.i("abc","$action")
            presenter.playPrev()
        } else if(AppConstant.PLAY_ACTION.equals(action)){
            Log.i("abc","$action")
//            pause()
        } else if(AppConstant.NEXT_ACTION.equals(action)){
            Log.i("abc","$action")
//            playNext()
        }
    }
}