## Environment

<br />

### Android Studio Ladybug Feature Drop | 2024.2.2
### JDK Version : JetBrains Runtime 17.0.14
### Android Gradle Plugin : 8.8.0
### Gradle Version : 8.10.2

<br />

## MockServer Migration

### build.gradle

```gradle
android {
    // namespace 추가
    namespace "com.kurly.android.mockserver"
    // Compile SDK 32 -> 35로 수정
    compileSdk 35
    
    defaultConfig {
        minSdk 23
        // Target SDK 32 -> 35로 수정
        targetSdk 35
    }
    
    ...
    
    kotlinOptions {
        // JVM 1.8 -> 11로 수정
        jvmTarget = '11'
    }
}

...

dependencies {
    // APP과 Hilt 버전 맞춤
    def hilt_version = libs.versions.hilt.get()
    ...
}
```

### MockServerTest.kt

```kt
// 디버깅 빌드 시 characters 에러가 발생하여 `리소스 파일 읽기 테스트`를 'resourceFileReadTest' 함수명으로 대체
@Test
fun resourceFileReadTest() {
    println(fileProvider.getJsonFromAsset("file_read_test.json"))
}

// 디버깅 빌드 시 Hilt 관련 에러가 발생하여 추가
@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderModule {
    @Binds
    @Singleton
    abstract fun bindFileProvider(
        assetFileProvider: AssetFileProvider
    ): FileProvider
}
```