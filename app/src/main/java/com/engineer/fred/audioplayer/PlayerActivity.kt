package com.engineer.fred.audioplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.bullhead.equalizer.Settings
import com.bumptech.glide.Glide
import com.engineer.fred.audioplayer.databinding.ActivityPlayerBinding
import com.engineer.fred.audioplayer.models.Audio
import com.google.android.material.snackbar.Snackbar
import java.io.File

class PlayerActivity: AppCompatActivity(), MediaPlayer.OnCompletionListener, ServiceConnection {

    private lateinit var runnable: Runnable

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        lateinit var musicListPA: ArrayList<Audio>
        var songPosition = 0
        var isPlaying = false
        var isFavorite = false
        var ms: MusicService? = null
        var repeatEnabled = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initializeLayout()

    }

    private fun initializeLayout() {
        receiveIntents()
        updateUi()
        registerClickListeners()
    }

    private fun receiveIntents() {
        songPosition = intent.getIntExtra("songPos", 0)
        when (intent.getStringExtra("class")) {
            "AudioAdapterSearch" -> {
                startMyMusicService()
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.searchMusicList)
                updateUi()
            }

            "AudioAdapter" -> {
                startMyMusicService()
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicList)
                updateUi()
            }

            "MainActivity" -> {
                startMyMusicService()
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicList)
                musicListPA.shuffle()
                updateUi()
            }

            "NowPlayingFragment" -> {
                updateUi()
                binding.songStartTime.text = formatDuration(ms!!.mp!!.currentPosition.toLong())
                binding.seekBar.progress = ms!!.mp!!.currentPosition
                binding.seekBar.max = ms!!.mp!!.duration
            }

            "NowPlayingFragmentFromFav" -> {
                updateUi()
                binding.songStartTime.text = formatDuration(ms!!.mp!!.currentPosition.toLong())
                binding.seekBar.progress = ms!!.mp!!.currentPosition
                binding.seekBar.max = ms!!.mp!!.duration
            }

            "FavoriteAdapter" -> {
                startMyMusicService()
                musicListPA = ArrayList()
                musicListPA.addAll(FavoritesActivity.favMusicList)
                updateUi()
            }

            "FavoriteActivityShuffle" -> {
                startMyMusicService()
                musicListPA = ArrayList()
                musicListPA.addAll(FavoritesActivity.favMusicList)
                musicListPA.shuffle()
                updateUi()
            }

            "PlayListDetail" -> {
                startMyMusicService()
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistListActivity.playlistList.ref[PlaylistDetail.currentPlayListPos].playlistSongs)
                updateUi()
            }

            "PlaylistDetailShuffle" -> {
                startMyMusicService()
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistListActivity.playlistList.ref[PlaylistDetail.currentPlayListPos].playlistSongs)
                musicListPA.shuffle()
                updateUi()
            }
        }
    }

    private fun createMediaPlayer(){
        if (ms!!.mp == null) ms!!.mp = MediaPlayer()
        ms!!.mp!!.reset()
        ms!!.mp!!.setDataSource(musicListPA[songPosition].path)
        ms!!.mp!!.prepare()
        ms!!.mp!!.start()
        ms!!.showNotification(R.drawable.baseline_pause_24)
        isPlaying = true
        binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
        NowPlayingFragment.binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
        binding.songStartTime.text = formatDuration(ms!!.mp!!.currentPosition.toLong())
        binding.seekBar.progress = 0
        binding.seekBar.max = ms!!.mp!!.duration
        setUpSeekBarMotion()
        ms!!.mp!!.setOnCompletionListener(this)
    }

    private fun updateUi() {
        favoriteChecker(musicListPA[songPosition].id)
        binding.apply {
            songName.text = musicListPA[songPosition].title
            songName.isSelected = true
            artistName.text = musicListPA[songPosition].album
            binding.songDuration.text = formatDuration(musicListPA[songPosition].duration)
            if (isPlaying) binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
            else binding.playPauseBtn.setImageResource(R.drawable.play)
            Glide.with(applicationContext)
                .load(getImageArt(musicListPA[songPosition].path))
                .fitCenter().placeholder(R.drawable.launch_icon).into(playerImageView)
            if (isFavorite) addToFavouritesBtn.setImageResource(R.drawable.fav)
            else addToFavouritesBtn.setImageResource(R.drawable.favourite_empty)

            when (MainActivity.themeIndex) {
                0 -> binding.root.background = AppCompatResources.getDrawable(baseContext, R.drawable.gradient_default)
                1 -> binding.root.background = AppCompatResources.getDrawable(baseContext, R.drawable.gradient_green)
                2 -> binding.root.background = AppCompatResources.getDrawable(baseContext, R.drawable.gradient_blue)
                3 -> binding.root.background = AppCompatResources.getDrawable(baseContext, R.drawable.gradient_orange)
                4 -> binding.root.background = AppCompatResources.getDrawable(baseContext, R.drawable.gradient_pink)
                5 -> binding.root.background = AppCompatResources.getDrawable(baseContext, R.drawable.gradient_dark)
            }
        }
    }

    private fun registerClickListeners() {
        binding.apply {
            playPauseBtn.setOnClickListener {
                if (isPlaying) pause() else play()
            }
            nextBtn.setOnClickListener {
                prevNext( true )
            }
            PrevBtn.setOnClickListener {
                prevNext( false )
            }

            seekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (  fromUser ) ms!!.mp!!.seekTo( progress )
                }
                override fun onStartTrackingTouch(p0: SeekBar?) = Unit
                override fun onStopTrackingTouch(p0: SeekBar?)  = Unit
            })

            backBtnPA.setOnClickListener {
                finish()
            }

            addToFavouritesBtn.setOnClickListener {
                favoriteChecker( musicListPA[songPosition].id )
                if (isFavorite){
                    //for toggle effect
                    isFavorite = false
                    addToFavouritesBtn.setImageResource( R.drawable.favourite_empty )
                    FavoritesActivity.favMusicList.remove( musicListPA[songPosition] )
                    Snackbar.make( binding.root, "Song removed from Favorites", Snackbar.LENGTH_SHORT ).show()
                } else {
                    //for toggle effect
                    isFavorite = true
                    addToFavouritesBtn.setImageResource( R.drawable.fav )
                    FavoritesActivity.favMusicList.add( musicListPA[songPosition] )
                    Snackbar.make( binding.root, "Song added to Favorites", Snackbar.LENGTH_SHORT ).show()
                }
                FavoritesActivity.favouritesChanged = true
            }

            eqBtn.setOnClickListener {
//               val intent = Intent( this@PlayerActivity, EqualizerActivity::class.java )
//                intent.putExtra("audioSession", ms!!.mp!!.audioSessionId )
//                startActivity( intent )
                try {
                    val eqIntent = Intent( AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL )
                    eqIntent.apply {
                        putExtra(  AudioEffect.EXTRA_AUDIO_SESSION, ms!!.mp!!.audioSessionId  )
                        putExtra(  AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName )
                        putExtra(  AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC  )
                    }
                    startActivityForResult( eqIntent, 2000 )
                } catch ( ex: Exception ) {
                    makeToast("You device doesn't not  have a built in equalizer!")
                }
//                binding.eqFrame.visibility = View.VISIBLE
//                val sessionId: Int = ms!!.mp!!.audioSessionId
////                Settings.isEditing = false
////                ms?.mp!!.isLooping = true
//                val equalizerFragment = EqualizerFragment.newBuilder()
//                    .setAccentColor(Color.parseColor("#4caf50"))
//                    .setAudioSessionId(sessionId)
//                    .build()
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.eqFrame , equalizerFragment)
//                    .commit()
            }

            shareBtn.setOnClickListener {
                val shareIntent = Intent(  Intent.ACTION_SEND  )
                shareIntent.apply {
                    type = "audio/*"
                    putExtra( Intent.EXTRA_STREAM , Uri.parse( musicListPA[songPosition].path ) )
                }
                val chooserIntent = Intent.createChooser( shareIntent, "Share ${ musicListPA[songPosition].title } via..." )
                startActivity( chooserIntent )
            }

            repeatButton.setOnClickListener {
                if ( !repeatEnabled) {
                    repeatEnabled = true
                    val imageRes = R.drawable.repeat_one
                    //binding.repeatButton.setColorFilter( iconTint )
                    repeatButton.setImageResource(imageRes)
                    //repeatButton.setColorFilter(  ContextCompat.getColor( baseContext, R.color.copperfield )  )
                } else {
                    repeatEnabled = false
                    val imageRes = R.drawable.baseline_repeat_24
                    //binding.repeatButton.setColorFilter( iconTint )
                    repeatButton.setImageResource(imageRes)
                }
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( resultCode == 2000 || resultCode == RESULT_OK ) return
    }

