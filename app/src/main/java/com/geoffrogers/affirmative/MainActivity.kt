package com.geoffrogers.affirmative

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: AffirmationAdapter
    private lateinit var dao: AffirmationDao
    private lateinit var ttsPlayer: TtsPlayer
    private lateinit var btnPlay: ImageButton

    private var isPlaying = false
    private var currentIndex = 0
    private var currentList = listOf<Affirmation>()

    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ) {
        override fun onMove(
            rv: RecyclerView,
            from: RecyclerView.ViewHolder,
            to: RecyclerView.ViewHolder
        ): Boolean {
            adapter.moveItem(from.bindingAdapterPosition, to.bindingAdapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        override fun clearView(rv: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(rv, viewHolder)
            adapter.commitReorder()
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dao = AffirmationDatabase.getInstance(this).affirmationDao()
        btnPlay = findViewById(R.id.btn_play)

        setupAdapter()
        setupTts()

        findViewById<ImageButton>(R.id.btn_add).setOnClickListener { showDialog(null) }
        btnPlay.setOnClickListener { if (isPlaying) stopPlayback() else startPlayback() }

        observeAffirmations()
    }

    private fun setupAdapter() {
        adapter = AffirmationAdapter(
            onEdit = { showDialog(it) },
            onDelete = { affirmation ->
                lifecycleScope.launch(Dispatchers.IO) { dao.delete(affirmation) }
            },
            onDragStart = { holder -> itemTouchHelper.startDrag(holder) },
            onReordered = { list ->
                lifecycleScope.launch(Dispatchers.IO) {
                    list.forEachIndexed { index, affirmation ->
                        dao.updatePosition(affirmation.id, index)
                    }
                }
            }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupTts() {
        ttsPlayer = TtsPlayer(this) {
            runOnUiThread {
                if (isPlaying && currentList.isNotEmpty()) {
                    currentIndex = (currentIndex + 1) % currentList.size
                    ttsPlayer.speak(currentList[currentIndex].text)
                }
            }
        }
    }

    private fun startPlayback() {
        if (currentList.isEmpty()) return
        isPlaying = true
        currentIndex = 0
        btnPlay.setImageResource(android.R.drawable.ic_media_pause)
        ttsPlayer.speak(currentList[currentIndex].text)
    }

    private fun stopPlayback() {
        isPlaying = false
        ttsPlayer.stop()
        btnPlay.setImageResource(android.R.drawable.ic_media_play)
    }

    private fun showDialog(existing: Affirmation?) {
        val editText = EditText(this).apply {
            hint = "Enter affirmation..."
            setPadding(64, 32, 64, 32)
            existing?.let { setText(it.text) }
        }

        AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Add Affirmation" else "Edit Affirmation")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (existing == null) {
                            val position = dao.count()
                            dao.insert(Affirmation(text = text, position = position))
                        } else {
                            dao.update(existing.copy(text = text))
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeAffirmations() {
        lifecycleScope.launch {
            dao.getAll().collect { list ->
                currentList = list
                adapter.submitList(list)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsPlayer.shutdown()
    }
}
