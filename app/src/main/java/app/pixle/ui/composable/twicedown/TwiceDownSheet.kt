package app.pixle.ui.composable.twicedown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.JoinLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.pixle.R
import app.pixle.asset.requiredPermissions
import app.pixle.ui.composition.ConnectionInformation
import app.pixle.ui.composition.rememberConnectionInformation
import app.pixle.ui.theme.Manrope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TwiceDownSheet(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val permissionsState = rememberMultiplePermissionsState(permissions = requiredPermissions)
    val connInfo = rememberConnectionInformation()

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth()
            .size(450.dp)
            .zIndex(40f),
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch {
                onDismiss()
                sheetState.hide()
            }
        },
    ) {

        Column(
            Modifier
                .fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.JoinLeft,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .size(50.dp),
            )

            Text(
                text = stringResource(R.string.twice_down),
                fontFamily = Manrope,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.twice_down_desc),
                fontFamily = Manrope,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            if (!permissionsState.allPermissionsGranted) {
                AskForPermission(permissionsState)
            } else {
                if (connInfo.endpoints.connectionState == ConnectionInformation.ConnectionState.PAIRED_TWICEDOWN) {
                    Unpair {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                } else {
                    CreateConnection(permissionsState) {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                }
            }
        }
    }
}

