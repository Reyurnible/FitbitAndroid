package io.github.reyurnible.fitbit.api

data class FitbitErrorResponse(
    val errors: List<FitbitError>
) {
    data class FitbitError(
        val errorType: String,
        val message: String
    )

}
