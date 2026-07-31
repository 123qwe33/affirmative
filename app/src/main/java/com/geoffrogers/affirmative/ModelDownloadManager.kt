package com.geoffrogers.affirmative

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.File

class ModelDownloadManager(private val context: Context) {

    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val activeDownloads = mutableMapOf<String, Long>()
    private val knownSizes = mutableMapOf<String, Long>()

    fun modelDir(modelId: String): File = File(context.filesDir, "models/$modelId")

    fun isModelReady(model: VoiceModel): Boolean =
        File(modelDir(model.id), model.onnxFileName).exists()

    fun startDownload(model: VoiceModel): Long {
        val request = DownloadManager.Request(Uri.parse(model.downloadUrl))
            .setTitle(model.displayName)
            .setDestinationInExternalFilesDir(context, null, "${model.id}.tar.bz2")
        val downloadId = downloadManager.enqueue(request)
        activeDownloads[model.id] = downloadId
        knownSizes[model.id] = model.fileSizeBytes
        return downloadId
    }

    fun cancelDownload(modelId: String) {
        downloadManager.remove(activeDownloads[modelId] ?: return)
        activeDownloads.remove(modelId)
    }

    fun isDownloadActive(modelId: String): Boolean = activeDownloads.containsKey(modelId)

    fun getProgress(modelId: String): Int {
        val downloadId = activeDownloads[modelId] ?: return -1
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        return cursor.use {
            if (!it.moveToFirst()) return -1
            val status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) return -1
            val downloaded = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val reported = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val total = if (reported > 0) reported else knownSizes[modelId] ?: 0L
            if (total <= 0) 0 else ((downloaded * 100L) / total).toInt()
        }
    }

    suspend fun extractArchive(model: VoiceModel) = withContext(Dispatchers.IO) {
        val archiveFile = File(context.getExternalFilesDir(null), "${model.id}.tar.bz2")
        val outDir = modelDir(model.id).also { it.mkdirs() }

        archiveFile.inputStream().buffered().use { fileIn ->
            BZip2CompressorInputStream(fileIn).use { bz2In ->
                TarArchiveInputStream(bz2In).use { tarIn ->
                    var entry = tarIn.nextEntry
                    while (entry != null) {
                        // Strip the top-level archive directory (e.g. "vits-piper-en_US-amy-low/")
                        val relativeName = entry.name.substringAfter('/')
                        if (relativeName.isNotEmpty()) {
                            val outFile = File(outDir, relativeName)
                            if (entry.isDirectory) {
                                outFile.mkdirs()
                            } else {
                                outFile.parentFile?.mkdirs()
                                outFile.outputStream().use { tarIn.copyTo(it) }
                            }
                        }
                        entry = tarIn.nextEntry
                    }
                }
            }
        }

        archiveFile.delete()
        activeDownloads.remove(model.id)
    }
}
