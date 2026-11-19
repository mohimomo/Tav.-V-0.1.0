package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ManualDocumentEntity::class,
        CachedAiResponseEntity::class,
        ComponentPhotoEntity::class,
        ShiftLogEntity::class,
        ComponentSpecEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class TavFurnaceDatabase : RoomDatabase() {

    abstract fun manualDao(): ManualDao
    abstract fun cachedAiResponseDao(): CachedAiResponseDao
    abstract fun componentPhotoDao(): ComponentPhotoDao
    abstract fun shiftLogDao(): ShiftLogDao
    abstract fun componentSpecDao(): ComponentSpecDao

    companion object {
        @Volatile
        private var INSTANCE: TavFurnaceDatabase? = null

        fun getDatabase(context: Context): TavFurnaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TavFurnaceDatabase::class.java,
                    "tav_furnace_911.db"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
