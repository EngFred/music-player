package com.engineer.fred.audioplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.engineer.fred.audioplayer.databinding.FragmentNowPlayingFromFavBinding
import java.io.File

class NowPlayingFragmentFromFav : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var binding: FragmentNowPlayingFromFavBinding? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().theme.applyStyle (MainActivity.currentTheme[  MainActivity.themeIndex ] , true )
        val view: View = inflater.inflate( R.layout.fragment_now_playing_from_fav, container, false )
        binding = FragmentNowPlayingFromFavBinding.bind( view )
        binding!!.root.visibility = View.GONE
        return view
    }

    override fun onResume() {
        super.onResume()
        if ( PlayerActivity.ms != null  ) {
            //setting layout for now playing fragment
            binding!!.root.visibility = View.VISIBLE
            Glide.with( requireContext() ).load( PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri ).placeholder( R.drawable.launch_icon ).into( binding!!.nowPlayingIV )
            binding!!.songName.text = PlayerActivity.musicListPA[ PlayerActivity.songPosition ].title
            if ( PlayerActivity.isPlaying ) binding!!.playPauseBtn.setImageResource( R.drawable.baseline_pause_24 ) else binding!!.playPauseBtn.setImageResource( R.drawable.play )
            binding!!.songName.isSelected = true

            binding!!.playPauseBtn.setOnClickListener {
                if ( PlayerActivity.isPlaying ) pause() else play()
            }
            binding!!.nextBtn.setOnClickListener {
                NotificationReceiver.prevNext( true, requireContext() )
            }

            binding!!.root.setOnClickListener {
                val intent = Intent( requireContext(), PlayerActivity::class.java )
                intent.putExtra( "songPos", PlayerActivity.songPosition )
                intent.putExtra( "class", "NowPlayingFragmentFromFav" )
                ContextCompat.startActivity( requireContext(), intent, null )
            }
        }
    }

    private fun play() {
        if (File(PlayerActivity.musicListPA[PlayerActivity.songPosition].path).exists()) {
            PlayerActivity.isPlaying = true
            PlayerActivity.ms!!.mp!!.start()
            PlayerActivity.ms!!.showNotification( R.drawable.baseline_pause_24 )
            PlayerActivity.binding.playPauseBtn.setImageResource( R.drawable.baseline_pause_24 )
            NowPlayingFragment.binding.playPauseBtn.setImageResource( R.drawable.baseline_pause_24 )
            binding!!.playPauseBtn.setImageResource( R.drawable.baseline_pause_24 )
        } else {
            Toast.makeText( requireActivity(), "Song not found!", Toast.LENGTH_SHORT ).show()
        }
    }

    private fun pause() {
        if (File(PlayerActivity.musicListPA[PlayerActivity.songPosition].path).exists()) {
            PlayerActivity.isPlaying = false
            PlayerActivity.ms!!.mp!!.pause()
            PlayerActivity.ms!!.showNotification(R.drawable.play)
            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.play)
            NowPlayingFragment.binding.playPauseBtn.setImageResource(R.drawable.play)
            binding!!.playPauseBtn.setImageResource(R.drawable.play)
        } else {
            Toast.makeText(requireActivity(), "Song not found!", Toast.LENGTH_SHORT).show()
        }
    }
}