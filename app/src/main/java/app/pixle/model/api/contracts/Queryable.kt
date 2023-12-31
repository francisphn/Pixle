package app.pixle.model.api.contracts

import android.content.Context

interface Queryable<TKeys, TData> {
    val key: TKeys
    suspend fun queryFn(keys: TKeys, context: Context): TData
}
