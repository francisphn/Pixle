package app.pixle.ui.composable.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.pixle.R
import app.pixle.ui.composable.SmallButton
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberPreferences
import app.pixle.ui.theme.Manrope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile() {
    val (isEditing, setIsEditing) = rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scope = rememberCoroutineScope()
    val preferences = rememberPreferences()
    val playerName by preferences.getPlayerName.collectAsState(initial = stringResource(R.string.initial_player_name))
    val playerBio by preferences.getPlayerBio.collectAsState(initial = "")

    var name by rememberSaveable { mutableStateOf(playerName) }
    var bio by rememberSaveable { mutableStateOf(playerBio) }

    LaunchedEffect(playerBio, playerName) {
        name = playerName
        bio = playerBio
    }

    SmallButton(
        label = stringResource(R.string.edit_profile),
        onClick = {
            setIsEditing(true)
        }
    )

    if (isEditing) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .size(384.dp)
                .zIndex(40f),
            sheetState = sheetState,
            onDismissRequest = { setIsEditing(false) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.edit_profile),
                    fontFamily = Manrope,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.player_name),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.opacity(0.8f),
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = name,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.initial_player_name),
                                fontFamily = Manrope,
                                color = MaterialTheme.colorScheme.onSurface.opacity(0.6f),
                                fontStyle = FontStyle.Italic
                            )
                        },
                        onValueChange = {
                            if (it.length <= 32) {
                                name = it
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface.opacity(0.25f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.opacity(0.25f),
                        ),
                        textStyle = TextStyle(
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                        )
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.player_bio),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.opacity(0.8f),
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = bio,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.initial_player_bio),
                                fontFamily = Manrope,
                                color = MaterialTheme.colorScheme.onSurface.opacity(0.45f),
                                fontStyle = FontStyle.Italic
                            )
                        },
                        onValueChange = {
                            if (it.length <= 256) {
                                bio = it
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface.opacity(0.25f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.opacity(0.25f),
                        ),
                        textStyle = TextStyle(
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable {
                            scope
                                .launch {
                                    preferences.savePlayerName(name)
                                    preferences.savePlayerBio(bio)
                                    sheetState.hide()
                                }
                                .invokeOnCompletion {
                                    setIsEditing(false)
                                }
                        }
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.confirm),
                        fontFamily = Manrope,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}