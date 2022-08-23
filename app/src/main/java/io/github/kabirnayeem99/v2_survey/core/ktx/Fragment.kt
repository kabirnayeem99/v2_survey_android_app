package io.github.kabirnayeem99.v2_survey.core.ktx

import android.widget.Toast
import androidx.fragment.app.Fragment
import io.github.kabirnayeem99.v2_survey.presentation.common.UserMessage

fun Fragment.showMessage(message: UserMessage, onMessageShown: (UserMessage) -> Unit) {
    Toast.makeText(context, message.message, Toast.LENGTH_LONG).show()
    onMessageShown(message)
}


