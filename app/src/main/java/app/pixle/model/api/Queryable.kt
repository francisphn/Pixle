package app.pixle.model.api

import android.content.Context

interface Queryable<TKeys, TData> {
    val key: TKeys
    suspend fun queryFn(keys: TKeys, context: Context): TData
}
