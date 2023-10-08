package app.pixle.twicedown

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.pixle.database.AppPreferences
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.flow.collect

class TwiceDown private constructor(
    private val connectionsClient: ConnectionsClient,
    private val appPreferences: AppPreferences
) {
    private val advertisingOptions = AdvertisingOptions.Builder()
        .setStrategy(Strategy.P2P_POINT_TO_POINT)
        .build()


    suspend fun startAdvertising() {
        appPreferences.getUserName.collect { username ->
            connectionsClient.startAdvertising(
                username,
                SERVICE_ID,
                connectionLifecycleCallback,
                advertisingOptions
            )
    }

    companion object {
        @Volatile
        var instance: TwiceDown? = null

        fun getInstance(context: Context): TwiceDown {
            return instance ?: synchronized(this) {
                instance
                    ?: TwiceDown(
                        Nearby.getConnectionsClient(context),
                        AppPreferences.getInstance(context)
                    ).also { instance = it }
            }
        }
    }
}