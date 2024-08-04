package com.engineer.fred.audioplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.engineer.fred.audioplayer.databinding.FavAudioBinding
import com.engineer.fred.audioplayer.models.Audio
import java.io.File

class FavoriteAdapter( private val context: Context, private var favList: ArrayList<Audio>) : Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder( private val binding: FavAudioBinding ) : ViewHolder( binding.root ) {
        fun bind( audio: Audio) {
            binding.apply {
                songNameFA.text = audio.title
                songAlbumFA.text = audio.album
                songDurationFA.text = formatDuration( audio.duration )
                Glide.with(itemView)
                    .load(getImageArt(audio.path))
                    .fitCenter().placeholder(R.drawable.launch_icon).into(imageMusicViewFA)
                //Glide.with( itemView ).load( audio.artUri ).placeholder( R.drawable.launch_icon ).into( imageMusicViewFA  )
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FavAudioBinding.inflate(  LayoutInflater.from( parent.context ), parent, false  )
        if ( MainActivity.themeIndex == 5 ) {
            binding.songNameFA.setTextColor( ContextCompat.getColor( context, R.color.white  ) )
            binding.songAlbumFA.setTextColor( ContextCompat.getColor( context, R.color.white ) )
            binding.songDurationFA.setTextColor( ContextCompat.getColor( context, R.color.white )  )
            binding.more.setColorFilter(R.color.white)
            binding.root.setBackgroundColor( ContextCompat.getColor( context, R.color.dark ) )
        }
        return FavoriteViewHolder( binding )
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentAudio = favList[ position ]
        holder.bind( currentAudio )
        holder.itemView.setOnClickListener {
            when {
                currentAudio.duration == 0L -> songError(currentAudio)
                else -> {
                    val file = File(currentAudio.path)
                    if (file.exists()) {
                        val intent = Intent( context, PlayerActivity::class.java )
                        intent.putExtra( "songPos", position )
                        intent.putExtra( "class", "FavoriteAdapter" )
                        ContextCompat.startActivity( context, intent, null )
                    } else{
                        Toast.makeText(context, "Song not found!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return favList.size
    }

    private fun songError(currentAudio: Audio) {
        Toast.makeText(context, "The song has some problem!", Toast.LENGTH_SHORT).show()
        Log.i("MyTag", currentAudio.toString())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFavourites(favoriteMusicList: ArrayList<Audio>) {
        favList = ArrayList()
        favList.addAll( favoriteMusicList )
        notifyDataSetChanged()
    }
}