package com.geoffrogers.affirmative

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "affirmations")
data class Affirmation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val position: Int
)
