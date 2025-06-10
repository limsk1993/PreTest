package com.kurly.android.mockserver.core

interface FileProvider {
    fun getJsonFromAsset(filePath: String): String?
}
