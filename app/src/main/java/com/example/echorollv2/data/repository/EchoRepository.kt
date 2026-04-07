package com.example.echorollv2.data.repository

import com.example.echorollv2.data.local.dao.EchoDao
import com.example.echorollv2.data.local.entity.AttendanceRecordEntity
import com.example.echorollv2.data.local.entity.RoutineEntity
import com.example.echorollv2.data.local.entity.StickyNoteEntity
import com.example.echorollv2.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EchoRepository(private val dao: EchoDao) {

    // Subjects
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()

    suspend fun insertSubject(subject: SubjectEntity) {
        dao.insertSubject(subject)
    }

    suspend fun updateSubject(subject: SubjectEntity) {
        dao.updateSubject(subject)
    }

    suspend fun deleteSubject(subject: SubjectEntity) {
        dao.deleteSubject(subject)
    }

    suspend fun getSubjectByCode(subjectCode: String): SubjectEntity? {
        return dao.getSubjectByCode(subjectCode)
    }

    // Routines
    val allRoutines: Flow<List<RoutineEntity>> = dao.getAllRoutines()

    suspend fun insertRoutines(routines: List<RoutineEntity>) {
        dao.insertRoutines(routines)
    }

    suspend fun deleteRoutinesForSubject(subjectCode: String) {
        dao.deleteRoutinesForSubject(subjectCode)
    }

    fun getRoutinesForDay(day: String): Flow<List<RoutineEntity>> {
        return dao.getRoutinesForDay(day)
    }

    // Sticky Notes
    suspend fun insertStickyNote(note: StickyNoteEntity) {
        dao.insertStickyNote(note)
    }

    suspend fun deleteStickyNote(note: StickyNoteEntity) {
        dao.deleteStickyNote(note)
    }

    fun getStickyNotesForSubject(subjectCode: String): Flow<List<StickyNoteEntity>> {
        return dao.getStickyNotesForSubject(subjectCode)
    }

    // Attendance Records
    suspend fun insertAttendanceRecord(record: AttendanceRecordEntity) {
        dao.insertAttendanceRecord(record)
    }

    fun getAttendanceRecordsForDate(date: String): Flow<List<AttendanceRecordEntity>> {
        return dao.getAttendanceRecordsForDate(date)
    }

    fun getAllAttendanceRecordsForSubject(subjectCode: String): Flow<List<AttendanceRecordEntity>> {
        return dao.getAllAttendanceRecordsForSubject(subjectCode)
    }

    suspend fun deleteAttendanceRecordsForDate(subjectCode: String, date: String) {
        dao.deleteAttendanceRecordsForDate(subjectCode, date)
    }

    suspend fun recalculateSubjectStats(subjectCode: String) {
        val records = dao.getAllAttendanceRecordsForSubject(subjectCode).first()
        val subject = dao.getSubjectByCode(subjectCode)
        
        subject?.let {
            val attended = records.count { it.status == "Present" }
            val missed = records.count { it.status == "Absent" }
            
            val updatedSubject = it.copy(attended = attended, missed = missed)
            dao.updateSubject(updatedSubject)
        }
    }

    // Holidays
    val allHolidays: Flow<List<com.example.echorollv2.data.local.entity.HolidayEntity>> = dao.getAllHolidays()

    suspend fun insertHoliday(holiday: com.example.echorollv2.data.local.entity.HolidayEntity) {
        dao.insertHoliday(holiday)
    }

    suspend fun insertHolidays(holidays: List<com.example.echorollv2.data.local.entity.HolidayEntity>) {
        dao.insertHolidays(holidays)
    }

    suspend fun deleteHoliday(holiday: com.example.echorollv2.data.local.entity.HolidayEntity) {
        dao.deleteHoliday(holiday)
    }

    suspend fun deleteAllAutomaticHolidays() {
        dao.deleteHolidaysByType("Automatic")
    }

    suspend fun deleteAllHolidays() {
        dao.deleteAllHolidays()
    }
}
