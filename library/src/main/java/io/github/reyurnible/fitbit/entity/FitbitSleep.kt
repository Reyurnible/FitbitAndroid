package io.github.reyurnible.fitbit.entity

data class FitbitSleep(
    val dateOfSleep: String,
    val duration: String,
    val efficiency: Int,
    val infoCode: Int,
    val isMainSleep: Boolean,
    val levels: SleepLevel,
    val logId: Long,
    val minutesAfterWakeup: Int,
    val minutesAsleep: Int,
    val minutesAwake: Int,
    val minutesToFallAsleep: Int,
    val startTime: String,
    val endTime: String,
    val timeInBed: Int,
    val type: String
) {
    data class SleepLevel(
        val data: List<SleepData>,
        val shortData: List<SleepData>,
        val summary: List<SleepSummary>
    )

    data class SleepData(
        val dateTime: String,
        val level: String,
        val seconds: Int
    )

    data class SleepSummary(
        val deep: SleepDescription,
        val light: SleepDescription,
        val rem: SleepDescription,
        val wake: SleepDescription
    )

    data class SleepDescription(
        val count: Int,
        val minutes: Int?,
        val seconds: Int?,
        val thirtyDayAvgMinutes: Int
    )
}
