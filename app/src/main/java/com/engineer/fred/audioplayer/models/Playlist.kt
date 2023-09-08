package com.engineer.fred.audioplayer.models

data class Playlist (
    var name: String,
    var playlistSongs: ArrayList<Audio>,
    var createdBy: String,
    var createdOn: String,
)

class PlaylistList {
    var ref: ArrayList<Playlist>  = ArrayList()
}