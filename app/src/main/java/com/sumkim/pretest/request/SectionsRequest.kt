package com.sumkim.pretest.request

import retrofit2.http.Query

data class SectionsRequest(
    @Query("page")
    val page: Int? = null
)
