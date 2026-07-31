package com.geoffrogers.affirmative

enum class VoiceModelState { NOT_DOWNLOADED, DOWNLOADING, READY }

data class VoiceModel(
    val id: String,
    val displayName: String,
    val state: VoiceModelState,
    val downloadUrl: String = "",
    val onnxFileName: String = "",
    val configFileName: String = "",
    val fileSizeBytes: Long = 0L
) {
    companion object {
        private const val BASE = "https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models"

        private fun piper(id: String, name: String, file: String, size: Long) = VoiceModel(
            id = id,
            displayName = name,
            state = VoiceModelState.NOT_DOWNLOADED,
            downloadUrl = "$BASE/$file.tar.bz2",
            onnxFileName = "${file.removePrefix("vits-piper-")}.onnx",
            configFileName = "${file.removePrefix("vits-piper-")}.onnx.json",
            fileSizeBytes = size
        )

        val CATALOG = listOf(
            VoiceModel("system", "System Default (Android TTS)", VoiceModelState.READY),

            // ── US English ────────────────────────────────────────────────────────
            piper("piper-amy-low",              "Amy - Low (US)",                   "vits-piper-en_US-amy-low",                  67_095_344L),
            piper("piper-us-amy-medium",        "Amy - Medium (US)",                "vits-piper-en_US-amy-medium",               67_223_746L),
            piper("piper-us-arctic-medium",     "Arctic - Medium (US)",             "vits-piper-en_US-arctic-medium",            80_255_511L),
            piper("piper-us-bryce-medium",      "Bryce - Medium (US)",              "vits-piper-en_US-bryce-medium",             67_278_133L),
            piper("piper-us-danny-low",         "Danny - Low (US)",                 "vits-piper-en_US-danny-low",                67_107_780L),
            piper("piper-us-glados",            "GlaDOS (US)",                      "vits-piper-en_US-glados",                   67_208_137L),
            piper("piper-us-glados-high",       "GlaDOS - High (US)",               "vits-piper-en_US-glados-high",             115_586_982L),
            piper("piper-us-hfc-female-medium", "HFC Female - Medium (US)",         "vits-piper-en_US-hfc_female-medium",        67_228_166L),
            piper("piper-us-hfc-male-medium",   "HFC Male - Medium (US)",           "vits-piper-en_US-hfc_male-medium",          67_214_049L),
            piper("piper-us-joe-medium",        "Joe - Medium (US)",                "vits-piper-en_US-joe-medium",               67_169_394L),
            piper("piper-us-john-medium",       "John - Medium (US)",               "vits-piper-en_US-john-medium",              67_249_181L),
            piper("piper-us-kathleen-low",      "Kathleen - Low (US)",              "vits-piper-en_US-kathleen-low",             67_118_360L),
            piper("piper-us-kristin-medium",    "Kristin - Medium (US)",            "vits-piper-en_US-kristin-medium",           67_259_230L),
            piper("piper-us-kusal-medium",      "Kusal - Medium (US)",              "vits-piper-en_US-kusal-medium",             67_219_292L),
            piper("piper-us-l2arctic-medium",   "L2Arctic - Medium (US)",           "vits-piper-en_US-l2arctic-medium",          80_314_104L),
            piper("piper-lessac-low",           "Lessac - Low (US)",                "vits-piper-en_US-lessac-low",               67_097_098L),
            piper("piper-us-lessac-medium",     "Lessac - Medium (US)",             "vits-piper-en_US-lessac-medium",            67_230_653L),
            piper("piper-us-lessac-high",       "Lessac - High (US)",               "vits-piper-en_US-lessac-high",             115_545_841L),
            piper("piper-us-libritts-high",     "LibriTTS - High (US)",             "vits-piper-en_US-libritts-high",           131_033_598L),
            piper("piper-us-libritts-r-medium", "LibriTTS-R - Medium (US)",         "vits-piper-en_US-libritts_r-medium",        82_038_311L),
            piper("piper-us-ljspeech-medium",   "LJSpeech - Medium (US)",           "vits-piper-en_US-ljspeech-medium",          67_169_893L),
            piper("piper-us-ljspeech-high",     "LJSpeech - High (US)",             "vits-piper-en_US-ljspeech-high",           115_817_679L),
            piper("piper-us-miro-high",         "Miro - High (US)",                 "vits-piper-en_US-miro-high",                67_198_472L),
            piper("piper-us-norman-medium",     "Norman - Medium (US)",             "vits-piper-en_US-norman-medium",            67_203_672L),
            piper("piper-us-reza-ibrahim-medium","Reza Ibrahim - Medium (US)",      "vits-piper-en_US-reza_ibrahim-medium",      67_214_755L),
            piper("piper-ryan-low",             "Ryan - Low (US)",                  "vits-piper-en_US-ryan-low",                 67_100_179L),
            piper("piper-us-ryan-medium",       "Ryan - Medium (US)",               "vits-piper-en_US-ryan-medium",              67_213_100L),
            piper("piper-us-ryan-high",         "Ryan - High (US)",                 "vits-piper-en_US-ryan-high",               115_630_708L),
            piper("piper-us-sam-medium",        "Sam - Medium (US)",                "vits-piper-en_US-sam-medium",               67_249_919L),

            // ── UK English ────────────────────────────────────────────────────────
            piper("piper-gb-alan-low",              "Alan - Low (UK)",                      "vits-piper-en_GB-alan-low",                  67_086_942L),
            piper("piper-gb-alan-medium",           "Alan - Medium (UK)",                   "vits-piper-en_GB-alan-medium",               67_220_121L),
            piper("piper-gb-alba-medium",           "Alba - Medium (UK)",                   "vits-piper-en_GB-alba-medium",               67_212_349L),
            piper("piper-gb-aru-medium",            "Aru - Medium (UK)",                    "vits-piper-en_GB-aru-medium",                80_309_222L),
            piper("piper-gb-cori-medium",           "Cori - Medium (UK)",                   "vits-piper-en_GB-cori-medium",               67_257_412L),
            piper("piper-gb-cori-high",             "Cori - High (UK)",                     "vits-piper-en_GB-cori-high",                115_574_061L),
            piper("piper-gb-dii-high",              "Dii - High (UK)",                      "vits-piper-en_GB-dii-high",                  67_229_442L),
            piper("piper-gb-jenny-dioco-medium",    "Jenny Dioco - Medium (UK)",            "vits-piper-en_GB-jenny_dioco-medium",        67_225_842L),
            piper("piper-gb-miro-high",             "Miro - High (UK)",                     "vits-piper-en_GB-miro-high",                 67_194_499L),
            piper("piper-gb-northern-male-medium",  "Northern English Male - Medium (UK)",  "vits-piper-en_GB-northern_english_male-medium", 67_210_490L),
            piper("piper-gb-semaine-medium",        "Semaine - Medium (UK)",                "vits-piper-en_GB-semaine-medium",            80_241_918L),
            piper("piper-gb-southern-female-low",   "Southern English Female - Low (UK)",   "vits-piper-en_GB-southern_english_female-low",  67_073_626L),
            piper("piper-gb-southern-female-medium","Southern English Female - Medium (UK)","vits-piper-en_GB-southern_english_female-medium",80_275_090L),
            piper("piper-gb-southern-male-medium",  "Southern English Male - Medium (UK)",  "vits-piper-en_GB-southern_english_male-medium", 80_299_212L),
            piper("piper-gb-sweetbbak-amy",         "Sweetbbak Amy (UK)",                   "vits-piper-en_GB-sweetbbak-amy",            115_595_629L),
            piper("piper-gb-vctk-medium",           "VCTK - Medium (UK)",                   "vits-piper-en_GB-vctk-medium",               80_488_085L),
        )
    }
}
