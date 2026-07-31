package com.geoffrogers.affirmative

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {

    private lateinit var downloadMgr: ModelDownloadManager
    private lateinit var voiceModelAdapter: VoiceModelAdapter
    private val liveModels: MutableList<VoiceModel> = mutableListOf()
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var voiceSpinner: Spinner

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

        downloadMgr = ModelDownloadManager(this)

        // Build live model list, upgrading state for already-downloaded models
        liveModels.addAll(VoiceModel.CATALOG.map { model ->
            if (model.onnxFileName.isNotEmpty() && downloadMgr.isModelReady(model))
                model.copy(state = VoiceModelState.READY)
            else
                model
        })

        voiceSpinner = findViewById(R.id.spinner_voice)
        val names = liveModels.map { it.displayName }
        spinnerAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
                super.getView(position, convertView, parent).also { applyAlpha(it, position) }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
                super.getDropDownView(position, convertView, parent).also { applyAlpha(it, position) }

            private fun applyAlpha(view: View, position: Int) {
                view.alpha = if (liveModels[position].state == VoiceModelState.NOT_DOWNLOADED) 0.5f else 1.0f
            }
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        voiceSpinner.adapter = spinnerAdapter

        val savedId = prefs.getString(KEY_VOICE_ID, "system")
        val savedIndex = liveModels.indexOfFirst { it.id == savedId }.takeIf { it >= 0 } ?: 0
        voiceSpinner.setSelection(savedIndex)

        voiceSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                prefs.edit().putString(KEY_VOICE_ID, liveModels[position].id).apply()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        val downloadableModels = liveModels.filter { it.id != "system" }.toMutableList()
        voiceModelAdapter = VoiceModelAdapter(downloadableModels) { model ->
            when (model.state) {
                VoiceModelState.NOT_DOWNLOADED -> {
                    downloadMgr.startDownload(model)
                    updateModelState(model.id, VoiceModelState.DOWNLOADING, 0)
                    startPolling(model)
                }
                VoiceModelState.DOWNLOADING -> {
                    downloadMgr.cancelDownload(model.id)
                    updateModelState(model.id, VoiceModelState.NOT_DOWNLOADED)
                }
                VoiceModelState.READY -> {}
            }
        }

        val rv = findViewById<RecyclerView>(R.id.rv_voice_models)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = voiceModelAdapter
    }

    private fun updateModelState(modelId: String, state: VoiceModelState, progressPct: Int = 0) {
        val index = liveModels.indexOfFirst { it.id == modelId }
        if (index >= 0) liveModels[index] = liveModels[index].copy(state = state)
        voiceModelAdapter.updateState(modelId, state, progressPct)
        spinnerAdapter.notifyDataSetChanged()
    }

    private fun startPolling(model: VoiceModel) {
        lifecycleScope.launch {
            while (true) {
                delay(500)
                val pct = downloadMgr.getProgress(model.id)
                if (pct == -1) {
                    if (!downloadMgr.isDownloadActive(model.id)) break
                    withContext(Dispatchers.Main) {
                        updateModelState(model.id, VoiceModelState.DOWNLOADING, 100)
                    }
                    downloadMgr.extractArchive(model)
                    withContext(Dispatchers.Main) {
                        updateModelState(model.id, VoiceModelState.READY)
                        voiceSpinner.adapter?.let { spinnerAdapter.notifyDataSetChanged() }
                    }
                    break
                } else {
                    withContext(Dispatchers.Main) {
                        updateModelState(model.id, VoiceModelState.DOWNLOADING, pct)
                    }
                }
            }
        }
    }

    private fun formatRate(value: Float) = "%.1fx".format(value)

    companion object {
        const val PREFS_NAME = "affirmative_prefs"
        const val KEY_SPEECH_RATE = "speech_rate"
        const val KEY_VOICE_ID = "voice_id"
    }
}
