package com.gabrielafonso.ipb.castelobranco.core.network

import com.gabrielafonso.ipb.castelobranco.data.local.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val tokens = runBlocking { tokenStorage.loadOrNull() }
        val access = tokens?.access

        val request = if (!access.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $access")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}
