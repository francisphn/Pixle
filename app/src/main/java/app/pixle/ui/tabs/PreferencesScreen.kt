package app.pixle.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Deblur
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.database.AppPreferences
import app.pixle.database.AppPreferences.Companion.DEFAULT_GAME_MODE
import app.pixle.database.AppPreferences.Companion.DEFAULT_SENSITIVITY
import app.pixle.lib.GameMode
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.composable.preferences.Selection
import app.pixle.ui.modifier.bottomBorder
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.ObjectDetectionModel
import app.pixle.ui.state.rememberPreferences
import app.pixle.ui.theme.Manrope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Preferences(navBuilder: NavigationBuilder) {
    val scope = rememberCoroutineScope()
    val preferences = rememberPreferences()
    val gameModePreference by preferences.getGameModePreference.collectAsState(initial = DEFAULT_GAME_MODE)
    val sensitivityPreference by preferences.getSensitivityPreference.collectAsState(initial = DEFAULT_SENSITIVITY)
    val modelPreferences by preferences.getModelPreference.collectAsState(initial = ObjectDetectionModel.EDL1)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Preferences",
                    fontFamily = Manrope,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
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
            },
            modifier = Modifier
                .padding(top = 10.dp)
        )

        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .bottomBorder(
                    1.dp, MaterialTheme.colorScheme.onBackground.opacity(0.125f)
                )
                .fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Game mode",
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.opacity(0.5f),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .padding(bottom = 8.dp)
                    )


                    Selection(
                        label = "Hardcore (Wordle mode)",
                        description = "6 attempts, hints after 3rd attempt",
                        icon = Icons.Filled.Extension,
                        isSelected = gameModePreference == GameMode.Hard,
                        onClick = {
                            scope.launch {
                                preferences.saveGameModePreference(GameMode.Hard)
                            }
                        }
                    )

                    Selection(
                        label = "Endless",
                        description = "Unlimited attempts, hints always available",
                        icon = Icons.Filled.AllInclusive,
                        isSelected = gameModePreference == GameMode.Easy,
                        onClick = {
                            scope.launch {
                                preferences.saveGameModePreference(GameMode.Easy)
                            }
                        }
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .bottomBorder(
                            1.dp, MaterialTheme.colorScheme.onBackground.opacity(0.125f)
                        )
                        .fillMaxWidth()
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Object Detection Sensitivity",
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.opacity(0.5f),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .padding(bottom = 8.dp)
                    )


                    Selection(
                        label = "Lenient (Aggressive)",
                        description = "More detected, but more false positive (≥ 0.2)",
                        icon = Icons.Filled.BlurOn,
                        isSelected = (sensitivityPreference - 0.2f).absoluteValue < 0.01f,
                        onClick = {
                            scope.launch {
                                preferences.saveSensitivityPreference(0.2f)
                            }
                        }
                    )

                    Selection(
                        label = "Medium (Balanced)",
                        description = "Balancing accuracy and quantity (≥ 0.4)",
                        icon = Icons.Filled.Deblur,
                        isSelected = (sensitivityPreference - 0.4f).absoluteValue < 0.01f,
                        onClick = {
                            scope.launch {
                                preferences.saveSensitivityPreference(0.4f)
                            }
                        }
                    )


                    Selection(
                        label = "Strict (Conservative)",
                        description = "Minimising false positives (≥ 0.6)",
                        icon = Icons.Filled.Circle,
                        isSelected = (sensitivityPreference - 0.6f).absoluteValue < 0.01f,
                        onClick = {
                            scope.launch {
                                preferences.saveSensitivityPreference(0.6f)
                            }
                        }
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .bottomBorder(
                            1.dp, MaterialTheme.colorScheme.onBackground.opacity(0.125f)
                        )
                        .fillMaxWidth()
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Machine Learning Model (Experimental)",
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.opacity(0.5f),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .padding(bottom = 8.dp)
                    )


                    Selection(
                        label = "v0",
                        description = "Fastest, but least accurate",
                        icon = Icons.Filled.Bolt,
                        isSelected = modelPreferences.filename == ObjectDetectionModel.EDL0.filename,
                        onClick = {
                            scope.launch {
                                preferences.saveModelPreference(ObjectDetectionModel.EDL0)
                            }
                        }
                    )

                    Selection(
                        label = "v1",
                        description = "Good balance between speed and accuracy",
                        icon = Icons.Filled.WbTwilight,
                        isSelected = modelPreferences.filename == ObjectDetectionModel.EDL1.filename,
                        onClick = {
                            scope.launch {
                                preferences.saveModelPreference(ObjectDetectionModel.EDL1)
                            }
                        }
                    )


                    Selection(
                        label = "v2",
                        description = "Most accurate, but slowest",
                        icon = Icons.Default.Psychology,
                        isSelected = modelPreferences.filename == ObjectDetectionModel.EDL2.filename,
                        onClick = {
                            scope.launch {
                                preferences.saveModelPreference(ObjectDetectionModel.EDL2)
                            }
                        }
                    )
                }
            }
        }
    }
}