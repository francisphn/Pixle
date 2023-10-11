package app.pixle.ui.state

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
                cont.resume(location)
            }
            .addOnCanceledListener {
                cont.resume(null)
            }
            .addOnFailureListener {
                cont.cancel(null)
            }
    }

    suspend fun lastLocationDisplayName(): String {
        val location = lastLocation() ?: return "Unknown location"
        val geocoder = Geocoder(context, Locale.UK)
        try {
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
        } catch (e: Exception) {
            Log.e("pixle:debug", "Cannot get location display name", e)
            return "Unknown location"
        }
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