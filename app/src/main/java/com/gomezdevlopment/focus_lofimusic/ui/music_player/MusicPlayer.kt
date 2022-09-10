package com.gomezdevlopment.focus_lofimusic.ui.music_player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.focus_lofimusic.viewModels.MusicPlayerViewModel
import com.gomezdevlopment.focus_lofimusic.ui.theme.*
import androidx.compose.material3.Slider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun MusicPlayerScreen(vm: MusicPlayerViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(vm.bgColor.value),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Image(
                painter = painterResource(id = vm.currentSongArt.value),
                contentDescription = "embrace song art",
                Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
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
            valueRange = 0f..vm.currentSongLength.value
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            MusicControlButton(previous, "previous song", Modifier.weight(1f)) {
                vm.previousSong()
            }
            MusicControlButton(skipBackwards, "skip 10 seconds backwards", Modifier.weight(1f)) {
                vm.skipBackwards()
            }
            when (songIsPlaying) {
                true -> MusicControlButton(pause, "pause song", Modifier.weight(1f)) {
                    vm.pauseOrPlaySong()
                }
                else -> MusicControlButton(play, "play song", Modifier.weight(1f)) {
                    vm.pauseOrPlaySong()
                }
            }
            MusicControlButton(skipForward, "skip 10 seconds forward", Modifier.weight(1f)) {
                vm.skipForward()
            }
            MusicControlButton(next, "next song", Modifier.weight(1f)) {
                vm.nextSong()
            }
        }
    }

}

@Composable
fun MusicControlButton(icon: Int, description: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = description,
            modifier = Modifier
                .height(30.dp)
                .aspectRatio(1f)
                .padding(5.dp)
        )
    }
}