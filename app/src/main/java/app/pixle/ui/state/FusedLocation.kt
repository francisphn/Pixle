package app.pixle.ui.state

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume


data class FusedLocation(
    val context: Context,
    val providerClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    suspend fun lastLocation() = suspendCancellableCoroutine { cont ->
        providerClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(location)
                }
            }
            .addOnCanceledListener {
                cont.cancel()
            }
            .addOnFailureListener {
                cont.cancel()
            }
    }

    suspend fun lastLocationDisplayName(): String {
        val location = lastLocation()
        val geocoder = Geocoder(context, Locale.UK)
        val addresses = suspendCancellableCoroutine { cont ->
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                2,
                cont::resume
            )
        }
        val res = addresses
            .mapNotNull { address ->
                val res = listOf(
                    address.featureName,
                    address.subLocality,
                    address.locality,
                    address.countryName
                )
                    .filter { it != null && it.isNotBlank() }
                    .joinToString(", ")
                return@mapNotNull res.ifBlank { null }
            }
            .lastOrNull()

        return res ?: addresses[0].countryName
    }

}

@Composable
fun rememberFusedLocation(): FusedLocation {
    val context = LocalContext.current
    return remember(context) {
        val provider = LocationServices.getFusedLocationProviderClient(context)
        return@remember FusedLocation(context, provider)
    }
}