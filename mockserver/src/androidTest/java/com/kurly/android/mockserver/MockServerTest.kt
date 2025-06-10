package com.kurly.android.mockserver

import com.kurly.android.mockserver.core.AssetFileProvider
import com.kurly.android.mockserver.core.FileProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
internal class MockServerTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // 디버깅 시 @Inject 구문에서 에러가 발생
    @Inject
    lateinit var fileProvider: FileProvider

    @Before
    fun init() {
        hiltRule.inject()
    }

    // characters 에러가 발생하여 아래의 'resourceFileReadTest' 함수로 대체
//    @Test
//    fun `리소스 파일 읽기 테스트`() {
//        println(fileProvider.getJsonFromAsset("file_read_test.json"))
//    }

    @Test
    fun resourceFileReadTest() {
        println(fileProvider.getJsonFromAsset("file_read_test.json"))
    }
}

// AssetFileProvider를 주입하는 구문 필요하여 작성
@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderModule {
    @Binds
    @Singleton
    abstract fun bindFileProvider(
        assetFileProvider: AssetFileProvider
    ): FileProvider
}
