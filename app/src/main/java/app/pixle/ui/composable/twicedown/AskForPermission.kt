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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.R
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForPermission(permissionState: MultiplePermissionsState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = stringResource(R.string.permissions),
            fontFamily = Manrope,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )

        Text(
            text = stringResource(R.string.permissions_twicedown_desc),
            modifier = Modifier.padding(bottom = 30.dp),
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )

        Box(modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onTertiaryContainer)
            .clickable {
                permissionState.launchMultiplePermissionRequest()
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
                text = stringResource(R.string.continue_text),
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }
    }
}
