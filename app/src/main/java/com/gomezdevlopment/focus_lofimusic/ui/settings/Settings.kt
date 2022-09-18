package com.gomezdevlopment.focus_lofimusic.ui.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gomezdevlopment.focus_lofimusic.viewModels.SettingsViewModel
import com.gomezdevlopment.focus_lofimusic.viewModels.SettingsViewModel.Companion.appTheme

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val radioOptions = listOf(dynamicTheme, systemTheme)
    val (selectedOption, onOptionSelected) = remember { appTheme }

    LaunchedEffect(key1 = onOptionSelected){
        settingsViewModel.setTheme(selectedOption)
    }

    Column(Modifier.padding(25.dp, 0.dp)) {
        Text(
            text = "Settings",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Theme",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(5.dp)
        )
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                        }
                    )
                    .padding(horizontal = 2.dp),
                verticalAlignment = CenterVertically
            ) {
                val context = LocalContext.current
                RadioButton(
                    selected = (text == selectedOption),
                    modifier = Modifier.padding(2.dp),
                    onClick = {
                        onOptionSelected(text)
                        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
                    }
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}