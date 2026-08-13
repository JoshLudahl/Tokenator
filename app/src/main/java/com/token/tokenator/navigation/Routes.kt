package com.token.tokenator.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Main : Route

    @Serializable
    data object SavedToken : Route

    @Serializable
    data object AddPassword : Route

    @Serializable
    data class PasswordDetail(
        val id: Int,
    ) : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Security : Route

    @Serializable
    data object PrivacyPolicy : Route
}
