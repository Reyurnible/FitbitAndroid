package io.github.reyurnible.fitbit.util

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi

object MoshiCreator {
    fun create(): Moshi =
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}
