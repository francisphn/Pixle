package app.pixle.model.dto

interface Queryable<TKeys, TData> {
    val key: TKeys
    suspend fun queryFn(keys: TKeys): TData
}