//    override fun onBackPressed() {
//        if ( binding.eqFrame.visibility == View.GONE ) {
//            super.onBackPressed()
//        } else {
//            binding.eqFrame.visibility = View.GONE
//        }
//    }

    private fun play() {
        if (  File( musicListPA[songPosition ].path ).exists()  ) {
            isPlaying = true
            binding.playPauseBtn.setImageResource( R.drawable.baseline_pause_24 )
            ms!!.showNotification( R.drawable.baseline_pause_24 )
            NowPlayingFragment.binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24 )
            NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource( R.drawable.baseline_pause_24 )
            ms!!.mp!!.start()
        } else {
            makeToast("Song not found!")
        }
    }

    private fun pause() {
        if (File(musicListPA[songPosition].path).exists()) {
            isPlaying = false
            binding.playPauseBtn.setImageResource(R.drawable.play)
            ms!!.showNotification(R.drawable.play)
            NowPlayingFragment.binding.playPauseBtn.setImageResource(R.drawable.play)
            NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource(R.drawable.play)
            ms!!.mp!!.pause()
        } else {
            makeToast("Song not found!")
        }
    }

    private fun prevNext( increment: Boolean ) {
        try {
            if ( increment ) {
                if ( !repeatEnabled) {
                    if ( musicListPA.size - 1 == songPosition){
                        songPosition = 0
                    } else {
                        songPosition++
                        val file = File(musicListPA[songPosition].path)
                        if (musicListPA[songPosition].duration == 0L || !file.exists()) {
                            songPosition++
                        }
                    }
                }
                updateUi()
                //setting the layout for Now Playing fragment
                Glide.with( applicationContext ).load( musicListPA[songPosition].artUri ).placeholder( R.drawable.launch_icon ).into( NowPlayingFragment.binding.nowPlayingIV )
                NowPlayingFragment.binding.songName.text = musicListPA[songPosition].title
                NowPlayingFragment.binding.songName.isSelected = true
                NowPlayingFragment.binding.playPauseBtn.setImageResource( R.drawable.baseline_pause_24 )
                //for Now Playing Fragment from fav
                NowPlayingFragmentFromFav.binding?.nowPlayingIV?.let { Glide.with( applicationContext ).load( musicListPA[songPosition].artUri ).placeholder( R.drawable.launch_icon ).into(it) }
                NowPlayingFragmentFromFav.binding?.songName?.text = musicListPA[songPosition].title
                NowPlayingFragmentFromFav.binding?.songName?.isSelected = true
                NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource( R.drawable.baseline_pause_24 )
                createMediaPlayer()
                ms?.currentPlayingSong = musicListPA[songPosition]
            } else {
                if ( !repeatEnabled) {
                    if ( songPosition == 0 ){
                        songPosition = ( musicListPA.size - 1 )
                    } else {
                        songPosition--
                        val file = File(musicListPA[songPosition].path)
                        if (musicListPA[songPosition].duration == 0L || !file.exists()) {
                            songPosition--
                        }
                    }
                }

                updateUi()
                //setting the layout for Now Playing fragment
                Glide.with( applicationContext ).load( musicListPA[songPosition].artUri ).placeholder( R.drawable.launch_icon ).into( NowPlayingFragment.binding.nowPlayingIV )
                NowPlayingFragment.binding.songName.text = musicListPA[songPosition].title
                NowPlayingFragment.binding.songName.isSelected = true
                NowPlayingFragment.binding.playPauseBtn.setImageResource( R.drawable.baseline_pause_24 )
                //for Now Playing Fragment from fav
                NowPlayingFragmentFromFav.binding?.nowPlayingIV?.let { Glide.with( applicationContext ).load( musicListPA[songPosition].artUri ).placeholder( R.drawable.launch_icon ).into(it) }
                NowPlayingFragmentFromFav.binding?.songName?.text = musicListPA[songPosition].title
                NowPlayingFragmentFromFav.binding?.songName?.isSelected = true
                NowPlayingFragmentFromFav.binding?.playPauseBtn?.setImageResource( R.drawable.baseline_pause_24 )
                createMediaPlayer()
                ms?.currentPlayingSong = musicListPA[songPosition]
            }
        }catch (e: Exception) {
            Toast.makeText(baseContext, "${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("Error", e.message.toString())
        }
    }

    private fun setUpSeekBarMotion() {
        runnable = Runnable {
            binding.songStartTime.text = formatDuration( ms!!.mp!!.currentPosition.toLong() )
            binding.seekBar.progress = ms!!.mp!!.currentPosition
            Handler( Looper.getMainLooper() ).postDelayed( runnable, 200 )
        }
        Handler( Looper.getMainLooper() ).postDelayed( runnable, 0 )
    }

    override fun onCompletion(mp: MediaPlayer?) {
        prevNext( true )
    }

    override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
        if (  ms != null ) ms!!.audioManager.abandonAudioFocus(ms)
        val binder = iBinder as MusicService.MyBinder
        ms = binder.getCurrentMusicService() // Initialization of the music service
        ms!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        ms!!.audioManager.requestAudioFocus(ms, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        createMediaPlayer()
        ms?.currentPlayingSong = musicListPA[songPosition]
    }

    override fun onServiceDisconnected( componentName: ComponentName? ) {
        ms = null
    }

    private fun startMyMusicService() {
        if (  ms != null ) ms!!.audioManager.abandonAudioFocus(ms)
        val intent = Intent( this, MusicService::class.java )
        bindService( intent, this, BIND_AUTO_CREATE )
        startService( intent )
    }

    override fun onResume() {
        super.onResume()
        Settings.isEqualizerEnabled = true
        val iconTint = if ( !repeatEnabled ) ContextCompat.getColor( baseContext, R.color.white ) else ContextCompat.getColor( baseContext, R.color.copperfield )
        val imageRes = if ( !repeatEnabled ) R.drawable.baseline_repeat_24 else R.drawable.repeat_one
        //binding.repeatButton.setColorFilter( iconTint )
        binding.repeatButton.setImageResource(imageRes)
    }
}