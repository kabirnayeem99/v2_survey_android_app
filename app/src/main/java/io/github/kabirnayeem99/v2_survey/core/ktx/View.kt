package io.github.kabirnayeem99.v2_survey.core.ktx

import android.view.View
import android.view.animation.AnimationUtils
import io.github.kabirnayeem99.v2_survey.R

/**
 * Animates the view with a bounce animation
 * @receiver View, the view that will be animated.
 */
fun View.bounce() {
    val bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_bounce)
    startAnimation(bounceAnimation)
}

/**
 * Toggles the visibility of the view either to GONE or VISIBle
 *
 * @receiver View, which will be visible or gone
 * @param isVisible Boolean, whether should be visible or not
 */
fun View.goneOrVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}