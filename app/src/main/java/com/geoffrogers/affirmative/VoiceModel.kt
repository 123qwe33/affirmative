package com.geoffrogers.affirmative

enum class VoiceModelState { NOT_DOWNLOADED, DOWNLOADING, READY }

data class VoiceModel(
    val id: String,
    val displayName: String,
    val state: VoiceModelState,
    val downloadUrl: String = "",
    val onnxFileName: String = "",
    val configFileName: String = ""
) {
    companion object {
        val CATALOG = listOf(
            VoiceModel("system", "System Default (Android TTS)", VoiceModelState.READY),
            VoiceModel(
                id = "piper-amy-low",
                displayName = "Amy (Piper VITS)",
                state = VoiceModelState.NOT_DOWNLOADED,
                downloadUrl = "https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/vits-piper-en_US-amy-low.tar.bz2",
                onnxFileName = "en_US-amy-low.onnx",
                configFileName = "en_US-amy-low.onnx.json"
            ),
            VoiceModel(
                id = "piper-lessac-low",
                displayName = "Lessac (Piper VITS)",
                state = VoiceModelState.NOT_DOWNLOADED,
                downloadUrl = "https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/vits-piper-en_US-lessac-low.tar.bz2",
                onnxFileName = "en_US-lessac-low.onnx",
                configFileName = "en_US-lessac-low.onnx.json"
            ),
            VoiceModel(
                id = "piper-ryan-low",
                displayName = "Ryan (Piper VITS)",
                state = VoiceModelState.NOT_DOWNLOADED,
                downloadUrl = "https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/vits-piper-en_US-ryan-low.tar.bz2",
                onnxFileName = "en_US-ryan-low.onnx",
                configFileName = "en_US-ryan-low.onnx.json"
            )
        )
    }
}
