package com.gomezdevlopment.focus_lofimusic.ViewModels

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.focus_lofimusic.R

class MusicPlayerViewModel(context: Context): ViewModel() {
    val songIsPlaying: MutableState<Boolean> = mutableStateOf(false)
    private var mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.embrace)

    fun pauseOrPlaySong(){
        songIsPlaying.value = !songIsPlaying.value
        if(songIsPlaying.value){
            mediaPlayer.start()
        }else{
            mediaPlayer.pause()
        }
    }
}