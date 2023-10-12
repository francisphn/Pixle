package app.pixle.ui.composition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import kotlin.random.Random

data class ConnectionInformation(
    val endpoints: EndpointInformation,
    val setEndpoints: (EndpointInformation) -> Unit = {}
) : androidx.compose.runtime.State<ConnectionInformation.EndpointInformation> {
    override val value: EndpointInformation
        get() = endpoints

    data class EndpointInformation(
        val thisEndpointNumericId: String,
        var otherEndpointNumericId: String? = null,
        var otherEndpointReadableId: String? = null,
        var connectionState: ConnectionState = ConnectionState.NOT_CONNECTED
    )

    enum class ConnectionState {
        ACTIVELY_DISCOVERING,
        SENDING_REQUEST,
        WAITING_FOR_REQUEST,
        CONNECTING,
        CONNECTED,
        PAIRED_TWICEDOWN,
        NOT_CONNECTED
    }
}

private val localNearbyConnections = compositionLocalOf<ConnectionsClient> {
    error("No Nearby Connections context provided")
}

private val localConnectionInformation = compositionLocalOf<ConnectionInformation> {
    error("No connection information context provided")
}


@Composable
fun rememberNearbyConnections(): ConnectionsClient {
    return localNearbyConnections.current
}
@Composable
fun rememberConnectionInformation() : ConnectionInformation {
    return localConnectionInformation.current
}


@Composable
fun TwiceDownProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val instance = Nearby.getConnectionsClient(context)

    val (endpointState, setEndpointState) = remember { mutableStateOf(
        ConnectionInformation.EndpointInformation(
            thisEndpointNumericId = Random.nextInt(0, 10000).toString()
        )
    ) }

    val endpoint = remember(endpointState, setEndpointState) {
        ConnectionInformation(endpointState, setEndpointState)
    }

    CompositionLocalProvider(localNearbyConnections provides instance) {
        CompositionLocalProvider(localConnectionInformation provides endpoint) {
            content()
        }
    }
}