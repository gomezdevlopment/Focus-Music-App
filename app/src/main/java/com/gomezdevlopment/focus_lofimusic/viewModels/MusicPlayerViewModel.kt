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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class MusicPlayerViewModel(private val context: Context) : ViewModel() {
    val sliderValue: MutableState<Int> = mutableStateOf(0)
    val songIsPlaying: MutableState<Boolean> = mutableStateOf(false)
    var mediaPlayer: MediaPlayer = MediaPlayer()
    var mediaPlayer2: MediaPlayer = MediaPlayer()
    var currentPlayer: MutableState<MediaPlayer> = mutableStateOf(mediaPlayer)
    var queuedPlayer: MutableState<MediaPlayer> = mutableStateOf(mediaPlayer2)
    var queuedPlayerIsPrepared: MutableState<Boolean> = mutableStateOf(false)
    var currentPlaylistIndex by mutableStateOf(0)
    var songArtBitmap: MutableState<Bitmap> =
        mutableStateOf(BitmapFactory.decodeResource(context.resources, R.drawable.embrace_art))

    var playlist: List<Song> = listOf(
        Song(bedtimeAfterACoffee, bedtimeAfterACoffeeArt, "bedtime after a coffee", "Barradeen", bedtimeAfterACoffeeCredits),
        Song(blossom, blossomArt, "Spirit Blossom", "RomanBelov", "RomanBelov"),
        Song(embrace, embraceArt, "Embrace", "ItsWatR", "ItsWatR"),
        Song(fluid, fluidArt, "Fluid", "ItsWatR", "ItsWatR"),
        Song(herbalTea, herbalTeaArt, "Herbal Tea", "Artificial.Music", herbalTeaCredits),
        Song(lofiStudy, lofiStudyArt, "Lofi Study", "FASSounds", "FASSounds"),
        Song(sandCastles, sandCastlesArt, "Sand Castles", "Purrple Cat", sandCastlesCredits),
        Song(smores, smoresArt, "S\'mores", "Purrple Cat", smoresCredits),
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
        playlist = playlist.shuffled()
        loadSongArt()
        createMediaPlayer()
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
            createPaletteAsync(bitmap)
        }
    }

    private fun createPaletteAsync(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            val bgSwatch = palette?.mutedSwatch
            val bgRgb = bgSwatch?.rgb
            if (bgRgb != null)
                bgColor.value = Color(bgRgb)
            val accentSwatch = palette?.darkVibrantSwatch
            val accentRgb = accentSwatch?.rgb
            if (accentRgb != null)
                accentColor.value = Color(accentRgb)
        }
        songArtBitmap.value = bitmap
    }

    fun pauseOrPlaySong() {
        songIsPlaying.value = !songIsPlaying.value
        if (songIsPlaying.value) {
            currentPlayer.value.start()
        } else {
            currentPlayer.value.pause()
        }
    }

    fun nextSong() {
        resetMediaPlayer()
        if (currentPlaylistIndex < playlist.lastIndex)
            currentPlaylistIndex += 1
        else
            currentPlaylistIndex = 0
        loadSongArt()
        if(queuedPlayerIsPrepared.value){
            currentPlayer.value.start()
        }else{
            songIsPlaying.value = false
        }
    }

    fun previousSong() {
        resetMediaPlayer()
        if (currentPlaylistIndex > 0)
            currentPlaylistIndex -= 1
        else
            currentPlaylistIndex = playlist.lastIndex
    }

    private fun resetMediaPlayer() {
        timer?.cancel()
        currentPlayer.value.pause()
        currentPlayer.value.reset()
        currentPlayer.value.release()
        val finishedPlayer = currentPlayer.value
        currentPlayer.value = queuedPlayer.value
        currentSongLength.value = currentPlayer.value.duration.toFloat()
        createTimer()
        queuedPlayer.value = finishedPlayer
        queuedPlayer.value = MediaPlayer()
        createQueuedPlayer()
    }

    private fun createMediaPlayer() {
        try {
            mediaPlayer.setAudioAttributes(AUDIO_ATTRIBUTES)
            mediaPlayer.setDataSource(context, Uri.parse(playlist[currentPlaylistIndex].audioUrl))
            mediaPlayer.prepare()

            mediaPlayer.setOnPreparedListener {
                createQueuedPlayer()
                currentSongLength.value = currentPlayer.value.duration.toFloat()
                createTimer()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun createQueuedPlayer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                queuedPlayer.value.setAudioAttributes(AUDIO_ATTRIBUTES)
                if (currentPlaylistIndex < playlist.lastIndex)
                    queuedPlayer.value.setDataSource(context, Uri.parse(playlist[currentPlaylistIndex+1].audioUrl))
                else
                    queuedPlayer.value.setDataSource(context, Uri.parse(playlist[0].audioUrl))
                queuedPlayer.value.prepare()
                queuedPlayer.value.setOnPreparedListener {
                    queuedPlayerIsPrepared.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun seek() {
        currentPlayer.value.seekTo(sliderValue.value)
        createTimer()
    }


    fun skipBackwards() {
        sliderValue.value = currentPlayer.value.currentPosition - 10000
        currentPlayer.value.seekTo(currentPlayer.value.currentPosition - 10000)
        createTimer()
    }

    fun skipForward() {
        sliderValue.value = currentPlayer.value.currentPosition + 10000
        currentPlayer.value.seekTo(currentPlayer.value.currentPosition + 10000)
        createTimer()
    }

    private fun createTimer() {
        timer = object :
            CountDownTimer(currentPlayer.value.duration.toLong() - currentPlayer.value.currentPosition, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sliderValue.value = currentPlayer.value.currentPosition
            }

            override fun onFinish() {

            }
        }
        timer?.start()
    }
}
