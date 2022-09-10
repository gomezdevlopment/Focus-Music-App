package com.gomezdevlopment.focus_lofimusic.viewModels

import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.support.v7.graphics.Palette
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.gomezdevlopment.focus_lofimusic.ui.song_elements.*
import com.gomezdevlopment.focus_lofimusic.ui.theme.Purple80


class MusicPlayerViewModel(private val context: Context): ViewModel() {
    val sliderValue: MutableState<Int> = mutableStateOf(0)
    val songIsPlaying: MutableState<Boolean> = mutableStateOf(false)
    lateinit var mediaPlayer: MediaPlayer

    private var currentPlaylistIndex by mutableStateOf(0)

    private var songMap : Map<Int, Int> = mapOf(
        blossom to blossomArt,
        cityscape to cityscapeArt,
        embrace to embraceArt,
        water to waterArt
    )

    private var playlist = songMap.toList()
    var currentSongArt = mutableStateOf(playlist[currentPlaylistIndex].second)
    var currentSongLength = mutableStateOf(60f)
    var bgColor: MutableState<Color> = mutableStateOf(Purple80)
    private var timer: CountDownTimer? = null

    init {
        createMediaPlayer(playlist[0].first)
        createPaletteAsync()
    }

    fun createPaletteAsync() {
        val drawable: Drawable? = ContextCompat.getDrawable(context, currentSongArt.value)
        val bitmapDrawable: BitmapDrawable = drawable as BitmapDrawable
        val bitmap : Bitmap = bitmapDrawable.bitmap
        Palette.from(bitmap).generate { palette ->
            val swatch = palette?.vibrantSwatch
            val rgb = swatch?.rgb
            if(rgb!=null)
                bgColor.value = Color(rgb)
        }
    }

    fun pauseOrPlaySong(){
        songIsPlaying.value = !songIsPlaying.value
        if(songIsPlaying.value){
            mediaPlayer.start()
        }else{
            mediaPlayer.pause()
        }
    }

    fun nextSong(){
        resetMediaPlayer()
        if(currentPlaylistIndex < playlist.lastIndex)
            currentPlaylistIndex += 1
        else
            currentPlaylistIndex = 0
        currentSongArt.value = playlist[currentPlaylistIndex].second
        createMediaPlayer(playlist[currentPlaylistIndex].first)
    }

    fun previousSong(){
        resetMediaPlayer()
        if(currentPlaylistIndex > 0)
            currentPlaylistIndex -= 1
        else
            currentPlaylistIndex = playlist.lastIndex
        currentSongArt.value = playlist[currentPlaylistIndex].second
        createMediaPlayer(playlist[currentPlaylistIndex].first)
    }

    private fun resetMediaPlayer(){
        mediaPlayer.pause()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    private fun createMediaPlayer(audio: Int){
        mediaPlayer = MediaPlayer.create(context, audio)
        mediaPlayer.start()
        currentSongLength.value = mediaPlayer.duration.toFloat()
        createPaletteAsync()
        mediaPlayer.setOnCompletionListener {
            resetMediaPlayer()
            if(currentPlaylistIndex < playlist.lastIndex)
                currentPlaylistIndex += 1
            else
                currentPlaylistIndex = 0
            currentSongArt.value = playlist[currentPlaylistIndex].second
            createMediaPlayer(playlist[currentPlaylistIndex].first)
        }
        createTimer()
    }

    fun seek(){
        mediaPlayer.seekTo(sliderValue.value)
        createTimer()
    }


    fun skipBackwards(){
        sliderValue.value = mediaPlayer.currentPosition-10000
        mediaPlayer.seekTo(mediaPlayer.currentPosition-10000)
        createTimer()
    }

    fun skipForward(){
        sliderValue.value = mediaPlayer.currentPosition+10000
        mediaPlayer.seekTo(mediaPlayer.currentPosition+10000)
        createTimer()
    }

    private fun createTimer(){
        timer?.cancel()
        timer = object: CountDownTimer(mediaPlayer.duration.toLong()-mediaPlayer.currentPosition, 10) {
            override fun onTick(millisUntilFinished: Long) {
                sliderValue.value = mediaPlayer.currentPosition
            }
            override fun onFinish() {

            }
        }
        timer?.start()
    }
}