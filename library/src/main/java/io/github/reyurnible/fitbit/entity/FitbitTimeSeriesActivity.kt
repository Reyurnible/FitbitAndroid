package io.github.reyurnible.fitbit.entity

import com.squareup.moshi.Json

sealed class FitbitTimeSeriesActivity<T>(
    open val activities: List<T>
) {
    data class HeartRate(
        @Json(name = "activities-heart")
        override val activities: List<FitbitHeartRateDateActivity>
    ) : FitbitTimeSeriesActivity<FitbitHeartRateDateActivity>(activities)

}
