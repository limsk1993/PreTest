package com.sumkim.pretest.repository

import android.content.Context
import com.sumkim.pretest.api.ApiClient
import com.sumkim.pretest.api.ApiInterface
import com.sumkim.pretest.api.requestApi
import com.sumkim.pretest.request.SectionProductsRequest
import com.sumkim.pretest.request.SectionsRequest

class ApiRepositoryImpl: ApiRepository {
    override suspend fun requestGetSections(
        context: Context,
        sectionsRequest: SectionsRequest
    ) = requestApi(
        ApiClient.createService(context, ApiInterface::class.java).getSections(
            sectionsRequest.page
        )
    )

    override suspend fun requestGetSectionProducts(
        context: Context,
        sectionProductsRequest: SectionProductsRequest
    ) = requestApi(
        ApiClient.createService(context, ApiInterface::class.java).getSectionProducts(
            sectionProductsRequest.sectionId
        )
    )
}