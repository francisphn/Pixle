package app.pixle.ui.composable.twicedown

import android.Manifest
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.JoinLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.pixle.asset.APP_NAME
import app.pixle.asset.NEARBY_CONN_D_TAG
import app.pixle.database.AppPreferences
import app.pixle.lib.GameMode
import app.pixle.lib.stringify
import app.pixle.model.api.AttemptsHistory
import app.pixle.model.api.AttemptsOfToday
import app.pixle.model.api.ConfirmAttempt
import app.pixle.model.api.Forfeit
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.attempt.Attempt
import app.pixle.ui.composition.ConnectionInformation
import app.pixle.ui.composition.GameAnimation
import app.pixle.ui.composition.rememberConnectionInformation
import app.pixle.ui.composition.rememberGameAnimation
import app.pixle.ui.composition.rememberNearbyConnections
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberInvalidate
import app.pixle.ui.state.rememberMutable
import app.pixle.ui.theme.Manrope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

private val advertisingOptions = AdvertisingOptions.Builder()
    .setStrategy(Strategy.P2P_POINT_TO_POINT)
    .build()

private val discoveryOptions = DiscoveryOptions.Builder()
    .setStrategy(Strategy.P2P_POINT_TO_POINT)
    .build()

private val requiredPermissions = listOf(
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TwiceDownSheet(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    val permissionsState = rememberMultiplePermissionsState(permissions = requiredPermissions)

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

            if (!permissionsState.allPermissionsGranted) {
                AskForPermission(permissionsState)
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



//
//    val deviceNumericId = Random.nextInt(1, 10000).toString()
//
//    var otherDeviceId by remember { mutableStateOf<String?>(null) }
//
//    var otherDeviceName by remember { mutableStateOf<String?>(null) }
//
//    val connectionsClient = Nearby.getConnectionsClient(context)
//
//    val messages = remember { mutableStateListOf<String>() }
//
//    val payloadCallback = object : PayloadCallback() {
//        override fun onPayloadReceived(id: String, payload: Payload) {
//            Toast.makeText(context, "Receiving a message", LENGTH_SHORT).show()
//
//            payload.asBytes()?.let {
//                messages.add(String(it, Charsets.UTF_8))
//            }
//        }
//
//        override fun onPayloadTransferUpdate(id: String, update: PayloadTransferUpdate) {
//            Toast.makeText(context, "Sending/receiving", LENGTH_SHORT).show()
//        }
//    }
//
//    val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
//        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
//            Toast.makeText(context, "Receiving a connection from ${info.endpointName}", Toast.LENGTH_SHORT).show()
//
//            connectionsClient.acceptConnection(endpointId, payloadCallback)
//        }
//
//        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
//            when (result.status.statusCode) {
//
//                ConnectionsStatusCodes.STATUS_OK -> {
//                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
//                }
//
//                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
//                    Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show()
//                }
//
//                ConnectionsStatusCodes.STATUS_ERROR -> {
//                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        override fun onDisconnected(endpointId: String) {
//            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
//        }
//    }
//

//
//        override fun onEndpointLost(endpointId: String) {
//            Log.d("pixle:debugNearbyConnections", "Endpoint lost $endpointId")
//        }
//
//    }
//
//    val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
//
//
//
//
//
//
//
//    LaunchedEffect(Unit, permissionsState) {
//        Log.d("pixle:debugNearbyConnections", "User granted all permissions: $permissionsState")
//
//        // log every permission that has been granted and that has not been granted
//        requiredPermissions.forEach {
//            Log.d("pixle:debugNearbyConnections", "Permission $it granted: ${ActivityCompat.checkSelfPermission(
//                context, it
//            ) == PackageManager.PERMISSION_GRANTED}")
//        }
//
//        if (permissionsState) {
//
//
//            Log.d("pixle:debugNearbyConnections", "This device ID is $deviceNumericId" )
//
//
//        }
//    }
////
////    connectionsClient.startAdvertising(
////        deviceNumericId,
////        "app.pixle",
////        connectionLifecycleCallback,
////        advertisingOptions
////    )
////
////    connectionsClient.startDiscovery(
////        "app.pixle",
////        endpointDiscoveryCallback,
////        discoveryOptions
////    )
//
//
//
//    ModalBottomSheet(
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight(0.90f)
//            .zIndex(40f),
//        sheetState = sheetState,
//        onDismissRequest = {
//            scope.launch {
//                onDismiss()
//                sheetState.hide()
//            }
//        },
//    ) {
//
//        Column(
//            Modifier
//                .fillMaxSize()
//        ) {
//            Text(
//                text = "Twice Down",
//                fontFamily = Manrope,
//                fontSize = 18.sp,
//                lineHeight = 28.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.fillMaxWidth(),
//                textAlign = TextAlign.Center
//            )
//
//            Text(
//                text = "Play with a friend",
//                fontFamily = Manrope,
//                fontSize = 18.sp,
//                lineHeight = 28.sp,
//                fontWeight = FontWeight.Normal,
//                modifier = Modifier
//                    .padding(bottom = 30.dp)
//                    .fillMaxWidth(),
//                textAlign = TextAlign.Center
//            )
//
//            if (permissionsState) {
//
//                Text(text = "You've granted permissions! All your attempts will be forfeit once connected.")
//
//                Column {
//                    LazyColumn {
//                        items(messages.toList()) { message ->
//                            Text(text = message)
//                        }
//                    }
//
//                    Row {
//                        val textState = remember { mutableStateOf(TextFieldValue()) }
//                        TextField(
//                            value = textState.value,
//                            onValueChange = { textState.value = it },
//                            modifier = Modifier.weight(1f)
//                        )
//                        Button(onClick = {
//                             otherDeviceId?.let {
//                                 Toast.makeText(context, "Sending ${textState.value.text}", LENGTH_SHORT).show()
//                                 val value = textState.value.text.toByteArray(Charsets.UTF_8)
//                                 val payload = Payload.fromBytes(value)
//
//                                 Log.d("pixle:debug", "other's device id is $otherDeviceId")
//
//                                 connectionsClient.sendPayload(it, payload)
//
//                                 connectionsClient.sendPayload(otherDeviceName!!, payload)
//
//                             }
//                        }) {
//                            Text("Send")
//                        }
//                    }
//                }
//
//
//            } else {
//                Text(
//                    text = "Permissions",
//
//                    fontFamily = Manrope,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Text(
//                    text = "Twice Down Mode requires Pixle to have permission to access Bluetooth, WiFi, and fine-grained locations.",
//
//                    modifier = Modifier.padding(bottom = 60.dp),
//
//                    fontFamily = Manrope,
//                    fontWeight = FontWeight.Normal
//                )
//
//                Box(modifier = Modifier
//                    .clip(CircleShape)
//                    .background(MaterialTheme.colorScheme.onTertiaryContainer)
//                    .clickable {
//                        scope.launch {
//                            if (permissionsState) {
//
//                            } else {
//                                permissionsLauncher.launch(requiredPermissions.toTypedArray())
//                            }
//                        }
//                    }
//                    .border(
//                        width = 1.dp,
//                        color = MaterialTheme.colorScheme.onBackground.opacity(0.25f),
//                        shape = CircleShape
//                    )
//                    .padding(horizontal = 20.dp, vertical = 10.dp)
//                    .fillMaxWidth(),
//                    contentAlignment = Alignment.Center) {
//
//                    Text(
//                        text = "Continue",
//                        fontFamily = Manrope,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.surface,
//                    )
//                }
//            }
//        }
//    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForPermission(permissionState: MultiplePermissionsState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = "Permissions",

            fontFamily = Manrope,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Twice Down Mode requires Pixle to have permission to access Bluetooth, WiFi, and fine-grained location.",

            modifier = Modifier.padding(bottom = 30.dp),

            fontFamily = Manrope,
            fontWeight = FontWeight.Normal
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
                text = "Continue",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface,
            )
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateConnection(permissionState: MultiplePermissionsState, onPair: () -> Unit) {

    val context = LocalContext.current
    val nearby = rememberNearbyConnections()
    val (connInfo, setConnInfo) = rememberConnectionInformation()
    val scope = rememberCoroutineScope()

    val (_, setAnimationState) = rememberGameAnimation()

    var primaryText by remember { mutableStateOf( "Discovering" ) }

    var secondaryText by remember { mutableStateOf<String?>("Please keep other device close") }

    val invalidateToday = rememberInvalidate(AttemptsOfToday)
    val invalidateHistory = rememberInvalidate(AttemptsHistory)

    val (_, _, mutate) = rememberMutable(ConfirmAttempt)


    val (_, _, forfeit) = rememberMutable(Forfeit) {
        onSuccess = { _, _, _ ->
            scope.launch {
                invalidateToday.invoke()
                invalidateHistory.invoke()
            }.invokeOnCompletion {
                scope.launch {
                    AppPreferences
                        .getInstance(context)
                        .saveGameModePreference(GameMode.Easy)

                    setConnInfo(connInfo.apply {
                        this.connectionState = ConnectionInformation.ConnectionState.PAIRED_TWICEDOWN
                    })

                    onPair()
                }
            }
        }
    }

    LaunchedEffect(connInfo.connectionState) {
        if (connInfo.connectionState == ConnectionInformation.ConnectionState.CONNECTED) {
            delay(3000)
            forfeit.invoke(Unit)
        }
    }


    val connectionLifecycleCallback = remember() {
        object : ConnectionLifecycleCallback() {

            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                primaryText = "Connecting"
                secondaryText = "Hang in there"

                setConnInfo(connInfo.apply {
                    this.connectionState = ConnectionInformation.ConnectionState.CONNECTING
                })


                nearby.acceptConnection(endpointId, object: PayloadCallback() {
                    override fun onPayloadReceived(id: String, payload: Payload) {
                        val it = payload.asBytes()?.stringify() ?: return

                        Log.d(NEARBY_CONN_D_TAG, "Receiving attempt...")

                        if (it.startsWith("ATTEMPT|||")) {
                            val attemptJson = it.substringAfter("ATTEMPT|||")


                            val attempt = Json.decodeFromString<Attempt>(attemptJson)
                            Log.d(NEARBY_CONN_D_TAG, "Received attempt is $attempt")

                            runBlocking {
                                mutate(Triple(attempt, Uri.EMPTY, GameMode.Easy))
                                invalidateToday.invoke()
                                invalidateHistory.invoke()
                                val win = attempt.attemptItems.all { it.kind == AtomicAttemptItem.KIND_EXACT }
                                setAnimationState(if (win) GameAnimation.State.WIN else GameAnimation.State.ATTEMPT)
                            }
                        }
                    }

                    override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {}
                })
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {

                    ConnectionsStatusCodes.STATUS_OK -> {
                        primaryText = "Connected"
                        secondaryText = null

                        setConnInfo(connInfo.apply {
                            this.connectionState = ConnectionInformation.ConnectionState.CONNECTED
                        })

                        nearby.stopAdvertising()
                        nearby.stopDiscovery()
                    }

                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        primaryText = "Rejected"
                        secondaryText = null

                        setConnInfo(connInfo.apply {
                            this.connectionState = ConnectionInformation.ConnectionState.NOT_CONNECTED
                        })
                    }

                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        primaryText = "Error"
                        secondaryText = null

                        setConnInfo(connInfo.apply {
                            this.connectionState = ConnectionInformation.ConnectionState.NOT_CONNECTED
                        })
                    }
                }
            }


            override fun onDisconnected(endpointId: String) {
                primaryText = "Disconnected"
                secondaryText = null

                setConnInfo(connInfo.apply {
                    this.connectionState = ConnectionInformation.ConnectionState.NOT_CONNECTED
                })
            }
        }
    }

    val endpointDiscoveryCallback = remember(connectionLifecycleCallback) {
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, endpointInfo: DiscoveredEndpointInfo) {

                Log.d(NEARBY_CONN_D_TAG, "Endpoint found $endpointId, ${endpointInfo.endpointName}")

                // If endpoint ID of this device is smaller, it should send a request to connect
                // And vice versa, if the endpoint ID of this device is larger, it should wait to accept a request.

                setConnInfo(connInfo.apply {
                    this.otherEndpointNumericId = endpointInfo.endpointName
                    this.otherEndpointReadableId = endpointId
                })

                if (connInfo.thisEndpointNumericId.toInt() < connInfo.otherEndpointNumericId!!.toInt()) {
                    Log.d(NEARBY_CONN_D_TAG,
                        "This device has a smaller numeric ID (${connInfo.thisEndpointNumericId}),"
                                + " therefore, it is connecting to the other device."
                    )

                    setConnInfo(connInfo.apply {
                        this.connectionState = ConnectionInformation.ConnectionState.SENDING_REQUEST
                    })

                    nearby.requestConnection(APP_NAME, endpointId, connectionLifecycleCallback)

                    return

                } else {

                    Log.d(NEARBY_CONN_D_TAG,
                        "This device has a larger numeric ID (${connInfo.thisEndpointNumericId}),"
                                + " therefore, it is waiting to be connected by the other device."
                    )

                    setConnInfo(connInfo.apply {
                        this.connectionState = ConnectionInformation.ConnectionState.WAITING_FOR_REQUEST
                    })
                }
            }

            override fun onEndpointLost(endpointId: String) {
                Log.d(NEARBY_CONN_D_TAG, "Endpoint lost $endpointId")

                setConnInfo(connInfo.apply {
                    this.connectionState = ConnectionInformation.ConnectionState.NOT_CONNECTED
                })
            }
        }
    }

    LaunchedEffect(permissionState, endpointDiscoveryCallback) {
        setConnInfo(connInfo.apply {
            this.connectionState = ConnectionInformation.ConnectionState.ACTIVELY_DISCOVERING
        })

        nearby.startAdvertising(
            connInfo.thisEndpointNumericId,
            APP_NAME,
            connectionLifecycleCallback,
            advertisingOptions
        )

        nearby.startDiscovery(
            APP_NAME,
            endpointDiscoveryCallback,
            discoveryOptions
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        if (connInfo.connectionState != ConnectionInformation.ConnectionState.CONNECTED) {
            CircularProgressIndicator()
        } else {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = ""
            )
        }
    }

    Text(
        text = primaryText,
        fontFamily = Manrope,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    secondaryText?.let {
        Text(
            text = it,
            fontFamily = Manrope,
            fontSize = 13.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}