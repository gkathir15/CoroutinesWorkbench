package com.example.coroutinesworkbench

import android.animation.LayoutTransition
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_socket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val imageAddress = "https://images.unsplash.com/photo-1585919737907-b6841d5c3b11?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1351&q=80"
    private val img2 = "https://images.unsplash.com/photo-1567684935919-81c216847116?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        getData.setOnClickListener {
            Log.d("COR","1fetchRemoteData")
            progressIndicator.visibility = VISIBLE
            progressIndicator.show()
         CoroutineScope(IO).launch {
             Log.d("COR","fetchRemoteData2")
             fetchRemoteData()
         }
        }

        socket.setOnClickListener { startActivity(Intent(this,SocketActivity::class.java)) }




    }





     private suspend fun fetchRemoteData(){
         Log.d("COR","fetchRemoteData")
            val input = URL(img2).readBytes()
         CoroutineScope(Main).launch {
             remoteImage.setImageBitmap(BitmapFactory.decodeByteArray(input,0,input.size))
         progressIndicator.visibility = GONE
         progressIndicator.hide()}
         Log.d("COR","byteCount ${input.size}")
    }
}
