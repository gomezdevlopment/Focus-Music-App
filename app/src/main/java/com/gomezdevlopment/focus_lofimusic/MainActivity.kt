package com.gomezdevlopment.focus_lofimusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gomezdevlopment.focus_lofimusic.ui.Navigation
import com.gomezdevlopment.focus_lofimusic.viewModels.MusicPlayerViewModel
import com.gomezdevlopment.focus_lofimusic.ui.music_player.MusicPlayerScreen
import com.gomezdevlopment.focus_lofimusic.ui.theme.FocusLofiMusicTheme
import com.gomezdevlopment.focus_lofimusic.viewModels.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = MusicPlayerViewModel(this)
        val settingsVM = SettingsViewModel(this)
        setContent {
            FocusLofiMusicTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(musicPlayerViewModel = vm, settingsVM)
                }
            }
        }
    }
}