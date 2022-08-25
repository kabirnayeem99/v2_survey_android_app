package io.github.kabirnayeem99.v2_survey.core.utility

import org.junit.Test
import java.util.*

class UtilityTest {
    @Test
    fun does_toFormattedDate_right_date() {
        val date = Date(1661353394889)
        val formattedDate = date.toFormattedDate()
        assert(formattedDate.isNotBlank())
    }
}