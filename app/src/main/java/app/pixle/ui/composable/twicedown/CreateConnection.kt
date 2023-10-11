package app.pixle.ui.composable.twicedown

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.asset.APP_NAME
import app.pixle.asset.NEARBY_CONN_D_TAG
import app.pixle.asset.advertisingOptions
import app.pixle.asset.discoveryOptions
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
import app.pixle.ui.state.rememberInvalidate
import app.pixle.ui.state.rememberMutable
import app.pixle.ui.theme.Manrope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

@OptIn(ExperimentalPermissionsApi::class)
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
            delay(1500)
            forfeit.invoke(Unit)
        }
    }


    val connectionLifecycleCallback = remember {

        object : ConnectionLifecycleCallback() {

            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                primaryText = "Connecting"
                secondaryText = "Hang in there"

                setConnInfo(connInfo.apply {
                    this.connectionState = ConnectionInformation.ConnectionState.CONNECTING
                })


                nearby.acceptConnection(endpointId,

                    object: PayloadCallback() {
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
                    }

                )
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
                    Log.d(
                        NEARBY_CONN_D_TAG,
                        "This device has a smaller numeric ID (${connInfo.thisEndpointNumericId}),"
                                + " therefore, it is connecting to the other device."
                    )

                    setConnInfo(connInfo.apply {
                        this.connectionState = ConnectionInformation.ConnectionState.SENDING_REQUEST
                    })

                    nearby.requestConnection(APP_NAME, endpointId, connectionLifecycleCallback)

                    return

                } else {

                    Log.d(
                        NEARBY_CONN_D_TAG,
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
            CircularProgressIndicator(Modifier.size(50.dp))
        } else {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Paired up",
                modifier = Modifier.size(50.dp)
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