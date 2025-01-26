package com.bangkit.trashup.ui.maps

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("LocalVariableName")
class NearestTPSPredictor(private val context: Context) {
    private val tpsCoordinates = listOf(
        LatLng(-7.74875, 110.407583),
        LatLng(-7.745361, 110.295694),
        LatLng(-7.738417, 110.289028),
        LatLng(-7.717917, 110.353833),
        LatLng(-7.717917, 110.353833),
        LatLng(-7.619972, 110.38325),
        LatLng(-7.710491, 110.416856),
        LatLng(-7.823889, 110.437194),
        LatLng(-7.761278, 110.251556),
        LatLng(-7.770304, 110.245075),
        LatLng(-7.6865, 110.35725),
        LatLng(-7.665472, 110.37225),
        LatLng(-7.707944, 110.432389),
        LatLng(-7.656111, 110.304389),
        LatLng(-7.666611, 110.315694),
        LatLng(-7.688583, 110.457472),
        LatLng(-7.711306, 110.474556),
        LatLng(-7.705444, 110.465194),
        LatLng(-7.772389, 110.450278),
        LatLng(-7.785944, 110.31),
        LatLng(-7.823889, 110.437083),
        LatLng(-7.731, 110.343306),
        LatLng(-7.682944, 110.293222),
        LatLng(-7.753583, 110.335028),
        LatLng(-7.784889, 110.295611),
        LatLng(-7.682944, 110.293222),
        LatLng(-7.684111, 110.394889),
        LatLng(-7.663278, 110.433667),
        LatLng(-7.771694, 110.24675),
        LatLng(-7.787167, 110.256417),
        LatLng(-7.699833, 110.318833),
        LatLng(-7.717972, 110.291806),
        LatLng(-7.656167, 110.356306),
        LatLng(-7.681657, 110.3448),
        LatLng(-7.7047405, 110.3632736),
        LatLng(-7.739167, 110.402556),
        LatLng(-7.8185, 110.439861),
        LatLng(-7.7500635, 110.4594436),
        LatLng(-7.740333, 110.489472),
        LatLng(-7.701056, 110.444556),
        LatLng(-7.717444, 110.418417),
        LatLng(-7.701778, 110.409972),
        LatLng(-7.8037362, 110.2878224),
        LatLng(-7.723139, 110.313667),
        LatLng(-7.673516, 110.323165),
        LatLng(-7.704528, 110.345778),
        LatLng(-7.623472, 110.37),
        LatLng(-7.723481, 110.41723),
        LatLng(-7.662528, 110.326528),
        LatLng(-7.7165615, 110.3901834),
        LatLng(-7.775389, 110.239361),
        LatLng(-7.648417, 110.398639),
        LatLng(-7.644556, 110.343889),
        LatLng(-7.792722, 110.487417),
        LatLng(-7.816384, 110.50384),
        LatLng(-7.716556, 110.25575),
        LatLng(-7.650972, 110.364472),
        LatLng(-7.663154, 110.42393),
        LatLng(-7.768693, 110.32872),
        LatLng(-7.823889, 110.437194),
        LatLng(-7.786006, 110.421816),
        LatLng(-7.767528, 110.384478),
        LatLng(-7.755042, 110.412452),
        LatLng(-7.772667, 110.340284),
        LatLng(-7.762583, 110.369672),
        LatLng(-7.766639, 110.350867),
        LatLng(-7.7806944, 110.397867),
        LatLng(-7.719, 110.360867),
        LatLng(-7.7294444, 110.392672),
        LatLng(-7.7958056, 110.3167),
        LatLng(-7.7415, 110.3792),
        LatLng(-7.7722222, 110.448228),
        LatLng(-7.684219, 110.352216),
        LatLng(-7.7073969, 110.325015),
        LatLng(-7.76003, 110.398132),
        LatLng(-7.745071, 110.409017),
        LatLng(-7.7814978, 110.4387952),
        LatLng(-7.681413, 110.3250825),
        LatLng(-7.7515455, 110.3653435),
        LatLng(-7.744078, -7.744078)
    )


    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    fun findNearestTPS(currentLocation: DoubleArray): LatLng? {
        try {
            if (currentLocation[0] !in -90.0..90.0 || currentLocation[1] !in -180.0..180.0) {
                Log.e("NearestTPSPredictor", "Lokasi tidak valid: ${currentLocation[0]}, ${currentLocation[1]}")
                return null
            }

            var nearestTPS: LatLng? = null
            var minDistance = Double.MAX_VALUE

            for (tps in tpsCoordinates) {
                val distance = calculateDistance(
                    currentLocation[0], currentLocation[1], tps.latitude, tps.longitude
                )

                if (distance < minDistance) {
                    minDistance = distance
                    nearestTPS = tps
                }
            }

            return nearestTPS
        } catch (e: Exception) {
            Log.e("NearestTPSPredictor", "Error calculating nearest TPS", e)
            return null
        }
    }
}
