package app.pixle.ui.state

import androidx.compose.runtime.Composable
import com.kazakago.swr.compose.config.SWRConfig
import com.kazakago.swr.compose.state.SWRState
import com.kazakago.swr.compose.useSWR
import kotlinx.coroutines.CoroutineScope

@Composable
fun <KEY, DATA> rememberQuery(
    key: KEY?,
    fetcher: (suspend (key: KEY) -> DATA)? = null,
    scope: CoroutineScope? = null,
    options: SWRConfig<KEY, DATA>.() -> Unit = {},
): SWRState<KEY, DATA> {
    return useSWR(key, fetcher, scope, options)
}