package com.engineer.fred.audioplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.engineer.fred.audioplayer.databinding.ActivityMainBinding
import com.engineer.fred.audioplayer.models.Audio
import com.engineer.fred.audioplayer.models.PlaylistList
import com.google.android.material.button.MaterialButton
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File


class MainActivity : AppCompatActivity() {

    private val perm = android.Manifest.permission.READ_EXTERNAL_STORAGE
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val perm2 = android.Manifest.permission.READ_MEDIA_AUDIO
    private val writePermReturnId = 7239 // any suitable constant

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMainBinding
        var  musicList: ArrayList<Audio> = ArrayList()
        lateinit var searchMusicList: ArrayList<Audio>
        @SuppressLint("StaticFieldLeak")
        lateinit var audioAdapter: AudioAdapter
        var isSearching = false
        //for themes
        var themeIndex = 0
        val currentTheme = arrayOf( R.style.defaultTheme, R.style.greenTheme, R.style.darkTheme, R.style.orangeTheme, R.style.pinkTheme, R.style.dark2Theme )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeIndex = getSharedPreferences( "THEMES", MODE_PRIVATE ).getInt("themeIndex", 0 )
        setTheme( currentTheme[  themeIndex ] )
        binding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( binding.root  )
        setSupportActionBar( binding.toolbar )
        //binding.toolbar.setTitleTextColor( ContextCompat.getColor( this, R.color.white ) )

