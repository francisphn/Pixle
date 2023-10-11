package app.pixle.asset

import android.Manifest
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy

val advertisingOptions = AdvertisingOptions.Builder()
    .setStrategy(Strategy.P2P_POINT_TO_POINT)
    .build()

val discoveryOptions = DiscoveryOptions.Builder()
    .setStrategy(Strategy.P2P_POINT_TO_POINT)
    .build()

val requiredPermissions = listOf(
    Manifest.permission.ACCESS_WIFI_STATE,
    Manifest.permission.CHANGE_WIFI_STATE,
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.BLUETOOTH_ADVERTISE,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.NEARBY_WIFI_DEVICES,
)