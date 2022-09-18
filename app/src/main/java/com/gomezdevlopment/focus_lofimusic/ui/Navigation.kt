package com.gomezdevlopment.focus_lofimusic.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gomezdevlopment.focus_lofimusic.ui.music_player.MusicPlayerScreen
import com.gomezdevlopment.focus_lofimusic.ui.settings.SettingsScreen
import com.gomezdevlopment.focus_lofimusic.viewModels.MusicPlayerViewModel
import com.gomezdevlopment.focus_lofimusic.viewModels.SettingsViewModel


@Composable
fun Navigation(musicPlayerViewModel: MusicPlayerViewModel, settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "musicPlayer") {
        composable("musicPlayer") {
            MusicPlayerScreen(vm = musicPlayerViewModel, navController)
        }
        composable("settings") { SettingsScreen(settingsViewModel) }
    }
}