        if ( reqRuntimePerms() ) {
            retrieveSharedPreferencesData()
            initialiseLayout()
        }
    }

    private fun initialiseLayout() {
        if ( themeIndex == 5 ) {
            binding.root.setBackgroundColor(  ContextCompat.getColor( this, R.color.dark) )
            binding.allSongsBtn.apply {
                setTextColor( ContextCompat.getColor( this@MainActivity, R.color.white) )
                icon =  AppCompatResources.getDrawable( baseContext, R.drawable.baseline_queue_music_dark_mode )
                iconGravity = MaterialButton.ICON_GRAVITY_TOP
            }
            binding.playlistsBtn.apply {
                setTextColor( ContextCompat.getColor( this@MainActivity, R.color.white) )
                icon =  AppCompatResources.getDrawable( baseContext, R.drawable.playlist_add_dark_mode )
                iconGravity = MaterialButton.ICON_GRAVITY_TOP
            }
            binding.favoriteBtn.apply {
                setTextColor( ContextCompat.getColor( this@MainActivity, R.color.white) )
            }
            binding.totalSongs.setTextColor( ContextCompat.getColor( this@MainActivity, R.color.white ))
            binding.totalSongsNumber.setTextColor( ContextCompat.getColor( this@MainActivity, R.color.white  ))
        }

        binding.refreshLayout.setOnRefreshListener {
            musicList = getAllAudio()
            audioAdapter.updateMusicList( musicList )
            binding.refreshLayout.isRefreshing = false
        }

        isSearching = false
        musicList = getAllAudio()
        audioAdapter = AudioAdapter( this, musicList )
        setUpRecyclerView()
        binding.totalSongsNumber.text = audioAdapter.itemCount.toString()
        registerClickEvents()

    }

    private fun setUpRecyclerView() {
        binding.apply {
            musicRV.setHasFixedSize( true )
            musicRV.setItemViewCacheSize( 13 )
            musicRV.layoutManager = LinearLayoutManager( this@MainActivity )
            musicRV.adapter = audioAdapter
        }
    }

    private fun registerClickEvents() {
        binding.floatingBtn.setOnClickListener {
            makeToast( "Shuffle mode enabled!" )
            val intent = Intent( this, PlayerActivity::class.java )
            intent.putExtra("songPos", 0 )
            intent.putExtra("class", "MainActivity")
            startActivity( intent )
        }
        binding.favoriteBtn.setOnClickListener {
            val intent = Intent( this, FavoritesActivity::class.java )
            startActivity( intent )
        }
        binding.playlistsBtn.setOnClickListener {
            val intent = Intent( this, PlaylistListActivity::class.java )
            startActivity( intent )
        }
    }

    //check run time permission
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun reqRuntimePerms() : Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(this, perm )
        val permissionCheck2 = ActivityCompat.checkSelfPermission( this, perm2 )
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ) {
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(perm), writePermReturnId)
                return false
            }
        } //android 13 permission request
        else if( Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU ){
            if (  permissionCheck2 != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, arrayOf(perm2), writePermReturnId )
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when( requestCode ) {
            writePermReturnId -> {
                if ( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    makeToast( "Permission granted!" )
                    initialiseLayout()
                    if ( musicList.size < 1  ) binding.floatingBtn.visibility = View.GONE
                } else {
                    ActivityCompat.requestPermissions( this, arrayOf( perm ), writePermReturnId )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate( R.menu.menu, menu )
        val sV = menu?.findItem(  R.id.search_icon )?.actionView as SearchView
        sV.setOnQueryTextListener( object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val userInput = it.lowercase()
                    searchMusicList = ArrayList()
                    for (  song in musicList  )
                        if (  song.title.lowercase().contains( userInput ) )
                            searchMusicList.add( song )
                    isSearching = true
                    audioAdapter.updateMusicList( searchMusicList )
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when( item.itemId ) {
            R.id.about -> startActivity( Intent( this, AboutActivity::class.java ) )
            R.id.settings -> startActivity( Intent( this, SettingsActivity::class.java ) )
        }
        return super.onOptionsItemSelected(item)
    }

    //Load audio files
    @SuppressLint("Range")
    private fun getAllAudio() : ArrayList<Audio> {
        val tempList = ArrayList<Audio>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null)
        if ( cursor != null ){
            if ( cursor.moveToFirst() )
                do {
                    val  title = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.TITLE ))
                    val  id = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media._ID ))
                    val  album = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM ))
                    val  artist = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST))
                    val  path = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.DATA))
                    val  duration = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.DURATION))
                    val  albumId = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart/")
                    val artUri = Uri.withAppendedPath( uri, albumId ).toString()
                    val audio = Audio( id, title, album, artist, duration, path , artUri )
                    val file = File( audio.path )
                    if ( file.exists() ) tempList.add( audio )
                } while ( cursor.moveToNext() )
            cursor.close()
        }
        return tempList
    }

    override fun onResume() {
        super.onResume()
        //store Playlist list data in shared preferences using gson
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistListActivity.playlistList)
        getSharedPreferences("FAVORITES", MODE_PRIVATE ).edit().putString( "PlaylistList", jsonStringPlaylist ).apply()
        //store favorite songs data in shared preferences using gson
        val jsonString = GsonBuilder().create().toJson(FavoritesActivity.favMusicList)
        getSharedPreferences("FAVORITES", MODE_PRIVATE ).edit().putString( "FavoriteMusicList", jsonString ).apply()
    }

    private fun retrieveSharedPreferencesData() {
        //retrieving favorite songs data in shared preferences using gson
        FavoritesActivity.favMusicList = ArrayList()
        val jsonString = getSharedPreferences("FAVORITES", MODE_PRIVATE ).getString( "FavoriteMusicList",  null )
        val typeToken = object  :  TypeToken<ArrayList<Audio>>(){}.type
        jsonString?.let {
            val data: ArrayList<Audio> = GsonBuilder().create().fromJson( jsonString, typeToken )
            FavoritesActivity.favMusicList.addAll( data )
        }

        //retrieving Playlist list data in shared preferences using gson
        PlaylistListActivity.playlistList = PlaylistList()
        val jsonStringPlaylistList= getSharedPreferences("FAVORITES", MODE_PRIVATE ).getString( "PlaylistList",  null )
        jsonStringPlaylistList?.let {
            val dataPlaylistList: PlaylistList = GsonBuilder().create().fromJson( jsonStringPlaylistList, PlaylistList::class.java )
            PlaylistListActivity.playlistList = dataPlaylistList
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        if ( PlayerActivity.isPlaying ) {
//            PlayerActivity.ms?.stopForeground(true)
//            PlayerActivity.ms?.mp?.release()
//            PlayerActivity.ms = null
//        }
//    }

}