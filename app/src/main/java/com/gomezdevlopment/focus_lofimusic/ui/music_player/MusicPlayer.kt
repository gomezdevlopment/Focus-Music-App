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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.gomezdevlopment.focus_lofimusic.ui.Navigation
import com.gomezdevlopment.focus_lofimusic.viewModels.SettingsViewModel.Companion.useDynamicColorsSetting
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun MusicPlayerScreen(vm: MusicPlayerViewModel, navController: NavController) {
    val bgColor by vm.bgColor
    val songArt by vm.songArtBitmap
    val systemUiController = rememberSystemUiController()
    val useDynamicColors by remember{ useDynamicColorsSetting}
    systemUiController.setSystemBarsColor(if (useDynamicColors) bgColor else MaterialTheme.colorScheme.background)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (useDynamicColors) bgColor else MaterialTheme.colorScheme.background)
            .padding(25.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(), horizontalArrangement = Arrangement.End) {
            Icon(imageVector = ImageVector.vectorResource(id = menu),
                contentDescription = "Menu",
                modifier = Modifier
                    .height(20.dp)
                    .clickable {
                        navController.navigate("settings")
                    }
                ,
                tint = if (useDynamicColors) vm.accentColor.value else MaterialTheme.colorScheme.primary
                )
        }

        BoxWithConstraints(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(if (useDynamicColors) bgColor else MaterialTheme.colorScheme.background),
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if(songArt != null){
                    Image(
                        painter = rememberAsyncImagePainter(model = songArt),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth(.9f)
                    )
                }
                Text(
                    text = vm.playlist[vm.currentPlaylistIndex].title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center,
                    color = if (useDynamicColors) Color.White else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = vm.playlist[vm.currentPlaylistIndex].artist,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center,
                    color = if (useDynamicColors) Color.White else MaterialTheme.colorScheme.onBackground
                )
            }

        }
        MusicControls(songIsPlaying = vm.songIsPlaying.value, vm, useDynamicColors)
    }
}

@Composable
fun MusicControls(songIsPlaying: Boolean, vm: MusicPlayerViewModel, useDynamicColors: Boolean) {
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
                thumbColor = if (useDynamicColors) vm.accentColor.value else MaterialTheme.colorScheme.primary,
                activeTrackColor = if (useDynamicColors) vm.accentColor.value.copy(.85f) else MaterialTheme.colorScheme.primary.copy(.85f)
            ),
            modifier = Modifier.padding(20.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(if (useDynamicColors) vm.bgColor.value else MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            MusicControlButton(
                previous,
                "previous song",
                Modifier.weight(1f),
                if (useDynamicColors) vm.accentColor.value else MaterialTheme.colorScheme.primary
            ) {
                vm.previousSong()
            }
            MusicControlButton(
                skipBackwards,
                "skip 10 seconds backwards",
                Modifier.weight(1f),
                if (useDynamicColors) vm.accentColor.value else MaterialTheme.colorScheme.primary
            ) {
                vm.skipBackwards()
            }
            when (songIsPlaying) {
                true -> MusicControlButton(
                    pause,
                    "pause song",
                    Modifier.weight(1f),
                    if (useDynamicColors) vm.accentColor.value else MaterialTheme.colorScheme.primary
                ) {
                    vm.pauseOrPlaySong()
                }
                else -> MusicControlButton(
                    play,
                    "play song",
                    Modifier.weight(1f),
                    if (useDynamicColors) vm.accentColor.value else MaterialTheme.colorScheme.primary
                ) {
                    vm.pauseOrPlaySong()
                }
            }
            MusicControlButton(
                skipForward,
                "skip 10 seconds forward",
                Modifier.weight(1f),
                if (useDynamicColors) vm.accentColor.value else MaterialTheme.colorScheme.primary
            ) {
                vm.skipForward()
            }
            MusicControlButton(next, "next song", Modifier.weight(1f), if (useDynamicColors) vm.accentColor.value else MaterialTheme.colorScheme.primary) {
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