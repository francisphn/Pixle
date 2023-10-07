package app.pixle.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.composable.RandomTextmojiMessage
import app.pixle.ui.composable.SmallButton
import app.pixle.ui.composable.profile.About
import app.pixle.ui.composable.profile.ForfeitToday
import app.pixle.ui.composable.profile.History
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navBuilder: NavigationBuilder) {
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
            },
            actions = {
                ForfeitToday()

                IconButton(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .background(
                            MaterialTheme.colorScheme.onBackground.opacity(0.8f),
                            CircleShape
                        )
                        .size(30.dp),
                    onClick = {
                    },
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "settings",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable {
                                navBuilder.navigateToPreferences()
                            }
                    )
                }
            },
            modifier = Modifier.padding(top = 10.dp)
        )

        About()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            History()
        }
    }
}