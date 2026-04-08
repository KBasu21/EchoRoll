package com.example.echorollv2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.echorollv2.data.local.entity.AttendanceRecordEntity
import com.example.echorollv2.data.local.entity.RoutineEntity
import com.example.echorollv2.data.local.entity.StickyNoteEntity
import com.example.echorollv2.data.local.entity.SubjectEntity
import com.example.echorollv2.data.local.entity.ClassReplacementEntity
import com.example.echorollv2.data.local.entity.ExamEntity
import com.example.echorollv2.data.local.entity.ExamSubjectEntity
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
import com.example.echorollv2.data.network.GitHubRelease
import com.example.echorollv2.data.network.UpdateApi
import com.example.echorollv2.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EchoViewModel(
    private val repository: EchoRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    private val _latestRelease = MutableStateFlow<GitHubRelease?>(null)
    val latestRelease = _latestRelease.asStateFlow()

    private val _updateAvailable = MutableStateFlow(false)
    val updateAvailable = _updateAvailable.asStateFlow()

    private val updateApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UpdateApi::class.java)
    }

    init {
        // Initial check from cache (Offline Support)
        viewModelScope.launch {
            val (cachedTag, cachedUrl) = preferences.updateInfoFlow.first()
            val dismissedTag = preferences.dismissedVersionFlow.first()
            if (cachedTag != null && cachedUrl != null && normalizeTag(cachedTag) != normalizeTag(dismissedTag)) {
                if (isVersionNewer(BuildConfig.VERSION_NAME, cachedTag)) {
                    _latestRelease.value = GitHubRelease(
                        tagName = cachedTag,
                        htmlUrl = cachedUrl,
                        name = cachedTag,
                        body = "A new update is available!",
                        assets = emptyList()
                    )
                    _updateAvailable.value = true
                }
            }
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            try {
                val release = updateApi.getLatestRelease()
                val currentVersion = BuildConfig.VERSION_NAME
                val dismissedTag = preferences.dismissedVersionFlow.first()
                
                if (isVersionNewer(currentVersion, release.tagName)) {
                    _latestRelease.value = release
                    if (normalizeTag(release.tagName) != normalizeTag(dismissedTag)) {
                        _updateAvailable.value = true
                    }
                    // Cache it for offline use next time
                    preferences.saveUpdateInfo(release.tagName, release.htmlUrl)
                } else {
                    _updateAvailable.value = false
                    preferences.clearUpdateInfo()
                }
            } catch (e: Exception) {
                // If offline, we stay with whatever the cache provided in init
            }
        }
    }

    private fun normalizeTag(tag: String?): String {
        return tag?.trim()?.lowercase()?.removePrefix("v") ?: ""
    }

    private fun isVersionNewer(current: String, latest: String): Boolean {
        return try {
            val cur = current.trim().lowercase().removePrefix("v").split(".")
            val lat = latest.trim().lowercase().removePrefix("v").split(".")
            val maxLength = maxOf(cur.size, lat.size)
            for (i in 0 until maxLength) {
                val c = cur.getOrNull(i)?.toIntOrNull() ?: 0
                val l = lat.getOrNull(i)?.toIntOrNull() ?: 0
                if (l > c) return true
                if (c > l) return false
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun dismissUpdate() {
        _updateAvailable.value = false
        _latestRelease.value?.let { release ->
            viewModelScope.launch {
                preferences.saveDismissedVersion(release.tagName)
            }
        }
    }

    // A live feed of all subjects to display on your Attendance Screen
    val allSubjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // NEW: Expose all routines for the Routine Screen
    val allRoutines: StateFlow<List<RoutineEntity>> = repository.allRoutines
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // UI state for Attendance Screen tab
    private val _selectedAttendanceTab = kotlinx.coroutines.flow.MutableStateFlow("Theory")
    val selectedAttendanceTab: StateFlow<String> = _selectedAttendanceTab

    fun setAttendanceTab(tab: String) {
        _selectedAttendanceTab.value = tab
    }

    fun getAttendanceRecordsForDate(date: String): Flow<List<AttendanceRecordEntity>> {
        return repository.getAttendanceRecordsForDate(date)
    }

    fun getAllAttendanceRecordsForSubject(subjectCode: String): Flow<List<AttendanceRecordEntity>> {
        return repository.getAllAttendanceRecordsForSubject(subjectCode)
    }

    // --- CLASS REPLACEMENT ---
    fun getReplacementsForDate(date: String): Flow<List<ClassReplacementEntity>> {
        return repository.getReplacementsForDate(date)
    }

    fun replaceClass(routine: RoutineEntity, date: String, replacementSubjectCode: String) {
        viewModelScope.launch {
            val replacement = ClassReplacementEntity(
                routineId = routine.id,
                date = date,
                originalSubjectCode = routine.subjectCode,
                replacementSubjectCode = replacementSubjectCode
            )
            repository.insertReplacement(replacement)
            
            // Clean up any existing attendance for the original subject in this slot (e.g. if marked by mistake)
            repository.deleteAttendanceRecordForRoutine(routine.subjectCode, routine.id, date)
            
            // Auto-cancel the original subject's attendance for this slot
            val cancelRecord = AttendanceRecordEntity(
                subjectCode = routine.subjectCode,
                routineId = routine.id,
                date = date,
                status = "Cancelled"
            )
            repository.insertAttendanceRecord(cancelRecord)
            repository.recalculateSubjectStats(routine.subjectCode)
        }
    }

    fun undoReplacement(routine: RoutineEntity, date: String) {
        viewModelScope.launch {
            val replacementsForDate = repository.getReplacementsForDate(date).first()
            val replacement = replacementsForDate.find { it.routineId == routine.id }
            
            if (replacement != null) {
                // Delete records for both subjects for this specific routine slot
                repository.deleteAttendanceRecordForRoutine(replacement.originalSubjectCode, routine.id, date)
                repository.deleteAttendanceRecordForRoutine(replacement.replacementSubjectCode, routine.id, date)
                
                // Recalculate stats for both
                repository.recalculateSubjectStats(replacement.originalSubjectCode)
                repository.recalculateSubjectStats(replacement.replacementSubjectCode)
                
                // Delete the replacement record itself
                repository.deleteReplacement(routine.id, date)
            }
        }
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

    fun deleteAllHolidays() {
        viewModelScope.launch {
            repository.deleteAllHolidays()
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
    fun markAttendance(routine: RoutineEntity, status: String, replacementSubjectCode: String? = null) {
        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            val subjectToMark = replacementSubjectCode ?: routine.subjectCode
            
            // Delete existing record for this routine slot to avoid duplicates
            repository.deleteAttendanceRecordForRoutine(subjectToMark, routine.id, date)
            
            val record = AttendanceRecordEntity(
                subjectCode = subjectToMark,
                routineId = routine.id,
                date = date,
                status = status
            )
            repository.insertAttendanceRecord(record)

            // If it was a replacement, ensure the original is marked as Cancelled
            if (replacementSubjectCode != null) {
                // Delete existing record for original before marking as Cancelled
                repository.deleteAttendanceRecordForRoutine(routine.subjectCode, routine.id, date)
                
                val originalCancelRecord = AttendanceRecordEntity(
                    subjectCode = routine.subjectCode,
                    routineId = routine.id,
                    date = date,
                    status = "Cancelled"
                )
                repository.insertAttendanceRecord(originalCancelRecord)
                repository.recalculateSubjectStats(routine.subjectCode)
            }

            // Always recalculate stats to ensure consistency
            repository.recalculateSubjectStats(subjectToMark)
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

    // --- EXAMS ---
    val allExams: StateFlow<List<ExamEntity>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allExamSubjects: StateFlow<List<ExamSubjectEntity>> = repository.allExamSubjects
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveExam(name: String, classesHeld: Boolean, id: Int = 0) {
        viewModelScope.launch {
            if (id == 0) {
                repository.insertExam(ExamEntity(name = name, classesHeldDuringExams = classesHeld))
            } else {
                repository.updateExam(ExamEntity(id = id, name = name, classesHeldDuringExams = classesHeld))
            }
        }
    }

    fun deleteExam(exam: ExamEntity) {
        viewModelScope.launch {
            repository.deleteExam(exam)
        }
    }

    fun saveExamSubject(
        examId: Int,
        subjectCode: String,
        subjectName: String,
        examDate: String,
        marks: String = "",
        stickyNote: String = "",
        id: Int = 0
    ) {
        viewModelScope.launch {
            val entity = ExamSubjectEntity(
                id = id,
                examId = examId,
                subjectCode = subjectCode,
                subjectName = subjectName,
                examDate = examDate,
                marksScored = marks,
                stickyNote = stickyNote
            )
            if (id == 0) {
                repository.insertExamSubject(entity)
            } else {
                repository.updateExamSubject(entity)
            }
        }
    }

    fun deleteExamSubject(subject: ExamSubjectEntity) {
        viewModelScope.launch {
            repository.deleteExamSubject(subject)
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
