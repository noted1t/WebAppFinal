package com.doma.plugins

import kotlinx.serialization.Serializable

val tokenList = arrayListOf<AdminSession>()

@Serializable
data class AdminSession(
    val id: Int,
    val token: String
)