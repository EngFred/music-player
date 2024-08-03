package com.engineer.fred.audioplayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.bullhead.equalizer.EqualizerFragment
import com.engineer.fred.audioplayer.PlayerActivity.Companion.ms

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mp: MediaPlayer? = null
    private lateinit var mediaSessionCompat: MediaSessionCompat
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {
        mediaSessionCompat = MediaSessionCompat( baseContext, "My Music" )
        return myBinder
    }
    inner class MyBinder : Binder() {
        fun getCurrentMusicService(): MusicService {
            return this@MusicService
        }
    }

        @SuppressLint("ResourceAsColor")
        fun showNotification(playPauseIcon: Int) {

            val intent = Intent(baseContext, MainActivity::class.java)
            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) PendingIntent.FLAG_IMMUTABLE  else PendingIntent.FLAG_UPDATE_CURRENT
            
            val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

            val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(MyMusicApplication.PREVIOUS)
            val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

            val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(MyMusicApplication.PLAY)
            val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

            val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(MyMusicApplication.NEXT)
            val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

            val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(MyMusicApplication.EXIT)
            val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

            val imageArt = getImageArt( PlayerActivity.musicListPA[PlayerActivity.songPosition].path )
            val image = if ( imageArt != null ) {
                BitmapFactory.decodeByteArray( imageArt, 0, imageArt.size )
            } else {
                BitmapFactory.decodeResource( resources, R.drawable.splash_toon )
            }

            val notification = NotificationCompat.Builder(baseContext, MyMusicApplication.CHANNEL_ID)
                    .setContentIntent( contentIntent )
                    .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
                    .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
                    .setSmallIcon(R.drawable.audio_track )
                    .setLargeIcon( image )
                    .setStyle( androidx.media.app.NotificationCompat.MediaStyle().setMediaSession( mediaSessionCompat.sessionToken ) )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOnlyAlertOnce(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(R.drawable.android_prev_not, "Prev", prevPendingIntent)
                    .addAction(playPauseIcon, "Play", playPendingIntent)
                    .addAction(R.drawable.android_next_not, "Next", nextPendingIntent)
                    .addAction(R.drawable.android_close, "Close", exitPendingIntent)
                    .build()

            startForeground(2000, notification)
        }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            //pause music
            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.play)
            showNotification(R.drawable.play)
            NowPlayingFragment.binding.playPauseBtn.setImageResource(R.drawable.play)
            NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource(R.drawable.play)
            PlayerActivity.isPlaying = false
            ms!!.mp!!.pause()
        } else {
            //play music
//            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
//            showNotification(R.drawable.baseline_pause_24)
//            NowPlayingFragment.binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
//            NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource(R.drawable.baseline_pause_24)
//            PlayerActivity.isPlaying = true
//            ms!!.mp!!.start()

        }

    }
}