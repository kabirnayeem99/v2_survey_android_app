package io.github.kabirnayeem99.v2_survey.core.utility

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

suspend fun convertContentUriToFile(context: Context, contentUri: Uri): File? {
    try {
        runCatching {
            // Preparing Temp file name
            val fileExtension = getFileExtensionFromUri(context, contentUri)
            val fileName = "survey_answer" + if (fileExtension != null) ".$fileExtension" else ""

            // to avoid disk read violation, we are running it on the IO thread
            return withContext(Dispatchers.IO) {
                // Creating a temporary file in the cache directory
                val tempFile = File(context.cacheDir, fileName)
                tempFile.createNewFile()

                val fileOutputStream = FileOutputStream(tempFile)
                val inputStream = context.contentResolver.openInputStream(contentUri)
                inputStream?.let { stream ->
                    copyInputStreamToOutputStream(
                        stream,
                        fileOutputStream
                    )
                }

                fileOutputStream.flush()

                tempFile
            }

        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to convert input stream to output stream -> ${e.localizedMessage}.")
        return null
    }

    return null
}

/**
 * Gets the file extension from the given URI
 *
 * @param context Context
 * @param uri The uri of the file.
 * @return The file extension.
 */
private fun getFileExtensionFromUri(context: Context, uri: Uri): String? {
    return try {
        val fileType: String? = context.contentResolver.getType(uri)
        MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    } catch (e: Exception) {
        Timber.e(e, "Failed to get file extension -> ${e.localizedMessage}.")
        null
    }
}

/**
 * Copy the contents of the source InputStream to the target OutputStream.
 *
 * @param source The InputStream to copy from.
 * @param target The file to write to.
 */
@Throws(IOException::class)
private fun copyInputStreamToOutputStream(source: InputStream, target: OutputStream) {
    val buf = ByteArray(8192)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}

/**
 * Converts Date to formatted date string
 *
 * @receiver Date, the date that will be converted
 * @return String, date as a formatted string.
 */
fun Date.toFormattedDate(): String {
    return try {
        val pattern = "EEE, MMM dd, yyyy (hh:mm aaa)"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        simpleDateFormat.format(this)
    } catch (e: Exception) {
        Timber.w(e, "Failed to convert long to date -> ${e.localizedMessage}")
        ""
    }
}