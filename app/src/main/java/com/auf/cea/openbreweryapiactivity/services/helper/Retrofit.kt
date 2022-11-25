package com.auf.cea.openbreweryapiactivity.services.helper

import com.auf.cea.openbreweryapiactivity.constants.BASE_API_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}