package com.example.echorollv2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.echorollv2.data.local.dao.EchoDao
import com.example.echorollv2.data.local.entity.AttendanceRecordEntity
import com.example.echorollv2.data.local.entity.ClassReplacementEntity
import com.example.echorollv2.data.local.entity.RoutineEntity
import com.example.echorollv2.data.local.entity.StickyNoteEntity
import com.example.echorollv2.data.local.entity.HolidayEntity
import com.example.echorollv2.data.local.entity.SubjectEntity
import com.example.echorollv2.data.local.entity.ExamEntity
import com.example.echorollv2.data.local.entity.ExamSubjectEntity
import com.example.echorollv2.data.local.entity.ExtraClassEntity

@Database(
    entities = [
        SubjectEntity::class, 
        RoutineEntity::class, 
        StickyNoteEntity::class, 
        AttendanceRecordEntity::class, 
        HolidayEntity::class,
        ExamEntity::class,
        ExamSubjectEntity::class,
        ClassReplacementEntity::class,
        ExtraClassEntity::class
    ],
    version = 8,
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

        val MIGRATION_5_6 = object : androidx.room.migration.Migration(5, 6) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `exams` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `classesHeldDuringExams` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `exam_subjects` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `examId` INTEGER NOT NULL, `subjectCode` TEXT NOT NULL, `subjectName` TEXT NOT NULL, `examDate` TEXT NOT NULL, `marksScored` TEXT NOT NULL, `stickyNote` TEXT NOT NULL, FOREIGN KEY(`examId`) REFERENCES `exams`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_exam_subjects_examId` ON `exam_subjects` (`examId`)")
            }
        }

        val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `class_replacements` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routineId` INTEGER NOT NULL, `date` TEXT NOT NULL, `originalSubjectCode` TEXT NOT NULL, `replacementSubjectCode` TEXT NOT NULL)")
            }
        }

        val MIGRATION_7_8 = object : androidx.room.migration.Migration(7, 8) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `extra_classes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `subjectCode` TEXT NOT NULL, `date` TEXT NOT NULL)")
            }
        }

        fun getDatabase(context: Context): EchoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoDatabase::class.java,
                    "echoroll_v2_database"
                ).addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
