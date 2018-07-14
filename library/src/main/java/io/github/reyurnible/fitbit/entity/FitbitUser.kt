package io.github.reyurnible.fitbit.entity

data class FitbitUser(
    val aboutMe : String?,
    val avatar : String?,
    val avatar150 : String?,
    val city : String?,
    val country : String?,
    val dateOfBirth : String?,
    val displayName : String?,
    val distanceUnit : String?,
    val encodedId : String?,
    val foodsLocale : String?,
    val fullName : String?,
    val gender : Gender,
    val glucoseUnit : String?,
    val height : Float?,
    val heightUnit : String?,
    val locale : String?,
    val memberSince : String?,
    val nickname : String?,
    val offsetFromUTCMillis : String?,
    val startDayOfWeek : String?,
    val state : String?,
    val strideLengthRunning : String?,
    val strideLengthWalking : String?,
    val timezone : String?,
    val waterUnit : String?,
    val weight : Float?,
    val weightUnit : String?
) {


}
