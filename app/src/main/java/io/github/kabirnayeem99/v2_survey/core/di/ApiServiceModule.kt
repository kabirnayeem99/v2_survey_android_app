package io.github.kabirnayeem99.v2_survey.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.kabirnayeem99.v2_survey.BuildConfig
import io.github.kabirnayeem99.v2_survey.data.service.SurveyApiService
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val BASE_URL: String = "https://example-response.herokuapp.com/"


@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideLogInterceptor(@ApplicationContext context: Context): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE

        val cache = Cache(
            directory = File(context.cacheDir, "survey_server_cache"),
            maxSize = 5L * 1024L * 1024L
        )

        return OkHttpClient.Builder()
            .cache(cache)
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideSurveyApiService(retrofit: Retrofit): SurveyApiService =
        retrofit.create(SurveyApiService::class.java)
}