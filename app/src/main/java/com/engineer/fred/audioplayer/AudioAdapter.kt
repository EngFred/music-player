package com.engineer.fred.audioplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.engineer.fred.audioplayer.PlayerActivity.Companion.musicListPA
import com.engineer.fred.audioplayer.PlayerActivity.Companion.songPosition
import com.engineer.fred.audioplayer.databinding.MusicItemBinding
import com.engineer.fred.audioplayer.models.Audio

class AudioAdapter(
    private val context: Context,
    private var musicList: ArrayList<Audio>,
    private val playlistDetail: Boolean = false,
    private val selectionActivity: Boolean = false
) : Adapter<AudioAdapter.AudioViewHolder>() {

    inner class AudioViewHolder( private val binding: MusicItemBinding ) : ViewHolder( binding.root ) {
        val songName = binding.songName
        val songAlbum = binding.songAlbum
        val songDuration = binding.songDuration
        val root = binding.root
        fun bind( audio: Audio) {
           binding.apply {
               songName.text = audio.title
               songAlbum.text = audio.album
               songDuration.text = formatDuration( audio.duration )
               Glide.with(itemView)
                   .load(getImageArt(audio.path))
                   .fitCenter().placeholder(R.drawable.launch_icon).into(imageMusicView)
               //Glide.with( itemView ).load( audio.artUri ).placeholder( R.drawable.launch_icon ).into( imageMusicView  )
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val binding = MusicItemBinding.inflate(  LayoutInflater.from( parent.context ), parent, false  )
        if ( MainActivity.themeIndex == 5 ) {
            binding.songName.setTextColor( ContextCompat.getColor( context, R.color.white  ) )
            binding.songAlbum.setTextColor( ContextCompat.getColor( context, R.color.white ) )
            binding.songDuration.setTextColor( ContextCompat.getColor( context, R.color.white )  )
            binding.root.setBackgroundColor( ContextCompat.getColor( context, R.color.special ) )
        }
        return AudioViewHolder( binding )
    }

    private fun addSong( mAudio: Audio): Boolean {
        PlaylistListActivity.playlistList.ref[  PlaylistDetail.currentPlayListPos ].playlistSongs.forEachIndexed { index, audio ->
            if (mAudio.id == audio.id) {
                PlaylistListActivity.playlistList.ref[PlaylistDetail.currentPlayListPos].playlistSongs.removeAt(index)
                return false
            }
        }
        PlaylistListActivity.playlistList.ref[  PlaylistDetail.currentPlayListPos ].playlistSongs.add( mAudio  )
        return true
    }

    override fun onBindViewHolder(holder: AudioAdapter.AudioViewHolder, position: Int) {
        val currentAudio = musicList[ position ]
        holder.bind( currentAudio )
           when {
               playlistDetail -> {
                   holder.itemView.setOnClickListener {
                       val intent = Intent(context, PlayerActivity::class.java)
                       intent.putExtra("songPos", position)
                       intent.putExtra("class", "PlayListDetail")
                       ContextCompat.startActivity(context, intent, null)
                   }
               }
               selectionActivity -> {
                   if (musicList[ position ] !in PlaylistListActivity.playlistList.ref[ PlaylistDetail.currentPlayListPos ].playlistSongs){
                       if ( MainActivity.themeIndex != 5 ) {
                           holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.w_white))
                       } else {
                           holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.special))
                           holder.songName.setTextColor( ContextCompat.getColor( context, R.color.white  ) )
                           holder.songAlbum.setTextColor( ContextCompat.getColor( context, R.color.white ) )
                           holder.songDuration.setTextColor( ContextCompat.getColor( context, R.color.white )  )
                       }
                   } else {
                       holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.copperfield))
                   }
                   holder.itemView.setOnClickListener {
                       if (  addSong( musicList[ position  ]) ) {
                           holder.itemView.setBackgroundColor( ContextCompat.getColor( context, R.color.copperfield ) )
                       } else {
                           if ( MainActivity.themeIndex != 5 ) {
                               holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.w_white))
                           } else {
                               holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.special))
                               holder.songName.setTextColor( ContextCompat.getColor( context, R.color.white  ) )
                               holder.songAlbum.setTextColor( ContextCompat.getColor( context, R.color.white ) )
                               holder.songDuration.setTextColor( ContextCompat.getColor( context, R.color.white )  )
                           }
                       }
                   }
               }
               else -> {
                   holder.itemView.setOnClickListener {
                       when {
                           MainActivity.isSearching -> {
                               val intent = Intent(context, PlayerActivity::class.java)
                               intent.putExtra("songPos", position)
                               intent.putExtra("class", "AudioAdapterSearch")
                               ContextCompat.startActivity(context, intent, null)
                           }

                           else -> {
                               val intent = Intent(context, PlayerActivity::class.java)
                               intent.putExtra("songPos", position)
                               intent.putExtra("class", "AudioAdapter")
                               ContextCompat.startActivity(context, intent, null)
                           }
                       }
                   }
               }
           }
    }
    override fun getItemCount(): Int {
        return musicList.size
    }

   fun updateMusicList( searchList: ArrayList<Audio>) {
       musicList = ArrayList()
       musicList.addAll( searchList )
      notifyDataSetChanged()
   }

    fun refreshPlaylist() {
        musicList = ArrayList()
        musicList = PlaylistListActivity.playlistList.ref[ PlaylistDetail.currentPlayListPos ].playlistSongs
        notifyDataSetChanged()
    }
}