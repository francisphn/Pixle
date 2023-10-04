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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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
import app.pixle.model.entity.AppPreferences.Companion.DEFAULT_GAME_MODE
import app.pixle.model.entity.AppPreferences.Companion.DEFAULT_SENSITIVITY
import app.pixle.model.entity.GameMode
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.modifier.opacity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Preferences(navBuilder: NavigationBuilder) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val dataStore = AppPreferences.getInstance(context)
    val gameModePreference by dataStore.getGameModePreference.collectAsState(initial = DEFAULT_GAME_MODE)

    var sensitivitySelection by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(Unit) {
        dataStore.getSensitivityPreference.collect {
            sensitivitySelection = it
        }
    }

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

                    GameMode.values().forEach { option ->

                        Row(modifier = Modifier
                            .padding(8.dp)
                            .background(
                                if (gameModePreference == option) Color(0xFFD3D3D3) else Color(
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
                                scope.launch {
                                    dataStore.saveGameModePreference(option)
                                }
                            }

                        ) {
                            Text(text = option.toString(),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }


                    }

                }


            }
        }

        if (sensitivitySelection != null) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Sensitivity")
                    Slider(
                        modifier = Modifier.padding(16.dp),
                        value = sensitivitySelection!!,
                        onValueChange = {
                            sensitivitySelection = it

                            scope.launch {
                                dataStore.saveSensitivityPreference(sensitivitySelection!!)
                            }
                        },
                        valueRange = 0.1f..0.9f,
                        steps = 7
                    )
                    Text(text = "Current Sensitivity:",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                    Text(text = String.format("%.1f", sensitivitySelection!!),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp))
                }
            }
        }
    }
}