package com.example.studysmart.di

import com.example.studysmart.data.remote.api.FamousQuoteApi
import com.example.studysmart.data.repo.FamousQuotesRepository
import com.example.studysmart.data.repo.impl.FamousQuotesRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoProviderModule {

    @Provides
    @Singleton
    fun provideFamousQuotesRepository(
        api: FamousQuoteApi
    ): FamousQuotesRepository = FamousQuotesRetrofit(api)
}
