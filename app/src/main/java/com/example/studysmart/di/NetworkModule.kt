// di/NetworkModule.kt
package com.example.studysmart.di
import com.example.studysmart.data.remote.api.ResourcesService
import com.example.studysmart.data.remote.api.FamousQuoteApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun okHttp(): OkHttpClient = OkHttpClient.Builder().build()
    @Provides @Singleton fun moshi(): Moshi = Moshi.Builder().build()

    @Provides @Singleton
    fun retrofit(ok: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://openlibrary.org/") // 你们定的资源 API 基址
            .client(ok)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun resourcesService(rt: Retrofit): ResourcesService = rt.create(ResourcesService::class.java)


    @Provides @Singleton
    fun quoteRetrofit(ok: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://zenquotes.io/api/")
            .client(ok)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun provideQuoteApi(quoteRetrofit: Retrofit): FamousQuoteApi =
        quoteRetrofit.create(FamousQuoteApi::class.java)
}
