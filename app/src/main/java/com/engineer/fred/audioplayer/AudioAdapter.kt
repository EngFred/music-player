package com.engineer.fred.audioplayer

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.engineer.fred.audioplayer.databinding.MusicItemBinding
import com.engineer.fred.audioplayer.models.Audio
import java.io.File

class AudioAdapter(
    private val context: Context,
    private var musicList: ArrayList<Audio>,
    private val playlistDetail: Boolean = false,
    private val selectionActivity: Boolean = false
) : Adapter<AudioAdapter.AudioViewHolder>() {

    private var songDeletedListener: SongDeletedListener? = null

    fun onSongDeleted( songDeletedListener: SongDeletedListener ) {
        this.songDeletedListener = songDeletedListener
    }

    inner class AudioViewHolder( private val binding: MusicItemBinding ) : ViewHolder( binding.root ) {
        val songName = binding.songName
        val songAlbum = binding.songAlbum
        val songDuration = binding.songDuration
        val moreVert = binding.more
        val root = binding.root
        fun bind(audio: Audio, position: Int) {
           binding.apply {
               songName.text = audio.title
               songAlbum.text = audio.album
               songDuration.text = formatDuration( audio.duration )
               Glide.with(itemView)
                   .load(getImageArt(audio.path))
                   .fitCenter().placeholder(R.drawable.launch_icon).into(imageMusicView)
               //Glide.with( itemView ).load( audio.artUri ).placeholder( R.drawable.launch_icon ).into( imageMusicView  )
               moreVert.setOnClickListener {
                   val popupMenu = PopupMenu(context, it)
                   popupMenu.menuInflater.inflate(R.menu.delete_song_popup_menu, popupMenu.menu)
                   popupMenu.setOnMenuItemClickListener { menuItem ->
                       when (menuItem.itemId) {
                           R.id.action_delete -> {
                               showDeleteConfirmationDialog(audio)
                               true
                           }

                           else -> false
                       }
                   }
                   popupMenu.show()
               }
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val binding = MusicItemBinding.inflate(  LayoutInflater.from( parent.context ), parent, false  )
        if ( MainActivity.themeIndex == 5 ) {
            binding.songName.setTextColor( ContextCompat.getColor( context, R.color.white  ) )
            binding.songAlbum.setTextColor( ContextCompat.getColor( context, R.color.white ) )
            binding.songDuration.setTextColor( ContextCompat.getColor( context, R.color.white )  )
            binding.more.setColorFilter(R.color.white)
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
        holder.bind( currentAudio, position )
           when {
               playlistDetail -> {
                   holder.itemView.setOnClickListener {
                       when {
                           currentAudio.duration == 0L -> songError(currentAudio)
                           else -> {
                               val file = File(currentAudio.path)
                               if (file.exists()) {
                                   val intent = Intent(context, PlayerActivity::class.java)
                                   intent.putExtra("songPos", position)
                                   intent.putExtra("class", "PlayListDetail")
                                   ContextCompat.startActivity(context, intent, null)
                               } else{
                                   Toast.makeText(context, "Song not found!", Toast.LENGTH_SHORT).show()
                               }
                           }
                       }
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
                               when {
                                   currentAudio.duration == 0L -> songError(currentAudio)
                                   else -> {
                                       val file = File(currentAudio.path)
                                       if (file.exists()) {
                                           val intent = Intent(context, PlayerActivity::class.java)
                                           intent.putExtra("songPos", position)
                                           intent.putExtra("class", "AudioAdapterSearch")
                                           ContextCompat.startActivity(context, intent, null)
                                       } else{
                                           Toast.makeText(context, "Song not found!", Toast.LENGTH_SHORT).show()
                                       }
                                   }
                               }
                           }

                           else -> {
                               when {
                                   currentAudio.duration == 0L -> songError(currentAudio)
                                   else -> {
                                       val file = File(currentAudio.path)
                                       if (file.exists()) {
                                           val intent = Intent(context, PlayerActivity::class.java)
                                           intent.putExtra("songPos", position)
                                           intent.putExtra("class", "AudioAdapter")
                                           ContextCompat.startActivity(context, intent, null)
                                       } else{
                                           Toast.makeText(context, "Song not found!", Toast.LENGTH_SHORT).show()
                                       }
                                   }
                               }
                           }
                       }
                   }
               }
           }
    }

    private fun songError(currentAudio: Audio) {
        Toast.makeText(context, "The song has some problem!", Toast.LENGTH_SHORT).show()
        Log.i("MyTag", currentAudio.toString())
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

   fun updateMusicList(audioList: ArrayList<Audio>) {
       musicList = ArrayList()
       musicList.addAll( audioList )
      notifyDataSetChanged()
   }

    fun refreshPlaylist() {
        musicList = ArrayList()
        musicList = PlaylistListActivity.playlistList.ref[ PlaylistDetail.currentPlayListPos ].playlistSongs
        notifyDataSetChanged()
    }

    private fun showDeleteConfirmationDialog(audio: Audio) {
        AlertDialog.Builder(context).apply {
            setTitle("Delete Song")
            setMessage("Are you sure you want to delete \"${audio.title}\"?")
            setPositiveButton("Delete") { dialog, _ ->
                deleteSong(audio)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun deleteSong(audio: Audio) {
        val file = File(audio.path)
        Log.d("MyTag", "Trying to delete file: ${file.path}")

        if (file.exists()) {
            Log.d("MyTag", "File exists. Path: ${file.path}")

            if (file.canWrite()) {
                Log.d("MyTag", "File is writable.")
                if (file.delete()) {
                    Toast.makeText(context, "Song deleted", Toast.LENGTH_SHORT).show()
                    // Remove the song from the list and notify the adapter
                    songDeletedListener?.onSongDeleted(audio)
                    updateMusicList(MainActivity.musicList)
                    Log.d("MyTag", "File deleted successfully.")
                } else {
                    Toast.makeText(context, "Unable to delete song", Toast.LENGTH_SHORT).show()
                    Log.d("MyTag", "Unable to delete song: ${file.path}")
                }
            } else {
                Toast.makeText(context, "File is not writable", Toast.LENGTH_SHORT).show()
                Log.d("MyTag", "File is not writable: ${file.path}")
            }
        } else {
            Toast.makeText(context, "File does not exist!", Toast.LENGTH_SHORT).show()
            Log.d("MyTag", "File does not exist: ${file.path}")
        }
    }

    interface SongDeletedListener {
        fun onSongDeleted(audio: Audio)
    }


}