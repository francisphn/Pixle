package app.pixle.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.model.entity.AppPreferences
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.modifier.opacity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Preferences(navBuilder: NavigationBuilder) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataStore = AppPreferences(context)
    val gameModePreference = dataStore.getGameModePreference.collectAsState(initial = "")
    val sensitivityPreference = dataStore.getSensitivityPreference.collectAsState(initial = "")

    var sensitivitySelection by remember { mutableFloatStateOf(0.2f) }
    if (sensitivityPreference.value?.equals("") != true) {
        sensitivitySelection= sensitivityPreference.value?.toFloat() ?: 0.2f
    }

    var gameModeHard = (gameModePreference.value?.equals("Hard") ?: true || gameModePreference.value?.equals("") ?: true)
    println(gameModeHard)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .background(
                            MaterialTheme.colorScheme.onBackground.opacity(0.8f),
                            CircleShape
                        )
                        .size(30.dp),
                    onClick = navBuilder.navigateBack,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "close",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .size(18.dp)
                    )
                }
            }
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Text(modifier = Modifier.fillMaxWidth(),
                fontSize = 48.sp,
                textAlign = TextAlign.Center,
                lineHeight = 52.sp,
                text = "Pixle Preferences")
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Game mode")
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround) {
                    Column(modifier = Modifier
                        .padding(8.dp)
                        .background(
                            if (!gameModeHard) Color(0xFFD3D3D3) else Color(
                                0xFFE9E9E9
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = Color.Gray
                        )
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            gameModeHard = false
                            scope.launch {
                                dataStore.saveGameModePreference("Easy")
                            }
                        }
                    ) {
                        Text(text = "Easy mode",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Column(modifier = Modifier
                        .padding(8.dp)
                        .background(
                            if (!gameModeHard) Color(0xFFE9E9E9) else Color(
                                0xFFD3D3D3
                            )
                        )
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.Gray
                        )
                        .weight(1f)
                        .clickable {
                            gameModeHard = true
                            scope.launch {
                                dataStore.saveGameModePreference("Hard")
                            }
                        }
                    ) {
                        Text(text = "Hard mode",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Sensitivity")
                Slider(
                    modifier = Modifier.padding(16.dp),
                    value = sensitivitySelection,
                    onValueChange = {
                            sensitivitySelection = it
                            scope.launch {
                                dataStore.saveSensitivityPreference(sensitivitySelection.toString())
                            }
                        },
                    valueRange = 0.1f..0.9f,
                    steps = 7
                )
                Text(text = "Current Sensitivity:",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
                Text(text = String.format("%.1f", sensitivitySelection),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp))
            }
        }
    }
}