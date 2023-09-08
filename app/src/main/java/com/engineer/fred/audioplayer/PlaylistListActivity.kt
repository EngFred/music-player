package com.engineer.fred.audioplayer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.engineer.fred.audioplayer.databinding.ActivityPlaylistListBinding
import com.engineer.fred.audioplayer.databinding.AddPlayListDialogBinding
import com.engineer.fred.audioplayer.models.Audio
import com.engineer.fred.audioplayer.models.Playlist
import com.engineer.fred.audioplayer.models.PlaylistList
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlaylistListActivity : AppCompatActivity(), PlaylistListAdapter.AdapterClickListener {

    private lateinit var binding: ActivityPlaylistListBinding
    private lateinit var playlistListAdapter: PlaylistListAdapter

    companion object {
        var playlistList: PlaylistList = PlaylistList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme( MainActivity.currentTheme[ MainActivity.themeIndex ] )
        binding = ActivityPlaylistListBinding.inflate( layoutInflater )
        setContentView( binding.root )
        supportActionBar?.hide()

        initialiseLayout()

    }

    private fun initialiseLayout() {
        if ( MainActivity.themeIndex == 5 ) binding.root.setBackgroundColor( ContextCompat.getColor( this, R.color.special ) )
        playlistListAdapter = PlaylistListAdapter( applicationContext, playlistList.ref )
        playlistListAdapter.setUpClickListener( this )
        setUpRecyclerView()
        registerEvents()
    }

    private fun setUpRecyclerView() {
        binding.apply {
            playlistListRV.setHasFixedSize( true )
            playlistListRV.setItemViewCacheSize( 13 )
            playlistListRV.layoutManager = GridLayoutManager( this@PlaylistListActivity, 2 )
            playlistListRV.adapter = playlistListAdapter
        }
    }

    private fun registerEvents() {
        binding.apply {
            backArrow.setOnClickListener { finish() }
            floatingBtn.setOnClickListener { createAlertDialog() }
        }
    }

    private fun createAlertDialog() {
        val view = LayoutInflater.from( this ).inflate( R.layout.add_play_list_dialog, binding.root, false  )
        val binder = AddPlayListDialogBinding.bind( view )
        MaterialAlertDialogBuilder( this )
            .setTitle("Playlist Details")
            .setView( view )
            .setPositiveButton("ADD"){ _, _  ->
                val playlistName = binder.playlistNameEt.text.toString()
                val playlistCreator = binder.usernameEt.text.toString()

                if ( playlistName.isEmpty() || playlistCreator.isEmpty()  ) {
                    Toast.makeText(this, "Provide both fields", Toast.LENGTH_LONG).show()
                } else {
                    addPlaylist( playlistName, playlistCreator )
                }
            }.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addPlaylist(playlistName: String, playlistCreator: String ) {
        var playlistExists = false
        for ( playlist in playlistList.ref  ) {
            if (  playlistName.contains( playlist.name )  ) {
                playlistExists = true
                break
            }
        }
        if ( playlistExists  ) Toast.makeText(this, "Playlist already exists!", Toast.LENGTH_LONG).show()
        else {
            val listOfAudios: ArrayList<Audio> = ArrayList()
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd, MMM, yyyy", Locale.ENGLISH )

            val myPlaylist = Playlist(  playlistName, listOfAudios, playlistCreator, sdf.format( calendar ) )
            playlistList.ref.add( myPlaylist )
            playlistListAdapter.notifyDataSetChanged()
            Toast.makeText( this, "Playlist created!", Toast.LENGTH_LONG ).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun deletePlayList(playlist: Playlist ) {
        MaterialAlertDialogBuilder( this )
            .setTitle("Delete ${playlist.name}")
            .setMessage("Are you sure you want to deleted this Playlist?")
            .setPositiveButton( "YES" ){ _,_  ->
                playlistList.ref.remove( playlist )
                playlistListAdapter.notifyDataSetChanged()
                Toast.makeText( this, "Playlist deleted!", Toast.LENGTH_LONG ).show()
            }.setNegativeButton("NO"){ dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun showPlayListDetail(playlist: Playlist, playlistPos: Int) {
        val intent = Intent( this, PlaylistDetail::class.java )
        intent.putExtra("index", playlistPos )
        startActivity( intent )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        playlistListAdapter.notifyDataSetChanged()
    }

}