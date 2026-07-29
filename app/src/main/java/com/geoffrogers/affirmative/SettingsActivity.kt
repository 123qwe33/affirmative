package com.geoffrogers.affirmative

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.slider.Slider
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val slider = findViewById<Slider>(R.id.slider_speech_rate)
        val label = findViewById<TextView>(R.id.tv_speech_rate_value)

        slider.value = prefs.getFloat(KEY_SPEECH_RATE, 1.0f)
        label.text = formatRate(slider.value)

        slider.addOnChangeListener { _, value, _ ->
            label.text = formatRate(value)
            prefs.edit().putFloat(KEY_SPEECH_RATE, value).apply()
        }
    }

    private fun formatRate(value: Float) = "%.1fx".format(value)

    companion object {
        const val PREFS_NAME = "affirmative_prefs"
        const val KEY_SPEECH_RATE = "speech_rate"
    }
}
