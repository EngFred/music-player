package com.engineer.fred.audioplayer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.engineer.fred.audioplayer.databinding.ActivityPlaylistDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetail : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailBinding
    private lateinit var adapter: AudioAdapter

    companion object {
        var currentPlayListPos = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme( MainActivity.currentTheme[ MainActivity.themeIndex ] )
        binding = ActivityPlaylistDetailBinding.inflate( layoutInflater )
        setContentView( binding.root )
        supportActionBar?.hide()

        initializeDetailLayout()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeDetailLayout() {
        when(  MainActivity.themeIndex ) {
            0 ->  {
                binding.playlistDetailHeader.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_default )
                binding.opts.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_default )
                binding.playlistTitleHead.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_default )
                binding.playerImageView.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_default )
            }
            1 -> {
                binding.playlistDetailHeader.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_green)
                binding.opts.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_green )
                binding.playlistTitleHead.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_green )
                binding.playerImageView.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_green)
            }
            2 -> {
                binding.playlistDetailHeader.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_blue)
                binding.opts.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_blue )
                binding.playlistTitleHead.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_blue )
                binding.playerImageView.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_blue )
            }
            3 -> {
                binding.playlistDetailHeader.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_orange)
                binding.opts.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_orange)
                binding.playlistTitleHead.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_orange )
                binding.playerImageView.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_orange )
            }
            4 -> {
                binding.playlistDetailHeader.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_pink)
                binding.opts.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_pink )
                binding.playlistTitleHead.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_pink)
                binding.playerImageView.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_pink )
            }
            5 -> {
                binding.playlistDetailHeader.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_dark)
                binding.opts.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_dark )
                binding.playlistTitleHead.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_dark)
                binding.root.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_dark)
                binding.playerImageView.background = AppCompatResources.getDrawable( baseContext, R.drawable.gradient_dark )
            }

        }
        currentPlayListPos = intent.extras?.getInt("index") as Int
        var playlistSongs  = PlaylistListActivity.playlistList.ref[ currentPlayListPos ].playlistSongs
        playlistSongs = checkFileExists( playlistSongs )
        adapter = AudioAdapter( this, playlistSongs, true )
        setUpRecyclerView()
        binding.backArrow.setOnClickListener { finish() }
        binding.floatingBtn.setOnClickListener {
            makeToast( "Playlist Shuffle mode enabled!" )
            val intent = Intent( this, PlayerActivity::class.java )
            intent.putExtra("songPos", 0 )
            intent.putExtra("class", "PlaylistDetailShuffle")
            startActivity( intent )
        }
        binding.addSongsBtn.setOnClickListener {
            val intent = Intent( this, SelectionActivity::class.java )
            for( song in MainActivity.musicList ) {
                if ( song in playlistSongs ) {

                }
            }
            startActivity( intent )
        }
        binding.removeBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle( "DELETE!")
                .setMessage("Are you sure you want to remove all these songs in ${ PlaylistListActivity.playlistList.ref[currentPlayListPos].name}?")
                .setPositiveButton("YES") { _, _ ->
                    PlaylistListActivity.playlistList.ref[currentPlayListPos].playlistSongs.clear()
                    binding.floatingBtn.visibility = View.GONE
                    binding.playlistTotalSongs.text = "0 songs"
                    binding.removeBtn.isEnabled = false
                    adapter.refreshPlaylist()
                    Toast.makeText(this, "songs removed!", Toast.LENGTH_LONG).show()
                }.setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    private fun setUpRecyclerView() {
        binding.apply {
            playlistRV.setHasFixedSize( true )
            playlistRV.setItemViewCacheSize( 10 )
            playlistRV.layoutManager = LinearLayoutManager( this@PlaylistDetail )
            playlistRV.adapter = adapter
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val playlist  = PlaylistListActivity.playlistList.ref[ currentPlayListPos ]
        binding.playlistName.text = playlist.name
        binding.creatorName.text = "@${playlist.createdBy}"
        binding.playlistDateOfCreation.text = "Created on: ${playlist.createdOn}"
        if(  adapter.itemCount == 1 ) binding.playlistTotalSongs.text =  "${playlist.playlistSongs.size} song" else binding.playlistTotalSongs.text =  "${playlist.playlistSongs.size} songs"
        if (  playlist.playlistSongs.size > 0 ) {
            Glide.with( applicationContext ).load(  playlist.playlistSongs[0].artUri  ).placeholder( R.drawable.launch_icon ) .centerCrop().into( binding.playerImageView )
            binding.floatingBtn.visibility = View.VISIBLE
        } else  binding.floatingBtn.visibility = View.GONE
        binding.removeBtn.isEnabled = true
        adapter.refreshPlaylist()
        //store Playlist list data in shared preferences using gson
        val jsonStringPlaylist = GsonBuilder().create().toJson(  PlaylistListActivity.playlistList  )
        getSharedPreferences("FAVORITES", MODE_PRIVATE ).edit().putString( "PlaylistList", jsonStringPlaylist ).apply()
    }
}