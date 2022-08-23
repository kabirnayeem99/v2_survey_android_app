package io.github.kabirnayeem99.v2_survey.core.ktx

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}


