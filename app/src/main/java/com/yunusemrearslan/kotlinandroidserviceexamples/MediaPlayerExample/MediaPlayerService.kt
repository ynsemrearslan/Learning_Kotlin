package com.yunusemrearslan.kotlinandroidserviceexamples.MediaPlayerExample

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings

class MediaPlayerService :Service() {
    // declaring object of MediaPlayer
    private lateinit var player:MediaPlayer
    //execution of service will start
    // on calling this method
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // creating a media player which
        //will play the audio of Default
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
        // providing the boolean
        // value as true to play
        // the audio on loop
        player.start()
         return START_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}