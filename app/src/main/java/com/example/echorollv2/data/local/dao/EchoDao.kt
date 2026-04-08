package com.example.echorollv2.data.local.dao

import androidx.room.*
import com.example.echorollv2.data.local.entity.AttendanceRecordEntity
import com.example.echorollv2.data.local.entity.RoutineEntity
import com.example.echorollv2.data.local.entity.StickyNoteEntity
import com.example.echorollv2.data.local.entity.ExamEntity
import com.example.echorollv2.data.local.entity.ExamSubjectEntity
import com.example.echorollv2.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EchoDao {

    // --- SUBJECT QUERIES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE subjectCode = :subjectCode")
    suspend fun getSubjectByCode(subjectCode: String): SubjectEntity?

    // --- ROUTINE QUERIES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineEntity>)

    @Query("DELETE FROM routines WHERE subjectCode = :subjectCode")
    suspend fun deleteRoutinesForSubject(subjectCode: String)

    @Query("SELECT * FROM routines WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getRoutinesForDay(day: String): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    // --- STICKY NOTE QUERIES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStickyNote(note: StickyNoteEntity)

    @Delete
    suspend fun deleteStickyNote(note: StickyNoteEntity)

    @Query("SELECT * FROM sticky_notes WHERE subjectCode = :subjectCode")
    fun getStickyNotesForSubject(subjectCode: String): Flow<List<StickyNoteEntity>>

    // --- ATTENDANCE RECORD QUERIES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecordEntity)

    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAttendanceRecordsForDate(date: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE subjectCode = :subjectCode")
    fun getAllAttendanceRecordsForSubject(subjectCode: String): Flow<List<AttendanceRecordEntity>>

    @Query("DELETE FROM attendance_records WHERE subjectCode = :subjectCode AND date = :date")
    suspend fun deleteAttendanceRecordsForDate(subjectCode: String, date: String)

    // --- HOLIDAY QUERIES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoliday(holiday: com.example.echorollv2.data.local.entity.HolidayEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolidays(holidays: List<com.example.echorollv2.data.local.entity.HolidayEntity>)

    @Delete
    suspend fun deleteHoliday(holiday: com.example.echorollv2.data.local.entity.HolidayEntity)

    @Query("SELECT * FROM holidays ORDER BY date ASC")
    fun getAllHolidays(): Flow<List<com.example.echorollv2.data.local.entity.HolidayEntity>>

    @Query("SELECT * FROM holidays WHERE date = :date LIMIT 1")
    suspend fun getHolidayByDate(date: String): com.example.echorollv2.data.local.entity.HolidayEntity?
    
    @Query("DELETE FROM holidays WHERE type = :type")
    suspend fun deleteHolidaysByType(type: String)

    @Query("DELETE FROM holidays")
    suspend fun deleteAllHolidays()

    // --- EXAM QUERIES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity)

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Delete
    suspend fun deleteExam(exam: ExamEntity)

    @Query("SELECT * FROM exams ORDER BY id DESC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamSubject(subject: ExamSubjectEntity)

    @Update
    suspend fun updateExamSubject(subject: ExamSubjectEntity)

    @Delete
    suspend fun deleteExamSubject(subject: ExamSubjectEntity)

    @Query("SELECT * FROM exam_subjects WHERE examId = :examId ORDER BY examDate ASC")
    fun getSubjectsForExam(examId: Int): Flow<List<ExamSubjectEntity>>

    @Query("SELECT * FROM exam_subjects ORDER BY examDate ASC")
    fun getAllExamSubjects(): Flow<List<ExamSubjectEntity>>
}