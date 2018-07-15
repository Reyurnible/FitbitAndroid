package io.github.reyurnible.fitbit.entity

data class FitbitSleepActivitySummary(
    val totalMinutesAsleep: Int,
    val totalSleepRecords: Int,
    val totalTimeInBed: Int,
    val stages: SleepStages?
) {
    data class SleepStages(
        val deep: Int,
        val light: Int,
        val rem: Int,
        val wake: Int
    )
}
