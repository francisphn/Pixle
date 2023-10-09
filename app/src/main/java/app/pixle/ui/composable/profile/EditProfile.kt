package app.pixle.ui.composable.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.pixle.R
import app.pixle.ui.composable.SmallButton
import app.pixle.ui.state.rememberPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile() {
    val (isEditing, setIsEditing) = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scope = rememberCoroutineScope()
    val preferences = rememberPreferences()
    val initialName = stringResource(R.string.initial_player_name)
    val playerName by preferences.getPlayerName.collectAsState(initial = initialName)
    val playerBio by preferences.getPlayerBio.collectAsState(initial = stringResource(R.string.initial_player_bio))

    var name by remember { mutableStateOf(initialName) }
    if (playerName !== stringResource(R.string.initial_player_name)) {
        name = playerName
    }
    var bio by remember { mutableStateOf(playerBio) }
    if (playerBio !== stringResource(R.string.initial_player_bio)) {
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
                .fillMaxHeight(0.90f)
                .zIndex(40f),
            sheetState = sheetState,
            onDismissRequest = { setIsEditing(false) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = name,
                        onValueChange = {
                            if (it.length <= 32) {
                                name = it
                            }
                        },
                        label = { Text("Name") },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = bio,
                        onValueChange = {
                            if (it.length <= 256) {
                                bio = it
                            }
                        },
                        label = { Text("Bio") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            scope.launch {
                                preferences.savePlayerName(name)
                                preferences.savePlayerBio(bio)
                                setIsEditing(false)
                            }                        }
                    ) {
                        Text("Save profile")
                    }
                }
            }
        }
    }
}