package com.gomezdevlopment.focus_lofimusic.ui.music_player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.focus_lofimusic.viewModels.MusicPlayerViewModel
import com.gomezdevlopment.focus_lofimusic.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun MusicPlayerScreen(vm: MusicPlayerViewModel) {
    val bgColor by vm.bgColor
    val songArt by vm.songArtBitmap
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(bgColor)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(bgColor),
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = songArt),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(.8f)
                )

                Text(
                    text = vm.playlist[vm.currentPlaylistIndex].title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(20.dp),
                    color = Color.White
                )
                Text(
                    text = vm.playlist[vm.currentPlaylistIndex].artist,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(20.dp),
                    color = Color.White
                )
            }

        }
        MusicControls(songIsPlaying = vm.songIsPlaying.value, vm)
    }
}

@Composable
fun MusicControls(songIsPlaying: Boolean, vm: MusicPlayerViewModel) {
    var sliderPosition by vm.sliderValue
    Column() {
        Slider(
            value = sliderPosition.toFloat(),
            onValueChange = {
                sliderPosition = it.toInt()
                vm.seek()
            },
            valueRange = 0f..vm.currentSongLength.value,
            colors = SliderDefaults.colors(
                thumbColor = vm.accentColor.value,
                activeTrackColor = (vm.accentColor.value.copy(.85f))
            ),
            modifier = Modifier.padding(20.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(vm.bgColor.value),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            MusicControlButton(
                previous,
                "previous song",
                Modifier.weight(1f),
                vm.accentColor.value
            ) {
                vm.previousSong()
            }
            MusicControlButton(
                skipBackwards,
                "skip 10 seconds backwards",
                Modifier.weight(1f),
                vm.accentColor.value
            ) {
                vm.skipBackwards()
            }
            when (songIsPlaying) {
                true -> MusicControlButton(
                    pause,
                    "pause song",
                    Modifier.weight(1f),
                    vm.accentColor.value
                ) {
                    vm.pauseOrPlaySong()
                }
                else -> MusicControlButton(
                    play,
                    "play song",
                    Modifier.weight(1f),
                    vm.accentColor.value
                ) {
                    vm.pauseOrPlaySong()
                }
            }
            MusicControlButton(
                skipForward,
                "skip 10 seconds forward",
                Modifier.weight(1f),
                vm.accentColor.value
            ) {
                vm.skipForward()
            }
            MusicControlButton(next, "next song", Modifier.weight(1f), vm.accentColor.value) {
                vm.nextSong()
            }
        }
    }

}

@Composable
fun MusicControlButton(
    icon: Int,
    description: String,
    modifier: Modifier,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape = CircleShape)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = description,
            modifier = Modifier
                .height(30.dp)
                .aspectRatio(1f)
                .padding(2.dp),
            tint = color
        )
    }
}