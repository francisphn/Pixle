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

data class PlayMode(
    val state: State = State.SINGLE,
    val setState: (State) -> Unit = { }
): androidx.compose.runtime.State<PlayMode.State> {
    enum class State {
        SINGLE,
        TWICE_DOWN
    }

    override val value: State
        get() = state
}

data class ConnectionInformation(
    val endpoints: EndpointInformation,
    val setEndpoints: (EndpointInformation) -> Unit = {}
) : androidx.compose.runtime.State<ConnectionInformation.EndpointInformation> {
    override val value: EndpointInformation
        get() = endpoints

    data class EndpointInformation(
        val thisEndpointNumericId: String,
        val otherEndpointNumericId: String? = null,
        val otherEndpointReadableId: String? = null,
    )
}

private val localNearbyConnections = compositionLocalOf<ConnectionsClient> {
    error("No Nearby Connections context provided")
}

private val localPlayMode = compositionLocalOf { PlayMode() }

private val localConnectionInformation = compositionLocalOf<ConnectionInformation> {
    error("No connection information context provided")
}

@Composable
fun rememberNearbyConnections(): ConnectionsClient {
    return localNearbyConnections.current
}

@Composable
fun rememberPlayMode() : PlayMode {
    return localPlayMode.current
}

@Composable
fun TwiceDownProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val instance = Nearby.getConnectionsClient(context)

    val (state, setState) = remember { mutableStateOf(PlayMode.State.SINGLE) }

    val playMode = remember(state, setState) {
        PlayMode(state, setState)
    }

    val (endpointState, setEndpointState) = remember { mutableStateOf(
        ConnectionInformation.EndpointInformation(
            thisEndpointNumericId = Random.nextInt(0, 10000).toString()
        )
    ) }

    val endpoint = remember(endpointState, setEndpointState) {
        ConnectionInformation(endpointState, setEndpointState)
    }

    CompositionLocalProvider(localNearbyConnections provides instance) {
        CompositionLocalProvider(localPlayMode provides playMode) {
            CompositionLocalProvider(localConnectionInformation provides endpoint) {
                content()
            }
        }
    }
}