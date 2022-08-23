package io.github.kabirnayeem99.v2_survey.core.ktx

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import io.github.kabirnayeem99.v2_survey.BuildConfig
import timber.log.Timber
import java.io.File


fun Fragment.createAndGetTempImageFileUri(): Uri? {
    val authorities = BuildConfig.APPLICATION_ID + ".provider"
    return FileProvider.getUriForFile(
        requireContext(),
        authorities,
        File.createTempFile("survey_question_ans_", ".png", context?.filesDir)
    )
}

fun Fragment.getBitmapFromUri(uri: Uri?): Bitmap? {
    if (uri == null) return null
    return try {
        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
    } catch (e: Exception) {
        Timber.e(e, "Failed to get image from uri -> ${e.localizedMessage}")
        null
    }
}


