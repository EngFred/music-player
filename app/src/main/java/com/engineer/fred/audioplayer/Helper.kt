package com.engineer.fred.audioplayer

import android.app.Activity
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.Toast
import com.engineer.fred.audioplayer.PlayerActivity.Companion.ms
import com.engineer.fred.audioplayer.models.Audio
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

fun formatDuration( duration: Long ) : String {
    val minutes = TimeUnit.MINUTES.convert( duration, TimeUnit.MILLISECONDS )
    val seconds = TimeUnit.SECONDS.convert( duration, TimeUnit.MILLISECONDS ) - minutes* TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
    return  String.format( "%02d:%02d", minutes, seconds )
}

fun favoriteChecker( id: String ) {
    PlayerActivity.isFavorite = false
    for ( audio in FavoritesActivity.favMusicList ) {
        if ( id == audio.id ) {
            PlayerActivity.isFavorite = true
        }
    }
}

fun getImageArt( path: String ): ByteArray? {
    return try{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource( path )
        retriever.embeddedPicture
    }catch (e: Exception){
        Log.e( "MyTag", e.toString() )
        null
    }
}

fun getMainColor(  img: Bitmap) : Int {
    val newImg = Bitmap.createScaledBitmap( img, 1, 1, true )
    val color = newImg.getPixel( 0, 0 )
    newImg.recycle()
    return color
}

fun Activity.makeToast( message: String ) {
    val toast = Toast.makeText( this, message, Toast.LENGTH_LONG )
    return toast.show()
}

fun checkFileExists( audioList: ArrayList<Audio> ) : ArrayList<Audio> {
//    audioList.forEachIndexed { index, audio ->
//        val file = File( audio.path )
//        if (  !file.exists() ) audioList.removeAt( index )
//    }
    for ( audio in audioList ) {
        val file = File( audio.path )
        if ( !file.exists() ) audioList.remove( audio )
    }
    return  audioList
}

fun exitApplication() {
    ms?.stopForeground( true )
    ms?.mp?.release()
    exitProcess(100)
}
