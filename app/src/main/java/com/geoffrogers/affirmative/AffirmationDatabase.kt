package com.geoffrogers.affirmative

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Affirmation::class], version = 1)
abstract class AffirmationDatabase : RoomDatabase() {
    abstract fun affirmationDao(): AffirmationDao

    companion object {
        @Volatile private var INSTANCE: AffirmationDatabase? = null

        fun getInstance(context: Context): AffirmationDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AffirmationDatabase::class.java,
                    "affirmative.db"
                ).build().also { INSTANCE = it }
            }
    }
}
