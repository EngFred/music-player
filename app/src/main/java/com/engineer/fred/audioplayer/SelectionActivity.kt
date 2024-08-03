package com.engineer.fred.audioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.engineer.fred.audioplayer.databinding.ActivitySelectionBinding
import com.engineer.fred.audioplayer.databinding.MusicItemBinding

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: AudioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme( MainActivity.currentTheme[ MainActivity.themeIndex ] )
        binding = ActivitySelectionBinding.inflate( layoutInflater )
        setContentView( binding.root )
        supportActionBar?.hide()

        initializeLayout()
    }

    private fun initializeLayout() {
        if ( MainActivity.themeIndex == 5 ) binding.root.setBackgroundColor( ContextCompat.getColor( this, R.color.dark ))
        adapter = AudioAdapter(  this, MainActivity.musicList, selectionActivity = true )
        setUpRecyclerView()
        registerClickEvents()
    }

    private fun registerClickEvents() {
        binding.searchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val userInput = it.lowercase()
                    MainActivity.searchMusicList = ArrayList()
                    for (  song in MainActivity.musicList)
                        if (song.title?.lowercase()?.contains( userInput ) == true)
                            MainActivity.searchMusicList.add( song )
                    MainActivity.isSearching = true
                    adapter.updateMusicList( MainActivity.searchMusicList )
                }
                return true
            }
        })
        binding.backArrow.setOnClickListener { finish() }
    }

    private fun setUpRecyclerView() {
        binding.apply {
            selectionRV .setHasFixedSize( true )
            selectionRV.setItemViewCacheSize( 13 )
            selectionRV.layoutManager = LinearLayoutManager( this@SelectionActivity)
            selectionRV.adapter = adapter
        }
    }
}