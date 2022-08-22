package io.github.kabirnayeem99.v2_survey.core.ktx

import android.view.View
import android.view.animation.AnimationUtils
import io.github.kabirnayeem99.v2_survey.R

fun View.bounce() {
    val bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_bounce)
    startAnimation(bounceAnimation)
}