package com.gomezdevlopment.focus_lofimusic.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.gomezdevlopment.focus_lofimusic.R
import com.gomezdevlopment.focus_lofimusic.models.Song
import com.gomezdevlopment.focus_lofimusic.ui.song_elements.*
import com.gomezdevlopment.focus_lofimusic.ui.theme.Purple40
import com.gomezdevlopment.focus_lofimusic.ui.theme.Purple80
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MusicPlayerViewModel(private val context: Context) : ViewModel() {
    val sliderValue: MutableState<Int> = mutableStateOf(0)
    val songIsPlaying: MutableState<Boolean> = mutableStateOf(false)
    var mediaPlayer: MediaPlayer = MediaPlayer()

    var currentPlaylistIndex by mutableStateOf(0)
    var songArtBitmap: MutableState<Bitmap> =
        mutableStateOf(BitmapFactory.decodeResource(context.resources, R.drawable.embrace_art))

    var playlist: List<Song> = listOf(
        Song(blossom, blossomArt, "Spirit Blossom", "RomanBelov", "RomanBelov"),
        Song(lofiStudy, lofiStudyArt, "Lofi Study", "FASSounds", "FASSounds"),
        Song(embrace, embraceArt, "Embrace", "ItsWatR", "ItsWatR"),
        Song(sandCastles, sandCastlesArt, "Sand Castles", "Purrple Cat", sandCastlesCredits),
        Song(fluid, fluidArt, "Fluid", "ItsWatR", "ItsWatR")
    )

    var currentSongLength = mutableStateOf(60f)
    var bgColor: MutableState<Color> = mutableStateOf(Color.White)
    var accentColor: MutableState<Color> = mutableStateOf(Color.White)
    private var timer: CountDownTimer? = null

    private val AUDIO_ATTRIBUTES = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    init {
        createMediaPlayer(false)
    }

    private fun loadSongArt() {
        CoroutineScope(Dispatchers.IO).launch {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(playlist[currentPlaylistIndex].artUrl)
                .allowHardware(false) // Disable hardware bitmaps.
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap
            songArtBitmap.value = bitmap
            createPaletteAsync()
        }
    }

    private fun createPaletteAsync() {
        Palette.from(songArtBitmap.value).generate { palette ->
            val bgSwatch = palette?.mutedSwatch
            val bgRgb = bgSwatch?.rgb
            if (bgRgb != null)
                bgColor.value = Color(bgRgb)
            val accentSwatch = palette?.darkVibrantSwatch
            val accentRgb = accentSwatch?.rgb
            if (accentRgb != null)
                accentColor.value = Color(accentRgb)
        }
    }

    fun pauseOrPlaySong() {
        songIsPlaying.value = !songIsPlaying.value
        if (songIsPlaying.value) {
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
        }
    }

    fun nextSong() {
        resetMediaPlayer()
        if (currentPlaylistIndex < playlist.lastIndex)
            currentPlaylistIndex += 1
        else
            currentPlaylistIndex = 0
        createMediaPlayer(true)
    }

    fun previousSong() {
        resetMediaPlayer()
        if (currentPlaylistIndex > 0)
            currentPlaylistIndex -= 1
        else
            currentPlaylistIndex = playlist.lastIndex
        createMediaPlayer(true)
    }

    private fun resetMediaPlayer() {
        timer?.cancel()
        println(mediaPlayer)
        mediaPlayer.pause()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    private fun createMediaPlayer(shouldStart: Boolean) {
        println(playlist[currentPlaylistIndex].audioUrl)
        loadSongArt()
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioAttributes(AUDIO_ATTRIBUTES)
            mediaPlayer.setDataSource(context, Uri.parse(playlist[currentPlaylistIndex].audioUrl))
            mediaPlayer.prepare()
            if (shouldStart) {
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        currentSongLength.value = mediaPlayer.duration.toFloat()
        createTimer()
        mediaPlayer.setOnCompletionListener {
//            resetMediaPlayer()
//            if (currentPlaylistIndex < playlist.lastIndex)
//                currentPlaylistIndex += 1
//            else
//                currentPlaylistIndex = 0
//            createMediaPlayer(true)
        }

    }

    fun seek() {
        mediaPlayer.seekTo(sliderValue.value)
        createTimer()
    }


    fun skipBackwards() {
        sliderValue.value = mediaPlayer.currentPosition - 10000
        mediaPlayer.seekTo(mediaPlayer.currentPosition - 10000)
        createTimer()
    }

    fun skipForward() {
        sliderValue.value = mediaPlayer.currentPosition + 10000
        mediaPlayer.seekTo(mediaPlayer.currentPosition + 10000)
        createTimer()
    }

    private fun createTimer() {
        timer = object :
            CountDownTimer(mediaPlayer.duration.toLong() - mediaPlayer.currentPosition, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sliderValue.value = mediaPlayer.currentPosition
            }

            override fun onFinish() {

            }
        }
        timer?.start()
    }
}
