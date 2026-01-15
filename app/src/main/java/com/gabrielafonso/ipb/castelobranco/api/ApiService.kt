package com.gabrielafonso.ipb.castelobranco.api

import com.gabrielafonso.ipb.castelobranco.data.model.PraiseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
interface ApiService {
//    @GET("ipbcb/fill-tables/")
    @GET("api/ipbcb/fill-tables/")
    fun fillTables(): Call<PraiseResponse>
}