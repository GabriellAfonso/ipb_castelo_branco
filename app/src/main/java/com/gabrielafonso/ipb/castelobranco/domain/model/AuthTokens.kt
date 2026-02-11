package com.gabrielafonso.ipb.castelobranco.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(
    @SerialName("access") val access: String,
    @SerialName("refresh") val refresh: String
)