package com.geoffrogers.affirmative

enum class VoiceModelState { NOT_DOWNLOADED, DOWNLOADING, READY }

data class VoiceModel(
    val id: String,
    val displayName: String,
    val state: VoiceModelState
) {
    companion object {
        val CATALOG = listOf(
            VoiceModel("system", "System Default (Android TTS)", VoiceModelState.READY),
            VoiceModel("piper-amy-low", "Amy (Piper VITS)", VoiceModelState.NOT_DOWNLOADED),
            VoiceModel("piper-lessac-low", "Lessac (Piper VITS)", VoiceModelState.NOT_DOWNLOADED),
            VoiceModel("piper-ryan-low", "Ryan (Piper VITS)", VoiceModelState.NOT_DOWNLOADED)
        )
    }
}
