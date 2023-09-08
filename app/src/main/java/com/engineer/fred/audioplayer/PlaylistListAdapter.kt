package com.engineer.fred.audioplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.engineer.fred.audioplayer.databinding.PlaylistBinding
import com.engineer.fred.audioplayer.models.Playlist

class PlaylistListAdapter( private val context: Context, private var playlistList: ArrayList<Playlist>) : Adapter<PlaylistListAdapter.PlaylistListViewHolder>() {

    private lateinit var adapterClickListener: AdapterClickListener

    fun setUpClickListener(  listener: AdapterClickListener  ) {
        this.adapterClickListener = listener
    }
    inner class PlaylistListViewHolder( private val binding: PlaylistBinding  ) : ViewHolder( binding.root ) {
        fun bind(playlist: Playlist, position: Int) {
            binding.apply {
                playlistName.text = playlist.name
                playlistName.isSelected = true
                if (  PlaylistListActivity.playlistList.ref[ position ].playlistSongs.size > 0  ) {
                    Glide.with( context ).load(  PlaylistListActivity.playlistList.ref[ position ].playlistSongs[0].artUri  )
                        .placeholder( R.drawable.launch_icon )
                        .centerCrop().into( playlistListImageView )
                }
                playlistListItemDelete.setOnClickListener { adapterClickListener.deletePlayList( playlist ) }
                root.setOnClickListener {  adapterClickListener.showPlayListDetail( playlist , position )  }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistListViewHolder {
        val binding = PlaylistBinding.inflate(  LayoutInflater.from( parent.context ), parent, false  )
        return PlaylistListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistListViewHolder, position: Int) {
        val currentPlaylist = playlistList[position]
        holder.bind( currentPlaylist, position )
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun refreshPlaylist() {
//        playlistList = ArrayList()
//        playlistList.addAll(  PlaylistListActivity.playlistList.ref  )
//        notifyDataSetChanged()
//    }

    interface AdapterClickListener {
        fun deletePlayList(  playlist: Playlist  )
        fun showPlayListDetail( playlist: Playlist, playlistPos: Int )
    }
}