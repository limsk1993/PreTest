package com.sumkim.pretest.request

import retrofit2.http.Query

data class SectionProductsRequest(
    @Query("sectionId")
    val sectionId: Int? = null
)
