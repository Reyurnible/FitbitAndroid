package io.github.reyurnible.fitbit.entity

data class FitbitHeartRateDateActivity(
    val dateTime: String,
    val value: HeartRateActivityValue
) {
    data class HeartRateActivityValue(
        val customHeartRateZones: List<HeartRateZoneValue>,
        val heartRateZones: List<HeartRateZoneValue>,
        val restingHeartRate: Int
    )

    data class HeartRateZoneValue(
        val caloriesOut: Float,
        val max: Int,
        val min: Int,
        val minutes: Int,
        val name: String
    )
}
