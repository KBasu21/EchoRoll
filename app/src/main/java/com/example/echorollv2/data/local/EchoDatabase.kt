package com.example.echorollv2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.echorollv2.data.local.dao.EchoDao
import com.example.echorollv2.data.local.entity.AttendanceRecordEntity
import com.example.echorollv2.data.local.entity.RoutineEntity
import com.example.echorollv2.data.local.entity.StickyNoteEntity
import com.example.echorollv2.data.local.entity.HolidayEntity
import com.example.echorollv2.data.local.entity.SubjectEntity

@Database(
    entities = [SubjectEntity::class, RoutineEntity::class, StickyNoteEntity::class, AttendanceRecordEntity::class, HolidayEntity::class],
    version = 5,
    exportSchema = false
)
abstract class EchoDatabase : RoomDatabase() {

    abstract fun echoDao(): EchoDao

    companion object {
        @Volatile
        private var INSTANCE: EchoDatabase? = null

        val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `holidays` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL)")
            }
        }

        fun getDatabase(context: Context): EchoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoDatabase::class.java,
                    "echoroll_v2_database"
                ).addMigrations(MIGRATION_4_5).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
