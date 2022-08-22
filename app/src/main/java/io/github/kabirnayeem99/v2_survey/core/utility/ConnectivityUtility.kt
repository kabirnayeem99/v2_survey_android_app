package io.github.kabirnayeem99.v2_survey.core.utility


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import timber.log.Timber
import java.net.InetAddress


class ConnectivityUtility(private val applicationContext: Context) {
    /**
     * Gets the network info
     */
    private fun getNetworkInfo(): NetworkInfo? {
        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    /**
     * Check if there is any connectivity
     */
    fun isConnected(): Boolean {
        val info = getNetworkInfo()
        return info != null && info.isConnected
    }

    /**
     * Checks if the internet is available or not.
     *
     * @return whether internet available or not
     */
    fun isInternetAvailable(): Boolean {
        return try {
            val address: InetAddress = InetAddress.getByName("google.com")
            !address.equals("")
        } catch (e: Exception) {
            Timber.w(e, "Internet is not available -> ${e.message}")
            false
        }
    }
}