package com.sumkim.pretest.repository

import android.content.Context
import com.sumkim.pretest.api.ApiResult
import com.sumkim.pretest.request.SectionProductsRequest
import com.sumkim.pretest.request.SectionsRequest
import com.sumkim.pretest.response.SectionProductsResponse
import com.sumkim.pretest.response.SectionsResponse

interface ApiRepository {
    suspend fun requestGetSections(
        context: Context,
        sectionsRequest: SectionsRequest,
    ): ApiResult<SectionsResponse>

    suspend fun requestGetSectionProducts(
        context: Context,
        sectionProductsRequest: SectionProductsRequest,
    ): ApiResult<SectionProductsResponse>
}