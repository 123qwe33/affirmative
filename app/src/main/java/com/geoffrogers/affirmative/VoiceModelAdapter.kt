package com.geoffrogers.affirmative

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VoiceModelAdapter(
    private val models: List<VoiceModel>,
    private val onActionClick: (VoiceModel) -> Unit
) : RecyclerView.Adapter<VoiceModelAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_voice_model_name)
        val action: ImageButton = itemView.findViewById(R.id.btn_voice_model_action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voice_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        holder.name.text = model.displayName

        when (model.state) {
            VoiceModelState.NOT_DOWNLOADED -> {
                holder.name.alpha = 0.5f
                holder.action.setImageResource(R.drawable.ic_download)
                holder.action.visibility = View.VISIBLE
                holder.action.setOnClickListener { onActionClick(model) }
            }
            VoiceModelState.DOWNLOADING -> {
                holder.name.alpha = 0.5f
                holder.action.setImageResource(R.drawable.ic_close)
                holder.action.visibility = View.VISIBLE
                holder.action.setOnClickListener { onActionClick(model) }
            }
            VoiceModelState.READY -> {
                holder.name.alpha = 1.0f
                holder.action.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = models.size
}
