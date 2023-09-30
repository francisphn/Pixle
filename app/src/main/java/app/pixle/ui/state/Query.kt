package app.pixle.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import app.pixle.model.dto.Queryable
import com.kazakago.swr.compose.config.SWRConfig
import com.kazakago.swr.compose.preload.SWRPreload
import com.kazakago.swr.compose.state.SWRState
import com.kazakago.swr.compose.useSWR
import com.kazakago.swr.compose.useSWRPreload
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

@Composable
fun <KEY, DATA> rememberQueryable(
    queryable: Queryable<KEY, DATA>,
    scope: CoroutineScope? = null,
    options: SWRConfig<KEY, DATA>.() -> Unit = {},
): SWRState<KEY, DATA> {
    val context = LocalContext.current

    return useSWR(
        key = queryable.key,
        fetcher = { queryable.queryFn(it, context) },
        scope = scope,
        options = options
    )
}

@Composable
fun <KEY, DATA> rememberQueryablePreload(
    queryable: Queryable<KEY, DATA>,
): SWRPreload {
    val context = LocalContext.current

    return useSWRPreload(
        key = queryable.key,
        fetcher = { queryable.queryFn(it, context) },
    )
}