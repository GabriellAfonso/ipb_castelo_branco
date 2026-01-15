package com.gabrielafonso.ipb.castelobranco.data.repository

import com.gabrielafonso.ipb.castelobranco.data.model.PraiseResponse
import com.gabrielafonso.ipb.castelobranco.data.model.SongRow
import com.gabrielafonso.ipb.castelobranco.data.model.TableView
import com.gabrielafonso.ipb.castelobranco.api.ApiService
import retrofit2.awaitResponse

class PraiseRepository(private val apiService: ApiService) {

    // Apenas busca os dados da API e retorna a resposta bruta
    suspend fun getPraiseData(): PraiseResponse? {
        val response = apiService.fillTables().awaitResponse()
        return if (response.isSuccessful) response.body() else null
    }
}

