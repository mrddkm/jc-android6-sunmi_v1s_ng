package com.arkhe.sunmi.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [ScanHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ScanHistoryDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        fun create(context: Context): ScanHistoryDatabase {
            return Room.databaseBuilder(
                context,
                ScanHistoryDatabase::class.java,
                "scan_history.db"
            ).build()
        }
    }
}