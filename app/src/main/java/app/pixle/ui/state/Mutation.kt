package app.pixle.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.pixle.model.api.contracts.Mutable
import app.pixle.model.api.contracts.Queryable
import app.pixle.ui.composition.ConnectionInformation
import app.pixle.ui.composition.rememberConnectionInformation
import com.kazakago.swr.compose.config.SWRMutateConfig
import com.kazakago.swr.compose.config.SWRTriggerConfig
import com.kazakago.swr.compose.state.SWRMutationState
import com.kazakago.swr.compose.useSWRConfig
import com.kazakago.swr.compose.useSWRMutation

@Composable
fun <KEY, ARG, DATA> rememberMutable(
    mutable: Mutable<KEY, ARG, DATA>,
    options: SWRTriggerConfig<KEY, DATA>.() -> Unit = {},
): SWRMutationState<KEY, DATA, ARG> {
    val context = LocalContext.current

    return useSWRMutation(
        key = mutable.key,
        fetcher = { key, arg -> mutable.mutationFn(key, arg, context) },
        options = options
    )
}


@Composable
fun <KEY, DATA> rememberInvalidate(
    queryable: Queryable<KEY, DATA>,
    data: (suspend () -> DATA)? = null,
    options:  SWRMutateConfig<DATA>.() -> Unit = {},
): suspend () -> Unit {
    val config = useSWRConfig<KEY, DATA>()

    return remember(queryable.key, config) {
        val mutate: suspend () -> Unit = {
            config.mutate.invoke(queryable.key, data, options)
        }
        return@remember mutate
    }
}