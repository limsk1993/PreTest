package com.sumkim.pretest.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumkim.pretest.MainEvent
import com.sumkim.pretest.api.onError
import com.sumkim.pretest.api.onSuccess
import com.sumkim.pretest.repository.ApiRepository
import com.sumkim.pretest.request.SectionProductsRequest
import com.sumkim.pretest.request.SectionsRequest
import com.sumkim.pretest.response.SectionInfo
import com.sumkim.pretest.response.SectionProduct
import com.sumkim.pretest.wishDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ar: ApiRepository
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val dataStore = context.wishDataStore
    private val wishKey = stringSetPreferencesKey("wished_product_ids")

    private val _wishedIds = MutableStateFlow<Set<String>>(emptySet())
    val wishedIds: StateFlow<Set<String>> = _wishedIds

    private val _eventChannel = Channel<MainEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _sectionInfos: MutableStateFlow<List<SectionInfo>?> = MutableStateFlow(listOf())
    val sectionInfos: StateFlow<List<SectionInfo>?> = _sectionInfos.asStateFlow()

    private val _nextPage: MutableStateFlow<Int?> = MutableStateFlow(null)
    val nextPage: StateFlow<Int?> = _nextPage.asStateFlow()

    val sectionProducts = mutableStateMapOf<Int, List<SectionProduct>>()

    init {
        viewModelScope.launch {
            dataStore.data.map { it[wishKey] ?: emptySet() }.collect { id -> _wishedIds.value = id }
        }
    }

    fun getSection() = viewModelScope.launch {
        ar.requestGetSections(
            context,
            SectionsRequest(nextPage.value)
        ).onSuccess {
            for (sectionInfo in it.sectionInfos ?: listOf()) {
                sectionInfo.id?.let { id ->
                    getSectionProducts(id)
                }
            }

            _sectionInfos.value = it.sectionInfos
            _nextPage.value = it.paging?.nextPage
            _isLoading.value = false
        }.onError {
            _eventChannel.send(MainEvent.Toast(it))
        }
    }

    private fun getSectionProducts(sectionId: Int) = viewModelScope.launch {
        ar.requestGetSectionProducts(
            context,
            SectionProductsRequest(sectionId),
        ).onSuccess {
            sectionProducts[sectionId] = it.sectionProducts ?: listOf()
        }.onError {
            _eventChannel.send(MainEvent.Toast(it))
        }
    }

    fun toggleWish(id: String) = viewModelScope.launch {
        dataStore.edit { prefs ->
            val current = prefs[wishKey]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(id)) {
                current.remove(id)
            } else {
                current.add(id)
            }
            prefs[wishKey] = current
        }
    }

    // 새로고침이 nextPage까지 초기화하는지 확인되지 않아 일단 getSection만 호출하여 계속 상품이 변경되도록 한다.
    fun refresh() = viewModelScope.launch {
        _isLoading.value = true
        getSection()
    }
}