package com.yunusemrearslan.kotlinandroidserviceexamples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var mediaPlayerButton:Button?=null
    private var mapButton:Button?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaPlayerButton=findViewById<View>(R.id.mediaPlayer) as Button
        mapButton=findViewById<View>(R.id.mapButton) as Button
        mediaPlayerButton!!.setOnClickListener(this)
        mapButton!!.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if(view == mediaPlayerButton){
          val intent=Intent(applicationContext,media_player_activity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }else if(view ==mapButton){
            val intent=Intent(applicationContext,MapsActivity::class.java)
            startActivity(intent)

        }
    }
}