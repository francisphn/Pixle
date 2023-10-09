package app.pixle.ui.composable.twicedown

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import app.pixle.MainActivity
import app.pixle.R
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.ui.composable.PhotoItem
import app.pixle.ui.composable.PolaroidFrame
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwiceDownSheet(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val advertisingOptions = AdvertisingOptions.Builder()
        .setStrategy(Strategy.P2P_POINT_TO_POINT)
        .build()


    val randomNumber = Random.nextInt(1, 10000).toString()

    var otherDeviceId by remember { mutableStateOf<String?>(null) }
    var otherDeviceName by remember { mutableStateOf<String?>(null) }

    val connectionsClient = Nearby.getConnectionsClient(context)

    val messages = remember { mutableStateListOf<String>() }


    val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(id: String, payload: Payload) {
            Toast.makeText(context, "Receiving a message", LENGTH_SHORT).show()

            payload.asBytes()?.let {
                messages.add(String(it, Charsets.UTF_8))
                Toast.makeText(context, "The message is ${String(it, Charsets.UTF_8)}", Toast.LENGTH_LONG).show()
            }
        }

        override fun onPayloadTransferUpdate(id: String, update: PayloadTransferUpdate) {
            Toast.makeText(context, "Sending/receiving", LENGTH_SHORT).show()
        }
    }

    val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Toast.makeText(context, "Receiving a connection from ${info.endpointName}", Toast.LENGTH_SHORT).show()

            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {

                ConnectionsStatusCodes.STATUS_OK -> {
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                }

                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show()
                }

                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }
    
    val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, endpointInfo: DiscoveredEndpointInfo) {
            Log.d("pixle:debugNearbyConnections", "Endpoint found $endpointId, ${endpointInfo.endpointName}")

            // If endpoint ID of this device is smaller, it should send a request to connect
            // And vice versa, if the endpoint ID of this device is larger, it should wait to accept a request.

            otherDeviceId = endpointInfo.endpointName

            otherDeviceName = endpointId

            if (randomNumber.toInt() < otherDeviceId!!.toInt()) {
                Toast.makeText(context, "This device has a smaller name, so connecting", LENGTH_SHORT).show()

                connectionsClient.requestConnection(
                    "app.pixle",
                    endpointId,
                    connectionLifecycleCallback
                )

                return
            } else {
                Toast.makeText(context, "This device has a larger name, so waiting to be connected", LENGTH_SHORT).show()
            }


        }

        override fun onEndpointLost(endpointId: String) {
            Log.d("pixle:debugNearbyConnections", "Endpoint lost $endpointId")
        }

    }

    val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()



    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val requiredPermissions = listOf(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.NEARBY_WIFI_DEVICES,
    )

    var permissionsState by remember {
        mutableStateOf(
            requiredPermissions.all {
                ActivityCompat.checkSelfPermission(
                    context, it
                ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    LaunchedEffect(Unit, permissionsState) {
        Log.d("pixle:debugNearbyConnections", "User granted all permissions: $permissionsState")

        // log every permission that has been granted and that has not been granted
        requiredPermissions.forEach {
            Log.d("pixle:debugNearbyConnections", "Permission $it granted: ${ActivityCompat.checkSelfPermission(
                context, it
            ) == PackageManager.PERMISSION_GRANTED}")
        }

        if (permissionsState) {


            Log.d("pixle:debugNearbyConnections", "This device ID is $randomNumber" )

            connectionsClient.startAdvertising(
                randomNumber,
                "app.pixle",
                connectionLifecycleCallback,
                advertisingOptions
            )

             connectionsClient.startDiscovery(
                 "app.pixle",
                 endpointDiscoveryCallback,
                 discoveryOptions
             )
        }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        if (isGranted.all { it.value }) {
            permissionsState = true

            onDismiss()
            scope.launch {
                sheetState.hide()
            }

        } else {
            permissionsState = false
        }
    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.90f)
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
                .padding(30.dp)
        ) {
            Text(
                text = "Twice Down",
                fontFamily = Manrope,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Play with a friend",
                fontFamily = Manrope,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            if (permissionsState) {

                Text(text = "You've granted permissions! All your attempts will be forfeit once connected.")

                Column {
                    LazyColumn {
                        items(messages.toList()) { message ->
                            Text(text = message)
                        }
                    }

                    Row {
                        val textState = remember { mutableStateOf(TextFieldValue()) }
                        TextField(
                            value = textState.value,
                            onValueChange = { textState.value = it },
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = {
                             otherDeviceId?.let {
                                 Toast.makeText(context, "Sending ${textState.value.text}", LENGTH_SHORT).show()
                                 val value = textState.value.text.toByteArray(Charsets.UTF_8)
                                 val payload = Payload.fromBytes(value)

                                 Log.d("pixle:debug", "other's device id is $otherDeviceId")

                                 connectionsClient.sendPayload(it, payload)

                                 connectionsClient.sendPayload(otherDeviceName!!, payload)

                             }
                        }) {
                            Text("Send")
                        }
                    }
                }


            } else {
                Text(
                    text = "Permissions",

                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Twice Down Mode requires Pixle to have permission to use your Bluetooth and WiFi connections.",

                    modifier = Modifier.padding(bottom = 30.dp),

                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal
                )

                Box(modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onTertiaryContainer)
                    .clickable {
                        scope.launch {
                            if (permissionsState) {
                                onDismiss()
                                sheetState.hide()
                            } else {
                                permissionsLauncher.launch(requiredPermissions.toTypedArray())
                            }
                        }
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
                        text = "Grant permission",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface,
                    )
                }
            }
        }
    }
}