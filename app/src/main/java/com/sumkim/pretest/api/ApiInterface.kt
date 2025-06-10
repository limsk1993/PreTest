package com.sumkim.pretest.api

import com.sumkim.pretest.response.SectionProductsResponse
import com.sumkim.pretest.response.SectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("sections")
    fun getSections(
        @Query("page")
        page: Int?
    ): Call<SectionsResponse>

    @GET("section/products")
    fun getSectionProducts(
        @Query("sectionId")
        sectionId: Int?
    ): Call<SectionProductsResponse>
}