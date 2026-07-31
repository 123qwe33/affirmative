package com.geoffrogers.affirmative

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.slider.Slider

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val slider = findViewById<Slider>(R.id.slider_speech_rate)
        val label = findViewById<TextView>(R.id.tv_speech_rate_value)

        slider.value = prefs.getFloat(KEY_SPEECH_RATE, 1.0f).coerceIn(slider.valueFrom, slider.valueTo)
        label.text = formatRate(slider.value)

        slider.addOnChangeListener { _, value, _ ->
            label.text = formatRate(value)
            prefs.edit().putFloat(KEY_SPEECH_RATE, value).apply()
        }

        val spinner = findViewById<Spinner>(R.id.spinner_voice)
        val names = VoiceModel.CATALOG.map { it.displayName }
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
                super.getView(position, convertView, parent).also { applyAlpha(it, position) }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
                super.getDropDownView(position, convertView, parent).also { applyAlpha(it, position) }

            private fun applyAlpha(view: View, position: Int) {
                view.alpha = if (VoiceModel.CATALOG[position].state == VoiceModelState.NOT_DOWNLOADED) 0.5f else 1.0f
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val savedId = prefs.getString(KEY_VOICE_ID, "system")
        val savedIndex = VoiceModel.CATALOG.indexOfFirst { it.id == savedId }.takeIf { it >= 0 } ?: 0
        spinner.setSelection(savedIndex)

        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                prefs.edit().putString(KEY_VOICE_ID, VoiceModel.CATALOG[position].id).apply()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun formatRate(value: Float) = "%.1fx".format(value)

    companion object {
        const val PREFS_NAME = "affirmative_prefs"
        const val KEY_SPEECH_RATE = "speech_rate"
        const val KEY_VOICE_ID = "voice_id"
    }
}
