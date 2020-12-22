package com.example.coroutinesworkbench

import android.animation.LayoutTransition
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_socket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Socket

class SocketActivity : AppCompatActivity() {


    private val ip = "192.168.56.1"
    private val port = 80
    private lateinit var socketClient: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket)
        rootV.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        connect.setOnClickListener {

            CoroutineScope(IO).launch { connectToSocket() }
        }
        disConnect.setOnClickListener { CoroutineScope(IO).launch { disConnectSocket() } }
    }

    /**
     *
     */
    private suspend fun connectToSocket() {
        withContext(IO) {
            try {
                socketClient = Socket(ip, port)
                print("socket" + socketClient.isConnected)
                socketClient.getOutputStream().write("Android".toByteArray())


                while (socketClient.isConnected) {
                    withContext(Main){
                        status.also{
                            it.text = (" Status: Conn...")
                            it.setTextColor(resources.getColor(R.color.green))
                        }
                    }
                    val ips = socketClient.getInputStream()
                    val buff = ByteArray(ips.available())
                    var iStream = ips.read(buff)
                    while (iStream>1)
                    {

                        Log.d("data",buff.toString())
                        withContext(Main){
                            dataV.text =String(buff)
                        }
                        iStream =0

                    }
                    delay(1000)
                }
            } catch (e: Exception) {
                print(e.message)
                if(::socketClient.isInitialized&&!socketClient.isConnected)
                {
                    withContext(Main){
                        status.also{
                            it.text = (" Status: Dis Conn...")
                            it.setTextColor(resources.getColor(R.color.red))
                        }
                    }
                }
            }
        }


    }

    /**
     * A suspend function to disconnect the Socket form the Server.
     * Post Connection close there can be indication on the UI and Server's log.
     */
    private suspend fun disConnectSocket()
    {
        withContext(IO)
        {
            socketClient.close()
            withContext(Main){
                status.also{
                    it.text = (" Status: Dis Conn...")
                    it.setTextColor(resources.getColor(R.color.red))
                }
            }
        }
    }
}
