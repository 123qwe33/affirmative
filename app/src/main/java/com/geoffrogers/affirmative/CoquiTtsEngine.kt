package com.geoffrogers.affirmative

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.OfflineTtsConfig
import com.k2fsa.sherpa.onnx.OfflineTtsModelConfig
import com.k2fsa.sherpa.onnx.OfflineTtsVitsModelConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class CoquiTtsEngine(private val modelDir: File, private val model: VoiceModel) {

    private val tts: OfflineTts by lazy {
        val config = OfflineTtsConfig(
            model = OfflineTtsModelConfig(
                vits = OfflineTtsVitsModelConfig(
                    model = File(modelDir, model.onnxFileName).absolutePath,
                    lexicon = "",
                    tokens = File(modelDir, "tokens.txt").absolutePath,
                    dataDir = File(modelDir, "espeak-ng-data").absolutePath,
                ),
                numThreads = 2,
                debug = false,
                provider = "cpu",
            ),
            maxNumSentences = 1,
        )
        OfflineTts(assetManager = null, config = config)
    }

    private var audioTrack: AudioTrack? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentJob: Job? = null

    fun speak(text: String, speechRate: Float, onDone: () -> Unit) {
        currentJob?.cancel()
        currentJob = scope.launch {
            val audio = tts.generate(text = text, sid = 0, speed = speechRate)

            val shorts = ShortArray(audio.samples.size) { i ->
                (audio.samples[i] * Short.MAX_VALUE).toInt().coerceIn(-32768, 32767).toShort()
            }

            audioTrack?.stop()
            audioTrack?.release()

            val bufferSize = AudioTrack.getMinBufferSize(
                audio.sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
            )

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(audio.sampleRate)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setBufferSizeInBytes(shorts.size * 2)
                .build()

            audioTrack = track

            track.write(shorts, 0, shorts.size)
            track.setNotificationMarkerPosition(shorts.size)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(track: AudioTrack) {
                    onDone()
                }
                override fun onPeriodicNotification(track: AudioTrack) {}
            })
            track.play()
        }
    }

    fun stop() {
        currentJob?.cancel()
        currentJob = null
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    fun shutdown() {
        stop()
        scope.cancel()
    }
}
