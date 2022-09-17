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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MusicPlayerViewModel(private val context: Context) : ViewModel() {
    val sliderValue: MutableState<Int> = mutableStateOf(0)
    val songIsPlaying: MutableState<Boolean> = mutableStateOf(false)
    var previousPlayer: MutableState<MediaPlayer> = mutableStateOf(MediaPlayer())
    var currentPlayer: MutableState<MediaPlayer> = mutableStateOf(MediaPlayer())
    var queuedPlayer: MutableState<MediaPlayer> = mutableStateOf(MediaPlayer())
    var queuedPlayerIsPrepared: MutableState<Boolean> = mutableStateOf(false)
    var previousPlayerIsPrepared: MutableState<Boolean> = mutableStateOf(false)
    var currentPlaylistIndex by mutableStateOf(0)
    var songArtBitmap: MutableState<Bitmap> =
        mutableStateOf(BitmapFactory.decodeResource(context.resources, R.drawable.embrace_art))

    var playlist: List<Song> = listOf(
        Song(bedtimeAfterACoffee, bedtimeAfterACoffeeArt, "bedtime after a coffee", "Barradeen", bedtimeAfterACoffeeCredits),
        Song(blossom, blossomArt, "Spirit Blossom", "RomanBelov", "RomanBelov"),
        Song(distant, distantArt, "Distant", "Ghostrifter Official", distantCredits),
        Song(embrace, embraceArt, "Embrace", "ItsWatR", "ItsWatR"),
        Song(ethereal, etherealArt, "Ethereal", "Ghostrifter Official", etherealCredits),
        Song(fluid, fluidArt, "Fluid", "ItsWatR", "ItsWatR"),
        Song(fragile, fragileArt, "Fragile", "Keys of Moon", fragileCredits),
        Song(herbalTea, herbalTeaArt, "Herbal Tea", "Artificial.Music", herbalTeaCredits),
        Song(interplanetaryTrip, interplanetaryTripArt, "Interplanetary Trip", "Billy Wuot", "Billy Wuot"),
        Song(jazzaddictsIntro, jazzaddictsIntroArt, "Jazzaddict’s Intro", "Cosimo Fogg", jazzaddictsIntroCredits),
        Song(lofiStudy, lofiStudyArt, "Lofi Study", "FASSounds", "FASSounds"),
        Song(missingTheStreet, missingTheStreetArt, "Missing The Street", "Billy Wuot", "Billy Wuot"),
        Song(missingYou, missingYouArt, "Missing You", "Purrple Cat", missingYouCredits),
        Song(noTurningBack, noTurningBackArt, "No Turning Back", "Billy Wuot", "Billy Wuot"),
        Song(odyssey, odysseyArt, "Odyssey", "Billy Wuot", "Billy Wuot"),
        Song(reverse, reverseArt, "Reverse", "Uniq", reverseCredits),
        Song(sandCastles, sandCastlesArt, "Sand Castles", "Purrple Cat", sandCastlesCredits),
        Song(sorrow, sorrowArt, "Sorrow", "Sappheiros", sorrowCredits),
        Song(sunShinesThroughTheLeaves, sunShinesThroughTheLeavesArt, "Sun shines through the leaves", "Babasmas", sunShinesThroughTheLeavesCredits),
        Song(underwater, underwaterArt, "Underwater", "LiQWYD", underwaterCredits),
        Song(smores, smoresArt, "S'mores", "Purrple Cat", smoresCredits),
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
        resetMediaPlayer(true)
        if (currentPlaylistIndex < playlist.lastIndex)
            currentPlaylistIndex += 1
        else
            currentPlaylistIndex = 0
        loadSongArt()
        if (queuedPlayerIsPrepared.value) {
            currentPlayer.value.start()
            queuedPlayerIsPrepared.value = false
        } else {
            songIsPlaying.value = false
        }
    }

    fun previousSong() {
        resetMediaPlayer(false)
        if (currentPlaylistIndex > 0)
            currentPlaylistIndex -= 1
        else
            currentPlaylistIndex = playlist.lastIndex
        loadSongArt()
        if (previousPlayerIsPrepared.value) {
            currentPlayer.value.start()
            previousPlayerIsPrepared.value = false
        } else {
            songIsPlaying.value = false
        }
    }

    private fun resetMediaPlayer(next: Boolean) {
        timer?.cancel()
        currentPlayer.value.pause()
        currentPlayer.value.reset()
        currentPlayer.value.release()
        if (next) {
            currentPlayer.value = queuedPlayer.value
        } else {
            currentPlayer.value = previousPlayer.value
        }
        currentSongLength.value = currentPlayer.value.duration.toFloat()
        createTimer()
        queuedPlayer.value = MediaPlayer()
        previousPlayer.value = MediaPlayer()
        createQueuedPlayers()
    }

    private fun createMediaPlayer() {
        try {
            currentPlayer.value.setAudioAttributes(AUDIO_ATTRIBUTES)
            currentPlayer.value.setDataSource(
                context,
                Uri.parse(playlist[currentPlaylistIndex].audioUrl)
            )
            currentPlayer.value.prepare()

            currentPlayer.value.setOnPreparedListener {
                createQueuedPlayers()
                currentSongLength.value = currentPlayer.value.duration.toFloat()
                createTimer()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun createQueuedPlayers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                queuedPlayer.value.setAudioAttributes(AUDIO_ATTRIBUTES)

                if (currentPlaylistIndex < playlist.lastIndex) {
                    queuedPlayer.value.setDataSource(
                        context,
                        Uri.parse(playlist[currentPlaylistIndex + 1].audioUrl)
                    )
                } else {
                    queuedPlayer.value.setDataSource(context, Uri.parse(playlist[0].audioUrl))
                }

                queuedPlayer.value.prepare()
                queuedPlayer.value.setOnPreparedListener {
                    println("next prepared")
                    queuedPlayerIsPrepared.value = true
                }

                previousPlayer.value.setAudioAttributes(AUDIO_ATTRIBUTES)

                if (currentPlaylistIndex > 0) {
                    previousPlayer.value.setDataSource(
                        context,
                        Uri.parse(playlist[currentPlaylistIndex - 1].audioUrl)
                    )
                } else {
                    previousPlayer.value.setDataSource(
                        context,
                        Uri.parse(playlist[playlist.lastIndex].audioUrl)
                    )
                }

                previousPlayer.value.prepare()
                previousPlayer.value.setOnPreparedListener {
                    println("prev prepared")
                    previousPlayerIsPrepared.value = true
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
            CountDownTimer(
                currentPlayer.value.duration.toLong() - currentPlayer.value.currentPosition,
                1000
            ) {
            override fun onTick(millisUntilFinished: Long) {
                sliderValue.value = currentPlayer.value.currentPosition
            }

            override fun onFinish() {

            }
        }
        timer?.start()
    }
}
