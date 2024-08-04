package com.engineer.fred.audioplayer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.engineer.fred.audioplayer.databinding.ActivityFavoritesBinding
import com.engineer.fred.audioplayer.models.Audio
import com.google.android.material.snackbar.Snackbar
import java.io.File

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var favoriteAdapter: FavoriteAdapter

    companion object {
        var favMusicList: ArrayList<Audio> = ArrayList()
        var favouritesChanged: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme( MainActivity.currentTheme[ MainActivity.themeIndex ] )
        binding = ActivityFavoritesBinding.inflate( layoutInflater )
        setContentView( binding.root )
        supportActionBar?.hide()

        initialiseFALayout()

    }

    private fun initialiseFALayout() {
        if ( MainActivity.themeIndex == 5 ) binding.root.setBackgroundColor( ContextCompat.getColor(  this, R.color.special) )
        setUpFARecyclerView()
        registerFAClickEvents()
    }

    private fun setUpFARecyclerView() {
        binding.apply {
            favRV.setHasFixedSize( true )
            favRV.setItemViewCacheSize( 13 )
            favRV.layoutManager = LinearLayoutManager( this@FavoritesActivity )
            favoriteAdapter = FavoriteAdapter( this@FavoritesActivity, favMusicList )
            if ( favoriteAdapter.itemCount == 0 ) binding.textInfo.visibility = View.VISIBLE
            favRV.adapter = favoriteAdapter
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun registerFAClickEvents() {
        binding.apply {
            backArrow.setOnClickListener { finish() }
            if (favMusicList.size <= 1) binding.floatingBtnFA.visibility = View.GONE
            else {
                binding.floatingBtnFA.visibility = View.VISIBLE
                binding.floatingBtnFA.setOnClickListener {
                    val intent = Intent(baseContext, PlayerActivity::class.java)
                    intent.putExtra("class", "FavoriteActivityShuffle")
                    startActivity( intent  )
                    Snackbar.make( it, "Shuffle mode enabled!", Snackbar.LENGTH_SHORT ).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            if ( favMusicList.isNotEmpty()) {
                favMusicList.forEachIndexed { index, audio ->
                    val file = File(audio.path)
                    if (!file.exists()) favMusicList.removeAt(index)
                }
            }
        } catch (e: ConcurrentModificationException) {
            Log.e("MyTag", e.message.toString())
        } catch (e: Exception) {
            Log.e("MyTag", e.message.toString())
        }
        if(favouritesChanged) {
            favoriteAdapter.updateFavourites( favMusicList )
            favouritesChanged = false
        }
        if(  favMusicList.size < 1 ) binding.floatingBtnFA.visibility = View.GONE
        favoriteAdapter.updateFavourites( favMusicList )
    }
}