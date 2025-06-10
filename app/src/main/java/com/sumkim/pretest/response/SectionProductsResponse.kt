package com.sumkim.pretest.response

import com.google.gson.annotations.SerializedName

data class SectionProductsResponse(
    @SerializedName("data")
    val sectionProducts: List<SectionProduct>? = null,
)

data class SectionProduct(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("originalPrice")
    val originalPrice: Int? = null,

    @SerializedName("discountedPrice")
    val discountedPrice: Int? = null,

    @SerializedName("isSoldOut")
    val isSoldOut: Boolean? = null,
)