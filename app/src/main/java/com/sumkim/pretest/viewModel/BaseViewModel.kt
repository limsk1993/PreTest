package com.sumkim.pretest.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseViewModel: ViewModel() {
    private val isInit = MutableStateFlow(false)
    fun ensureInit(): Boolean = !isInit.compareAndSet(expect = false, update = true)
}