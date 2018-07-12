package io.github.reyurnible.fitbit

data class FitbitActivity(
    val inMetric: Boolean = false,
//    val json: JSON?,
    val activityId: String?,
    val activityParentId: String?,
    val name: String?,
    val description: String?,
    val calories: Float?,
    // in Miles
    val distance: Float?,
    // in Seconds
    val duration: Float?,
    val hasStartTime: Boolean = false,
    val isFavorite: Boolean = false,
    val logId: String?,
    // in Seconds
    val steps: Int?,
    val startTime: String?
) {
    companion object {
        /// Constants
        private const val FEET_IN_MILE: Float = 5280f
        private const val METERS_IN_MILE: Float = 1609.34f
        private const val KM_IN_MILE: Float = 1.609344f
    }

    val durationInSeconds: Float?
        get() = duration?.let { it / 1000f }

    val durationInMinutes: Float?
        get() = durationInSeconds?.let { it / 60 }

    val distanceKilometers: Float?
        get() = distance?.let { it * KM_IN_MILE }

    val availableInformation: Map<String, Float?>
        get() = mutableMapOf<String, Float?>().apply {
            set("calories", calories)
            if (inMetric) {
                set("distance", distanceKilometers)
                set("pace", kilometerPace)
            } else {
                set("distance", distance)
                set("pace", milePace)
            }
        }

    val milePace: Float?
        get() =
            if (distance?.takeUnless { it == 0F } == null || duration?.takeUnless { it == 0F } == null) null
            else requireNotNull(durationInMinutes) / requireNotNull(distance)

    val kilometerPace: Float?
        get() =
            if (distance?.takeUnless { it == 0F } == null || duration?.takeUnless { it == 0F } == null) null
            else requireNotNull(durationInMinutes) / requireNotNull(distanceKilometers)

    val minutesSecondsString: String?
        get() = duration?.let {
            val minutes: Int = (it / 60f).toInt()
            val seconds: Int = (it % 60f).toInt()
            if (seconds > 9) "$minutes:$seconds"
            else "$minutes):0$seconds"
        }
}
