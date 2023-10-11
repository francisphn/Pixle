package app.pixle.ui.composable.twicedown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.pixle.ui.composition.rememberConnectionInformation
import app.pixle.ui.composition.rememberNearbyConnections
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope

@Composable
fun Unpair(onUnpair: () -> Unit) {

    val (connInfo, setConnInfo) = rememberConnectionInformation()
    val nearby = rememberNearbyConnections()

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = "You're currently paired up.",

            fontFamily = Manrope,
            fontWeight = FontWeight.Bold
        )

        Box(modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onTertiaryContainer)
            .clickable {
                nearby.disconnectFromEndpoint(connInfo.otherEndpointReadableId!!)
                onUnpair()
            }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.opacity(0.25f),
                shape = CircleShape
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(),
            contentAlignment = Alignment.Center) {

            Text(
                text = "Unpair",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface,
            )
        }
    }
}