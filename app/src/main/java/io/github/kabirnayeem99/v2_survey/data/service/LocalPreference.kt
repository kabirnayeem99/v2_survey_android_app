package io.github.kabirnayeem99.v2_survey.data.service

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import javax.inject.Inject

class LocalPreference @Inject constructor(private val context: Context) {

    private val prefName = "survey_local_pref_data"

    private var prefs: SharedPreferences? = null

    private val surveyIdListKey = "survey_id_list_name"
    private val gson by lazy { Gson() }

    suspend fun getAllSurveyIds(): List<Long> {
        return coroutineScope {
            try {
                if (prefs == null) prefs =
                    context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                val json = prefs?.getString(surveyIdListKey, "")
                gson.fromJson<List<Long>>(json, object : TypeToken<List<Long>>() {}.type)
                    ?: emptyList()
            } catch (e: Exception) {
                Timber.e(e, "Failed to save survey id names")
                emptyList()
            }
        }
    }

    suspend fun saveSurveyId(surveyId: Long) {
        coroutineScope {
            try {
                val currentList = getAllSurveyIds().toMutableList()
                currentList.add(surveyId)
                val editor = prefs?.edit()
                editor?.putString(surveyIdListKey, gson.toJson(currentList))
                editor?.apply()
            } catch (e: Exception) {
                Timber.e(e, "Failed to save survey id names")
            }
        }
    }
}