package io.github.reyurnible.fitbit.auth

enum class FitbitScope {
    Activity,
    Heartrate,
    Location,
    Nutrition,
    Profile,
    Settings,
    Sleep,
    Social,
    Weight
    ;

    companion object {
        fun generateUrlString(scopes: Array<FitbitScope>): String =
            scopes.map { it.name.toLowerCase() }.joinToString(" ")
    }
}
