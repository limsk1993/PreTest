package com.sumkim.pretest

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sumkim.pretest.repository.ApiRepository
import com.sumkim.pretest.repository.ApiRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Module
@InstallIn(ViewModelComponent::class)
internal class RepositoryModule {
    @Provides
    @ViewModelScoped
    fun apiRepository(): ApiRepository = ApiRepositoryImpl()
}

sealed interface MainEvent {
    data class Toast(val msg: String? = null): MainEvent
}

object Extensions {
    @Composable
    inline fun <reified T> Flow<T>.collectWithLifecycle(
        lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        noinline action: suspend (T) -> Unit
    ) {
        LaunchedEffect(key1 = Unit) {
            lifecycleOwner.lifecycleScope.launch {
                flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect(action)
            }
        }
    }
}

val Context.wishDataStore: DataStore<Preferences> by preferencesDataStore(name = "wish_prefs")