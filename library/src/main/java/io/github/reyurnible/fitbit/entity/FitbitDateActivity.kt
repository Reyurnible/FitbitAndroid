package io.github.reyurnible.fitbit.entity

data class FitbitDateActivity(
    val activities: List<FitbitActivity>,
    val goals: Goals,
    val summary: Summary
) {
    data class Goals(
        val caloriesOut: Int,
        val distance: Float,
        val floors: Int,
        val steps: Int
    )

    data class Summary(
        val activityCalories: Int,
        val caloriesBMR: Int,
        val caloriesOut: Int,
        val distances: List<Distance>,
        val elevation: Float,
        val fairlyActiveMinutes: Int,
        val floors: Int,
        val lightlyActiveMinutes: Int,
        val marginalCalories: Int,
        val sedentaryMinutes: Int,
        val steps: Int,
        val veryActiveMinutes: Int
    )

    data class Distance(
        val activity: String,
        val distance: Float
    )
}
