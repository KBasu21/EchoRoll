package com.example.echorollv2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.echorollv2.data.local.entity.AttendanceRecordEntity
import com.example.echorollv2.data.local.entity.RoutineEntity
import com.example.echorollv2.data.local.entity.StickyNoteEntity
import com.example.echorollv2.data.local.entity.SubjectEntity
import com.example.echorollv2.data.repository.EchoRepository
import com.example.echorollv2.ui.screens.setup.DaySchedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.echorollv2.data.local.entity.HolidayEntity
import com.example.echorollv2.data.network.HolidayApi
import com.example.echorollv2.data.preferences.UserPreferences

class EchoViewModel(
    private val repository: EchoRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    // A live feed of all subjects to display on your Attendance Screen
    val allSubjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // NEW: Expose all routines for the Routine Screen
    val allRoutines: StateFlow<List<RoutineEntity>> = repository.allRoutines
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getAttendanceRecordsForDate(date: String): Flow<List<AttendanceRecordEntity>> {
        return repository.getAttendanceRecordsForDate(date)
    }

    fun getAllAttendanceRecordsForSubject(subjectCode: String): Flow<List<AttendanceRecordEntity>> {
        return repository.getAllAttendanceRecordsForSubject(subjectCode)
    }

    // --- HOLIDAYS ---
    val allHolidays: StateFlow<List<HolidayEntity>> = repository.allHolidays
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    enum class HolidayFetchStatus { IDLE, LOADING, SUCCESS, ERROR, EMPTY, NO_INTERNET }
    private val _fetchStatus = kotlinx.coroutines.flow.MutableStateFlow(HolidayFetchStatus.IDLE)
    val fetchStatus: StateFlow<HolidayFetchStatus> = _fetchStatus

    private val _errorMessage = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val countryCode: StateFlow<String?> = preferences.countryCodeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")
        
    val subdivisionCode: StateFlow<String?> = preferences.subdivisionCodeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun saveRegion(country: String, subdivision: String) {
        viewModelScope.launch {
            preferences.saveCountryCode(country)
            preferences.saveSubdivisionCode(subdivision)
        }
    }

    fun saveHoliday(holiday: HolidayEntity) {
        viewModelScope.launch {
            repository.insertHoliday(holiday)
        }
    }

    fun fetchHolidays(year: Int, country: String, subdivision: String) {
        if (country.isBlank()) {
            _fetchStatus.value = HolidayFetchStatus.ERROR
            _errorMessage.value = "Country code is missing."
            return
        }

        viewModelScope.launch {
            _fetchStatus.value = HolidayFetchStatus.LOADING
            _errorMessage.value = null
            try {
                val api = HolidayApi.create()
                val apiDtos = try {
                    api.getPublicHolidays(year, country)
                } catch (e: Exception) {
                    emptyList()
                }
                
                val localGenerated = if (country == "IN") {
                    com.example.echorollv2.data.local.IndianHolidayGenerator.generate(year, subdivision)
                } else {
                    emptyList()
                }

                if (apiDtos.isEmpty() && localGenerated.isEmpty()) {
                    _fetchStatus.value = HolidayFetchStatus.EMPTY
                    return@launch
                }

                // Filter API by subdivision if provided
                val filteredApiDtos = if (subdivision.isBlank()) {
                    apiDtos 
                } else {
                    val fullSubCode = "${country}-${subdivision}"
                    apiDtos.filter { 
                        it.global == true || 
                        it.counties?.contains(fullSubCode) == true || 
                        it.counties?.contains(subdivision) == true 
                    }
                }

                repository.deleteAllAutomaticHolidays()
                
                // Map API to entities
                val apiEntities = filteredApiDtos.mapNotNull { dto ->
                    dto.date?.let { date ->
                        HolidayEntity(
                            date = date, 
                            name = dto.name ?: dto.localName ?: "Holiday", 
                            type = "Automatic"
                        )
                    }
                }
                
                // Merge and dedup
                val finalEntities = (apiEntities + localGenerated).distinctBy { it.date + it.name }
                
                if (finalEntities.isEmpty()) {
                    _fetchStatus.value = HolidayFetchStatus.EMPTY
                    return@launch
                }

                repository.insertHolidays(finalEntities)
                _fetchStatus.value = HolidayFetchStatus.SUCCESS
            } catch (e: retrofit2.HttpException) {
                _fetchStatus.value = HolidayFetchStatus.ERROR
                _errorMessage.value = "Server error: ${e.code()} ${e.message()}"
            } catch (e: java.io.IOException) {
                _fetchStatus.value = HolidayFetchStatus.NO_INTERNET
            } catch (e: Exception) {
                _fetchStatus.value = HolidayFetchStatus.ERROR
                _errorMessage.value = "Error: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    fun resetFetchStatus() {
        _fetchStatus.value = HolidayFetchStatus.IDLE
    }

    fun addManualHoliday(date: String, name: String) {
        viewModelScope.launch {
            repository.insertHoliday(HolidayEntity(date = date, name = name, type = "Manual"))
        }
    }

    fun deleteHoliday(holiday: HolidayEntity) {
        viewModelScope.launch {
            repository.deleteHoliday(holiday)
        }
    }

    // The function triggered by the "Add Subject" button
    fun saveSubjectAndRoutine(
        subjectCode: String,
        name: String,
        category: String,
        professorName: String,
        attended: Int,
        missed: Int,
        requiredPercentage: Int,
        weeklySchedule: List<DaySchedule>
    ) {
        // Run this in the background so it doesn't freeze the app
        viewModelScope.launch {

            // 1. Package the Subject Data
            val subject = SubjectEntity(
                subjectCode = subjectCode,
                name = name,
                category = category,
                professorName = professorName,
                attended = attended,
                missed = missed,
                requiredPercentage = requiredPercentage
            )

            // 2. Save the Subject to the Database
            repository.insertSubject(subject)

            // 3. Delete existing routines for this subject before saving new ones
            repository.deleteRoutinesForSubject(subjectCode)

            // 4. Package only the Days where the Checkbox was ticked
            val activeRoutines = weeklySchedule.filter { it.isEnabled }.map { day ->
                RoutineEntity(
                    subjectCode = subjectCode,
                    dayOfWeek = day.dayName,
                    startTime = day.startTime,
                    endTime = day.endTime
                )
            }

            // 5. Save those routines to the Database linked to this subject!
            if (activeRoutines.isNotEmpty()) {
                repository.insertRoutines(activeRoutines)
            }
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    fun updateSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.updateSubject(subject)
        }
    }
    
    fun getRoutinesForSubject(subjectCode: String): List<RoutineEntity> {
        return allRoutines.value.filter { it.subjectCode == subjectCode }
    }

    // --- STICKY NOTE FUNCTIONS ---
    fun getStickyNotesForSubject(subjectCode: String): Flow<List<StickyNoteEntity>> {
        return repository.getStickyNotesForSubject(subjectCode)
    }

    fun saveStickyNote(subjectCode: String, title: String, description: String) {
        viewModelScope.launch {
            repository.insertStickyNote(StickyNoteEntity(subjectCode = subjectCode, title = title, description = description))
        }
    }

    fun deleteStickyNote(note: StickyNoteEntity) {
        viewModelScope.launch {
            repository.deleteStickyNote(note)
        }
    }

    // --- ATTENDANCE MARKING ---
    fun markAttendance(routine: RoutineEntity, status: String) {
        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val record = AttendanceRecordEntity(
                subjectCode = routine.subjectCode,
                routineId = routine.id,
                date = date,
                status = status
            )
            repository.insertAttendanceRecord(record)

            // Update subject counts
            if (status == "Present" || status == "Absent") {
                val subject = repository.getSubjectByCode(routine.subjectCode)
                subject?.let {
                    val updatedSubject = if (status == "Present") {
                        it.copy(attended = it.attended + 1)
                    } else {
                        it.copy(missed = it.missed + 1)
                    }
                    repository.updateSubject(updatedSubject)
                }
            }
        }
    }

    fun updateManualAttendance(subjectCode: String, date: Date, isOffDay: Boolean, attendedCount: Int, missedCount: Int) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            
            // Delete existing records for this date/subject to replace them
            repository.deleteAttendanceRecordsForDate(subjectCode, dateStr)
            
            if (isOffDay) {
                repository.insertAttendanceRecord(AttendanceRecordEntity(
                    subjectCode = subjectCode, routineId = -1, date = dateStr, status = "Cancelled"
                ))
            } else {
                // Create individual records for each class attended/missed
                repeat(attendedCount) {
                    repository.insertAttendanceRecord(AttendanceRecordEntity(
                        subjectCode = subjectCode, routineId = -1, date = dateStr, status = "Present"
                    ))
                }
                repeat(missedCount) {
                    repository.insertAttendanceRecord(AttendanceRecordEntity(
                        subjectCode = subjectCode, routineId = -1, date = dateStr, status = "Absent"
                    ))
                }
            }
            
            // Re-calculate all subject totals from records
            repository.recalculateSubjectStats(subjectCode)
        }
    }
}

// A Factory is required to pass the Repository and Preferences into the ViewModel
class EchoViewModelFactory(
    private val repository: EchoRepository,
    private val preferences: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EchoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EchoViewModel(repository, preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
