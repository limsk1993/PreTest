package com.sumkim.pretest.response

import com.google.gson.annotations.SerializedName

data class SectionsResponse(
    @SerializedName("data")
    val sectionInfos: List<SectionInfo>? = null,

    @SerializedName("paging")
    val paging: Paging? = null
)

data class SectionInfo(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("url")
    val url: String? = null,
)

data class Paging(
    @SerializedName("next_page")
    val nextPage: Int? = null,
)