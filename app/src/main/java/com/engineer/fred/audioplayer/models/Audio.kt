package com.engineer.fred.audioplayer.models

data class Audio(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long,
    val path: String,
    val artUri: String,
)
