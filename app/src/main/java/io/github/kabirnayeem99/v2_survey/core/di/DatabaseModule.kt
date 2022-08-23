package io.github.kabirnayeem99.v2_survey.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.kabirnayeem99.v2_survey.data.service.db.AnsweredSurveyDao
import io.github.kabirnayeem99.v2_survey.data.service.db.SurveyDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SurveyDatabase {
        return Room.databaseBuilder(
            context,
            SurveyDatabase::class.java,
            "survey_local_caching"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideAnsweredSurveyDao(db: SurveyDatabase): AnsweredSurveyDao {
        return db.getAnsweredSurveyDao()
    }
}