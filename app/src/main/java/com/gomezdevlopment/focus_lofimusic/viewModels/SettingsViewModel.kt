package com.gomezdevlopment.focus_lofimusic.viewModels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gomezdevlopment.focus_lofimusic.ui.settings.dynamicTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(val context: Context) : ViewModel() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val THEME = stringPreferencesKey("theme")

    companion object{
        val appTheme: MutableState<String> = mutableStateOf(dynamicTheme)
        val useDynamicColorsSetting: MutableState<Boolean> = mutableStateOf(false)
    }


    suspend fun setTheme(theme: String){
        context.dataStore.edit { settings ->
            settings[THEME] = theme
        }
        appTheme.value = theme
        useDynamicColorsSetting.value = appTheme.value == dynamicTheme
    }

    private suspend fun theme(): String {
        val preferences = context.dataStore.data.first()
        return preferences[THEME] ?: dynamicTheme
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            appTheme.value = theme()
        }
    }
}