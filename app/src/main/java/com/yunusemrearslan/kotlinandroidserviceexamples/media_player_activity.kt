package com.yunusemrearslan.kotlinandroidserviceexamples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.yunusemrearslan.kotlinandroidserviceexamples.MediaPlayerExample.MediaPlayerService

class media_player_activity : AppCompatActivity(),View.OnClickListener {
    private var start:Button?=null
    private  var stop :Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)
        start=findViewById<View>(R.id.startButton) as Button
        stop=findViewById<View>(R.id.stopButton) as Button

        start!!.setOnClickListener(this)
        stop!!.setOnClickListener(this)
    }
    override fun onClick(view: View?) {
        if(view == start){
            startService(Intent(this, MediaPlayerService::class.java))

        }else if(view ==stop){
            stopService(Intent(this, MediaPlayerService::class.java))
        }
    }
}
