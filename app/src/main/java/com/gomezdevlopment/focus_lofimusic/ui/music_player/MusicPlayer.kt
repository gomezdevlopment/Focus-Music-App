package com.gomezdevlopment.focus_lofimusic.ui.music_player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gomezdevlopment.focus_lofimusic.R
import com.gomezdevlopment.focus_lofimusic.ui.theme.*

@Composable
fun MusicPlayerScreen() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().background(MaterialTheme.colorScheme.primary)) {
            Image(
                painter = painterResource(id = R.drawable.embrace_art),
                contentDescription = "embrace song art",
                Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
        MusicControls(songIsPlaying = true)
    }
}

@Composable
fun MusicControls(songIsPlaying: Boolean) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .background(MaterialTheme.colorScheme.primary)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        MusicControlButton(previous, "previous song", Modifier.weight(1f))
        MusicControlButton(skipBackwards, "skip 10 seconds backwards", Modifier.weight(1f))
        when (songIsPlaying) {
            true -> MusicControlButton(pause, "pause song", Modifier.weight(1f))
            else -> MusicControlButton(play, "play song", Modifier.weight(1f))
        }
        MusicControlButton(skipForward, "skip 10 seconds forward", Modifier.weight(1f))
        MusicControlButton(next, "next song", Modifier.weight(1f))
    }
}

@Composable
fun MusicControlButton(icon: Int, description: String, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
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