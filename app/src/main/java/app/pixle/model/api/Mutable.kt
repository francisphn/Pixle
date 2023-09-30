package app.pixle.model.api

import android.content.Context

interface Mutable<TKeys, TArgs, TData> {
    val key: TKeys
    suspend fun mutationFn(keys: TKeys, args: TArgs, context: Context): TData
}