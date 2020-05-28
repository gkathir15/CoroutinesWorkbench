package com.example.coroutinesworkbench

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutinesworkbench.models.Repos
import com.example.coroutinesworkbench.models.ReposItem
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_github_list.*
import kotlinx.android.synthetic.main.activity_github_list.getData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.repos.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

class GithubListActivity : AppCompatActivity() {
    val url = "https://api.github.com/repositories"

    /**
     * Simple implementation of Caching via storing bitmaps for recyclerView Items, So only once its being loaded its Reused.
     */
    lateinit var cacheMap: ConcurrentHashMap<String, Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_list)
        cacheMap = ConcurrentHashMap()

        getData.setOnClickListener { CoroutineScope(IO).launch { fetchRemoteData() } }
    }

    /**
     * Fetches Data from Github API Via HttpUrlConnection object.
     * Kotlin keyword suspend mentions that this is a coroutine.
     * The callig of the coRoutne is being called inside a coroutine scope of  IO scope.
     */
    private suspend fun fetchRemoteData() {

        val http = URL(url).openConnection() as HttpURLConnection
        Log.d("resp", " " + http.responseCode + " " + http.responseMessage)
        Log.d("resp", " " + http.inputStream.available())

        if (http.responseCode == 200) {
            val inputStream: InputStream = if (http.responseCode in 200..299) {
                http.inputStream
            } else {
                http.errorStream
            }

            val `in` = BufferedReader(
                InputStreamReader(
                    inputStream
                )
            )

            val response = StringBuilder()
            var currentLine: String?

            while (`in`.readLine()
                    .also { currentLine = it } != null
            ) response.append(currentLine)

            `in`.close()

            CoroutineScope(Default).launch {
                populateAdapter(serializeRepo(response.toString()))
            }
            Log.d("resp", " $response")
        }
    }

    /**
     * after the response is read as a string, We serilize it via our Kotlin data classes ie. Pojos.
     * this can be run on main thread ,But we are running inside  Coroutine scope - Main.
     * returns Repos.
     */
    private suspend fun serializeRepo(resp: String): Repos {

        val gson = Gson()
        return gson.fromJson(resp, Repos::class.java) as Repos

    }

    /**
     * Method being run on main thread but being called from IO thread.
     * with Main Coroutine scope, make it callable without exception from any thread.
     *
     */
    private fun populateAdapter(repos: Repos) {
        CoroutineScope(Main).launch {
            recyclerView.layoutManager = LinearLayoutManager(this@GithubListActivity)
            recyclerView.adapter = RepoAdapter(repos)
            getData.visibility = GONE

        }
    }


    inner class RepoAdapter(val list: List<ReposItem>) :
        RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {
        inner class RepoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val repoTV = view.name
            val ownerTV = view.owner
            val imgIV = view.img
        }


        override fun getItemCount(): Int {
            return list.size
        }


        override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
            holder.repoTV.text = list[position].fullName
            holder.ownerTV.text = list[position].owner.login

                if (cacheMap.containsKey(list[position].owner.avatarUrl)) {
                        holder.imgIV.setImageBitmap(
                                cacheMap[list[position].owner.avatarUrl]
                        )

                } else {
                    CoroutineScope(IO).launch {
                    val input = URL(list[position].owner.avatarUrl).readBytes()
                    cacheMap[list[position].owner.avatarUrl] = BitmapFactory.decodeByteArray(
                        input,
                        0,
                        input.size
                    )
                    CoroutineScope(Main).launch {
                        holder.imgIV.setImageBitmap(
                            BitmapFactory.decodeByteArray(
                                input,
                                0,
                                input.size
                            )
                        )
                    }

                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
            return RepoViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.repos, parent, false)
            )
        }
    }
}
