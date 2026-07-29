package com.geoffrogers.affirmative

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AffirmationAdapter(
    private val onEdit: (Affirmation) -> Unit,
    private val onDelete: (Affirmation) -> Unit,
    private val onDragStart: (RecyclerView.ViewHolder) -> Unit,
    private val onReordered: (List<Affirmation>) -> Unit
) : RecyclerView.Adapter<AffirmationAdapter.ViewHolder>() {

    private val items = mutableListOf<Affirmation>()

    fun submitList(list: List<Affirmation>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        val item = items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
    }

    fun commitReorder() {
        onReordered(items.toList())
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dragHandle: ImageButton = view.findViewById(R.id.btn_drag)
        val text: TextView = view.findViewById(R.id.tv_affirmation)
        val editBtn: ImageButton = view.findViewById(R.id.btn_edit)
        val deleteBtn: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_affirmation, parent, false)
        return ViewHolder(view)
    }

    @Suppress("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val affirmation = items[position]
        holder.text.text = affirmation.text
        holder.editBtn.setOnClickListener { onEdit(affirmation) }
        holder.deleteBtn.setOnClickListener { onDelete(affirmation) }
        holder.dragHandle.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onDragStart(holder)
            }
            false
        }
    }

    override fun getItemCount() = items.size
}
