package io.github.reyurnible.fitbit.entity

enum class Gender {
    Female,
    Male,
    None,
    ;

    companion object {
        fun parse(gender: String): Gender =
            when (gender) {
                "FEMALE" -> Female
                "MALE" -> Male
                else -> None
            }
    }
}
