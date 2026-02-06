package com.gabrielafonso.ipb.castelobranco.data.repository

import com.gabrielafonso.ipb.castelobranco.data.api.BackendApi
import com.gabrielafonso.ipb.castelobranco.domain.repository.ProfileRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val api: BackendApi
) : ProfileRepository {

    override suspend fun uploadProfilePhoto(bytes: ByteArray, fileName: String): Result<String?> =
        runCatching {
            val body = bytes.toRequestBody("image/*".toMediaType())
            val part = MultipartBody.Part.createFormData(
                name = "photo",
                filename = fileName,
                body = body
            )

            val response = api.uploadProfilePhoto(part)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                throw Exception(errorBody ?: "HTTP ${response.code()}")
            }

            response.body()?.photoUrl
        }

    override suspend fun deleteProfilePhoto(): Result<Unit> =
        runCatching {
            val response = api.deleteProfilePhoto()
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                throw Exception(errorBody ?: "HTTP ${response.code()}")
            }
            Unit
        }
}
