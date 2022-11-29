package com.auf.cea.openbreweryapiactivity.services.repository

import com.auf.cea.openbreweryapiactivity.models.OpenBreweryDB
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenBreweryAPI {
    // by_type:
    // GET https://api.openbrewerydb.org/breweries?by_type=micro&per_page=3
    @GET("/breweries")
    suspend fun getBreweryData(
        @Query("by_type") byType:String,
        @Query("page") page: Int,
        @Query("per_page") perPage:Int
    ):Response<OpenBreweryDB>


    // RANDOMIZE:
    //GET https://api.openbrewerydb.org/breweries/random
    @GET("/breweries/random")
    suspend fun getRandomBreweryData(
        @Query("size") size:Int
    ):Response<OpenBreweryDB>
}