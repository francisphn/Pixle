package app.pixle.ui.composable.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.R
import app.pixle.model.api.AttemptsHistory
import app.pixle.ui.modifier.bottomBorder
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberQueryable
import app.pixle.ui.theme.Manrope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

@Composable
fun About() {
    val (history, _) = rememberQueryable(AttemptsHistory)

    // Profile picture and edit button
    Row(
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://night.saturday.fitness/matthew.png")
                .transformations(CircleCropTransformation()).build(),
            contentDescription = "profile",
            modifier = Modifier.size(56.dp)
        )

        EditProfile()
    }


    // Player info
    Column(
        modifier = Modifier
            .bottomBorder(
                1.dp, MaterialTheme.colorScheme.onBackground.opacity(0.125f)
            )
            .padding(top = 10.dp)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = stringResource(R.string.initial_player_name),
            fontFamily = Manrope,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.initial_player_bio),
            fontFamily = Manrope,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onBackground.opacity(0.5f),
            fontStyle = FontStyle.Italic
        )


        // Short stats
        Row(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "${history?.size ?: 0}",
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.games_played),
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "${history?.filter{ each -> each.second.any { it.isWinningAttempt } }?.size ?: 0}",
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.games_won),
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}