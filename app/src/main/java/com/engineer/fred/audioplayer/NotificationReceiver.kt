package com.engineer.fred.audioplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.engineer.fred.audioplayer.PlayerActivity.Companion.ms
import com.engineer.fred.audioplayer.PlayerActivity.Companion.musicListPA
import com.engineer.fred.audioplayer.PlayerActivity.Companion.repeatEnabled
import com.engineer.fred.audioplayer.PlayerActivity.Companion.songPosition
import java.io.File

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(  intent?.action ) {
            MyMusicApplication.PREVIOUS -> prevNext( false, context!! )
            MyMusicApplication.PLAY -> if ( PlayerActivity.isPlaying ) pause(  context ) else play( context )
            MyMusicApplication.NEXT -> prevNext( true, context!! )
            MyMusicApplication.EXIT -> {
               exitApplication()
            }
        }
    }

    companion object {

        fun prevNext( increment: Boolean, context: Context ) {
            try {
                if ( increment ) {
                    if ( !PlayerActivity.repeatEnabled ) {
                        if ( PlayerActivity.musicListPA.size - 1 == PlayerActivity.songPosition ){
                            PlayerActivity.songPosition = 0
                        } else {
                            PlayerActivity.songPosition++
                            val file = File(musicListPA[songPosition].path)
                            if ( PlayerActivity.musicListPA[PlayerActivity.songPosition].duration == 0L || !file.exists()) {
                                PlayerActivity.songPosition++
                            }
                        }
                    }
                    //setting layout in the Player Activity
                    PlayerActivity.binding.songName.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
                    PlayerActivity.binding.songName.isSelected = true
                    PlayerActivity.binding.artistName.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].artist
                    PlayerActivity.binding.songDuration.text = formatDuration( PlayerActivity.musicListPA[PlayerActivity.songPosition].duration )
                    PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
                    Glide.with( context ).load( PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri ).placeholder( R.drawable.launch_icon ).into( PlayerActivity.binding.playerImageView )
                    favoriteChecker(  PlayerActivity.musicListPA[PlayerActivity.songPosition].id  )
                    if (PlayerActivity.isFavorite) PlayerActivity.binding.addToFavouritesBtn.setImageResource( R.drawable.fav )
                    else {
                        PlayerActivity.binding.addToFavouritesBtn.setImageResource(R.drawable.favourite_empty)
                        PlayerActivity.binding.addToFavouritesBtn.setColorFilter( R.color.red )
                    }
                    if (PlayerActivity.repeatEnabled)  PlayerActivity.binding.repeatButton.setColorFilter(  ContextCompat.getColor( context, R.color.copperfield )  ) else PlayerActivity.binding.repeatButton.setColorFilter(  ContextCompat.getColor( context, R.color.white)  )
                    //setting the notification layout
                    ms!!.showNotification( R.drawable.baseline_pause_24 )
                    //setting the layout for Now Playing fragment
                    Glide.with( context ).load( PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri ).placeholder( R.drawable.launch_icon).into( NowPlayingFragment.binding.nowPlayingIV )
                    NowPlayingFragment.binding.songName.text = PlayerActivity.musicListPA[ PlayerActivity.songPosition ].title
                    NowPlayingFragment.binding.songName.isSelected = true
                    NowPlayingFragment.binding.playPauseBtn.setImageResource( R.drawable.baseline_pause_24 )
                    //Setting the layout for NowPlayingFragmentFromFav
                    NowPlayingFragmentFromFav.binding?.nowPlayingIV?.let {
                        Glide.with( context ).load( PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri ).placeholder( R.drawable.launch_icon ).into(
                            it
                        )
                    }
                    NowPlayingFragmentFromFav.binding?.songName?.text = PlayerActivity.musicListPA[ PlayerActivity.songPosition ].title
                    NowPlayingFragmentFromFav.binding?.songName?.isSelected = true
                    NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource( R.drawable.baseline_pause_24 )
                    ms?.currentPlayingSong = PlayerActivity.musicListPA[ PlayerActivity.songPosition ]
                    //create media player in Player Activity
                    try {
                        if (ms!!.mp == null) ms!!.mp = MediaPlayer()
                        ms!!.mp!!.reset()
                        ms!!.mp!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
                        ms!!.mp!!.prepare()
                        PlayerActivity.binding.songStartTime.text =  formatDuration(ms!!.mp!!.currentPosition.toLong())
                    } catch (ex: Exception) {
                        return
                    }
                    play( context )
                } else {
                    if ( !PlayerActivity.repeatEnabled) {
                        if ( PlayerActivity.songPosition == 0   ) PlayerActivity.songPosition = ( PlayerActivity.musicListPA.size - 1 )
                        else PlayerActivity.songPosition--
                    }
                    //setting layout in the Player Activity
                    PlayerActivity.binding.songName.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
                    PlayerActivity.binding.songName.isSelected = true
                    PlayerActivity.binding.artistName.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].artist
                    PlayerActivity.binding.songDuration.text = formatDuration( PlayerActivity.musicListPA[PlayerActivity.songPosition].duration )
                    PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
                    Glide.with( context ).load( PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri ).placeholder( R.drawable.launch_icon ).into( PlayerActivity.binding.playerImageView )
                    favoriteChecker(  PlayerActivity.musicListPA[PlayerActivity.songPosition].id  )
                    if (PlayerActivity.isFavorite) PlayerActivity.binding.addToFavouritesBtn.setImageResource( R.drawable.fav )
                    else {
                        PlayerActivity.binding.addToFavouritesBtn.setImageResource(R.drawable.favourite_empty)
                        PlayerActivity.binding.addToFavouritesBtn.setColorFilter( R.color.red )
                    }
                    if (PlayerActivity.repeatEnabled)  PlayerActivity.binding.repeatButton.setColorFilter(  ContextCompat.getColor( context, R.color.copperfield )  ) else PlayerActivity.binding.repeatButton.setColorFilter(  ContextCompat.getColor( context, R.color.white )  )
                    //setting the notification layout
                    ms!!.showNotification( R.drawable.baseline_pause_24 )
                    //setting the layout for Now Playing fragment
                    Glide.with( context ).load( PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri ).placeholder( R.drawable.launch_icon).into( NowPlayingFragment.binding.nowPlayingIV )
                    NowPlayingFragment.binding.songName.text = PlayerActivity.musicListPA[ PlayerActivity.songPosition ].title
                    NowPlayingFragment.binding.songName.isSelected = true
                    NowPlayingFragment.binding.playPauseBtn.setImageResource( R.drawable.baseline_pause_24 )
                    //Setting the layout for NowPlayingFragmentFromFav
                    NowPlayingFragmentFromFav.binding?.nowPlayingIV?.let {
                        Glide.with( context ).load( PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri ).placeholder( R.drawable.launch_icon ).into(
                            it
                        )
                    }
                    NowPlayingFragmentFromFav.binding?.songName?.text = PlayerActivity.musicListPA[ PlayerActivity.songPosition ].title
                    NowPlayingFragmentFromFav.binding?.songName?.isSelected = true
                    NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource( R.drawable.baseline_pause_24 )
                    ms?.currentPlayingSong = PlayerActivity.musicListPA[ PlayerActivity.songPosition ]
                    //create media player in Player Activity
                    try {
                        if (ms!!.mp == null) ms!!.mp = MediaPlayer()
                        ms!!.mp!!.reset()
                        ms!!.mp!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
                        ms!!.mp!!.prepare()
                        PlayerActivity.binding.songStartTime.text =  formatDuration(ms!!.mp!!.currentPosition.toLong())
                    } catch (ex: Exception) {
                        return
                    }
                    play( context )
                }
            }catch (e: IndexOutOfBoundsException) {
                PlayerActivity.songPosition = 0
                Log.e("Error", e.message.toString())
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            }
        }

        private fun play( context: Context? ) {
            if (File(PlayerActivity.musicListPA[PlayerActivity.songPosition].path).exists()) {
                PlayerActivity.isPlaying = true
                ms!!.mp!!.start()
                ms!!.showNotification(R.drawable.baseline_pause_24)
                PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
                NowPlayingFragment.binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
                NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource(R.drawable.baseline_pause_24)
            } else {
                Toast.makeText( context , "Song not found!", Toast.LENGTH_SHORT ).show()
            }
        }

        private fun pause( context: Context? ) {
            if (File(PlayerActivity.musicListPA[PlayerActivity.songPosition].path).exists()) {
                PlayerActivity.isPlaying = false
                ms!!.mp!!.pause()
                ms!!.showNotification(R.drawable.play)
                PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.play)
                NowPlayingFragment.binding.playPauseBtn.setImageResource(R.drawable.play)
                NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource(R.drawable.play)
            } else {
                Toast.makeText( context , "Song not found!", Toast.LENGTH_SHORT ).show()
            }
        }
    }


}