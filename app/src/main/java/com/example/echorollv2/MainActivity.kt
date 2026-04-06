package com.example.echorollv2

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.echorollv2.data.local.EchoDatabase
import com.example.echorollv2.data.local.entity.AttendanceRecordEntity
import com.example.echorollv2.data.local.entity.HolidayEntity
import com.example.echorollv2.data.local.entity.RoutineEntity
import com.example.echorollv2.data.local.entity.SubjectEntity
import com.example.echorollv2.data.preferences.UserPreferences
import com.example.echorollv2.data.repository.EchoRepository
import com.example.echorollv2.services.DailyCheckWorker
import com.example.echorollv2.services.NotificationHelper
import com.example.echorollv2.ui.screens.setup.AddSubjectScreen
import com.example.echorollv2.ui.screens.setup.DaySchedule
import com.example.echorollv2.ui.theme.*
import com.example.echorollv2.ui.viewmodels.EchoViewModel
import com.example.echorollv2.ui.viewmodels.EchoViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


val SubjectColors = listOf(
    PrimaryBlue, PrimaryGreen, PrimaryRed, PrimaryOrange, PrimaryPurple,
    Color(0xFF26A69A), Color(0xFFEC407A), Color(0xFFFF5722), Color(0xFFE91E63),
    Color(0xFF9C27B0), Color(0xFF3F51B5), Color(0xFF00BCD4), Color(0xFF4CAF50),
    Color(0xFFFFC107), Color(0xFF673AB7), Color(0xFF03A9F4), Color(0xFFCDDC39)
)

fun getSubjectColor(subjectCode: String): Color {
    val index = kotlin.math.abs(subjectCode.hashCode()) % SubjectColors.size
    return SubjectColors[index]
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)
        val workRequest = PeriodicWorkRequestBuilder<DailyCheckWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("DailyCheck", ExistingPeriodicWorkPolicy.KEEP, workRequest)

        val database = EchoDatabase.getDatabase(this)
        val repository = EchoRepository(database.echoDao())
        val preferences = UserPreferences(this)

        setContent {
            var isDarkMode by androidx.compose.runtime.remember { mutableStateOf(true) }

            // Notification Permission (API 33+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
                    androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
                ) { /* handle */ }
                
                LaunchedEffect(Unit) {
                    val granted = androidx.core.content.ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    if (!granted) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            EchoRollV2Theme(isDark = isDarkMode) {
                val viewModel: EchoViewModel = viewModel(
                    factory = EchoViewModelFactory(repository, preferences)
                )

                MainScreen(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    onThemeToggle = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: EchoViewModel,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    val colors = LocalAppColors.current
    var currentScreen by remember { mutableStateOf("Today") }
    
    // Notification Intent Handler
    val activity = androidx.compose.ui.platform.LocalContext.current as? android.app.Activity
    LaunchedEffect(activity?.intent) {
        if (activity?.intent?.getBooleanExtra("OPEN_TODAY", false) == true) {
            currentScreen = "Today"
        }
    }

    var navigationStack by remember { mutableStateOf(emptyList<String>()) }
    
    // Scroll States
    val attendanceScrollState = androidx.compose.foundation.lazy.rememberLazyListState()
    val todayScrollState = androidx.compose.foundation.lazy.rememberLazyListState()

    var subjectToEdit by remember { mutableStateOf<SubjectEntity?>(null) }
    var editRoutines by remember { mutableStateOf<List<DaySchedule>>(emptyList()) }
    var selectedSubjectCode by remember { mutableStateOf("") }

    val navigateTo: (String) -> Unit = { screen ->
        navigationStack = navigationStack + currentScreen
        currentScreen = screen
    }

    val goBack: () -> Unit = {
        if (navigationStack.isNotEmpty()) {
            currentScreen = navigationStack.last()
            navigationStack = navigationStack.dropLast(1)
        }
    }

    androidx.activity.compose.BackHandler(enabled = navigationStack.isNotEmpty()) {
        goBack()
    }

    Scaffold(
        bottomBar = {
            if (currentScreen in listOf("Attendance", "Routine", "Today", "Holidays", "Settings")) {
                BottomNavigationBar(currentScreen) { 
                    if (it != currentScreen) {
                        navigationStack = emptyList() // Clear stack when switching tabs
                        currentScreen = it 
                    }
                }
            }
        },
        containerColor = colors.background
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            when (currentScreen) {
                "Today" -> TodayScreen(
                    viewModel = viewModel, 
                    isDarkMode = isDarkMode, 
                    onThemeToggle = onThemeToggle,
                    scrollState = todayScrollState,
                    onNavigateToStickyNotes = { code ->
                        selectedSubjectCode = code
                        navigateTo("StickyNotes")
                    }
                )
                "Attendance" -> AttendanceScreen(
                    viewModel = viewModel,
                    scrollState = attendanceScrollState,
                    onNavigateToAddSubject = { navigateTo("AddSubject") },
                    onEditSubject = { subject ->
                        subjectToEdit = subject
                        editRoutines = viewModel.getRoutinesForSubject(subject.subjectCode).map {
                            DaySchedule(it.dayOfWeek, isEnabled = true, it.startTime, it.endTime)
                        }
                        navigateTo("EditSubject")
                    },
                    onNavigateToStickyNotes = { code ->
                        selectedSubjectCode = code
                        navigateTo("StickyNotes")
                    },
                    onNavigateToSubjectDetail = { code ->
                        selectedSubjectCode = code
                        navigateTo("SubjectDetail")
                    },
                    isDarkMode = isDarkMode,
                    onThemeToggle = onThemeToggle
                )
                "Routine" -> RoutineScreen(viewModel, isDarkMode, onThemeToggle)

                "AddSubject" -> AddSubjectScreen(
                    onNavigateBack = goBack,
                    onSaveSubject = { code, name, category, professor, attended, missed, req, schedule ->
                        viewModel.saveSubjectAndRoutine(
                            code, name, category, professor, attended, missed, req, schedule
                        )
                        goBack()
                    }
                )
                
                "EditSubject" -> {
                    subjectToEdit?.let { subject ->
                        AddSubjectScreen(
                            onNavigateBack = goBack,
                            onSaveSubject = { code, name, category, professor, attended, missed, req, schedule ->
                                viewModel.saveSubjectAndRoutine(
                                    code, name, category, professor, attended, missed, req, schedule
                                )
                                goBack()
                            },
                            onDeleteSubject = {
                                viewModel.deleteSubject(it)
                                goBack()
                            },
                            initialSubject = subject,
                            initialRoutines = editRoutines
                        )
                    }
                }

                "StickyNotes" -> StickyNotesScreen(
                    subjectCode = selectedSubjectCode,
                    viewModel = viewModel,
                    onNavigateBack = goBack,
                    onNavigateToAddNote = { navigateTo("AddStickyNote") }
                )

                "AddStickyNote" -> AddStickyNoteScreen(
                    subjectCode = selectedSubjectCode,
                    viewModel = viewModel,
                    onNavigateBack = goBack
                )

                "SubjectDetail" -> SubjectDetailScreen(
                    subjectCode = selectedSubjectCode,
                    viewModel = viewModel,
                    onNavigateBack = goBack
                )

                "Settings" -> com.example.echorollv2.ui.screens.features.SettingsScreen(
                    currentCountryCode = viewModel.countryCode.collectAsState().value ?: "",
                    currentSubdivisionCode = viewModel.subdivisionCode.collectAsState().value ?: "",
                    fetchStatus = viewModel.fetchStatus.collectAsState().value,
                    errorMessage = viewModel.errorMessage.collectAsState().value,
                    onSaveRegion = { country, state -> viewModel.saveRegion(country, state) },
                    onNavigateBack = goBack,
                    onFetchHolidays = { country, state -> 
                        viewModel.fetchHolidays(Calendar.getInstance().get(Calendar.YEAR), country, state) 
                    },
                    onResetFetchStatus = { viewModel.resetFetchStatus() }
                )

                "Holidays" -> com.example.echorollv2.ui.screens.features.HolidaysScreen(
                    holidays = viewModel.allHolidays.collectAsState().value,
                    onNavigateBack = goBack,
                    onDeleteHoliday = { viewModel.deleteHoliday(it) },
                    onAddManualHoliday = { date, name -> viewModel.addManualHoliday(date, name) },
                    onUpdateHoliday = { viewModel.saveHoliday(it) }
                )
            }
        }
    }
}

@Composable
fun TodayScreen(
    viewModel: EchoViewModel,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    scrollState: androidx.compose.foundation.lazy.LazyListState,
    onNavigateToStickyNotes: (String) -> Unit
) {
    val colors = LocalAppColors.current
    var selectedDate by remember { mutableStateOf(Date()) }
    
    val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(selectedDate)
    val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val isEditable = dateFormatted == todayDateStr
    
    val routines by viewModel.allRoutines.collectAsState()
    val attendanceRecords by viewModel.getAttendanceRecordsForDate(dateFormatted).collectAsState(initial = emptyList())
    val subjects by viewModel.allSubjects.collectAsState()

    val todayRoutines = routines.filter { it.dayOfWeek == dayName }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = colors.textPrimary,
                modifier = Modifier.clickable { onThemeToggle() }
            )
            Text("Today's Schedule", color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(24.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Calendar Row
        CalendarHeader(selectedDate = selectedDate, onDateSelected = { selectedDate = it })

        Spacer(modifier = Modifier.height(16.dp))

        val allHolidays by viewModel.allHolidays.collectAsState()
        val todayHoliday = allHolidays.find { it.date == dateFormatted }

        if (todayHoliday != null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉", fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("${todayHoliday.name}", color = colors.textPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text(com.example.echorollv2.utils.HumorUtils.getHolidayMessage(), color = colors.textSecondary, fontSize = 18.sp, textAlign = TextAlign.Center)
                }
            }
        } else if (todayRoutines.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No classes scheduled!", color = colors.textSecondary)
            }
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(todayRoutines) { routine ->
                    val subject = subjects.find { it.subjectCode == routine.subjectCode }
                    val attendance = attendanceRecords.find { it.routineId == routine.id }
                    
                    if (subject != null) {
                        TodayClassCard(
                            subject = subject,
                            routine = routine,
                            attendanceStatus = attendance?.status,
                            isEditable = isEditable,
                            onMarkAttendance = { status ->
                                viewModel.markAttendance(routine, status)
                            },
                            onStickyNotesClick = {
                                onNavigateToStickyNotes(subject.subjectCode)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(selectedDate: Date, onDateSelected: (Date) -> Unit) {
    val colors = LocalAppColors.current
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    
    val dates = remember {
        val list = java.util.ArrayList<Date>(14601)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7300) // approx 20 years back
        for (i in 0..14600) {
            list.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }
    
    val listState = androidx.compose.foundation.lazy.rememberLazyListState(initialFirstVisibleItemIndex = 7300 - 3)

    val currentMonthYear by remember {
        androidx.compose.runtime.derivedStateOf {
            val idx = listState.firstVisibleItemIndex + 2
            val safeIdx = idx.coerceIn(0, dates.lastIndex.coerceAtLeast(0))
            val d = if (dates.isNotEmpty()) dates[safeIdx] else selectedDate
            monthYearFormat.format(d)
        }
    }

    Surface(
        color = colors.surfaceVariant,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = currentMonthYear, 
                color = colors.textPrimary, 
                fontSize = 16.sp, 
                fontWeight = FontWeight.Bold, 
                modifier = Modifier.fillMaxWidth(), 
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            androidx.compose.foundation.lazy.LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dates) { date ->
                    val isSelected = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
                    val dayNum = SimpleDateFormat("d", Locale.getDefault()).format(date)
                    val dayNameStr = SimpleDateFormat("EEE", Locale.getDefault()).format(date)
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onDateSelected(date) }.padding(horizontal = 4.dp)
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier.size(40.dp).background(PrimaryBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(dayNum, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(dayNameStr, color = PrimaryOrange, fontSize = 12.sp)
                        } else {
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(dayNum, color = colors.textPrimary, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(dayNameStr, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodayClassCard(
    subject: SubjectEntity,
    routine: RoutineEntity,
    attendanceStatus: String?,
    isEditable: Boolean,
    onMarkAttendance: (String) -> Unit,
    onStickyNotesClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val attended = subject.attended
    val missed = subject.missed
    val total = attended + missed
    val req = subject.requiredPercentage
    val percentage = if (total > 0) (attended * 100 / total) else 100

    var statusText = ""
    var statusColor = Color.White
    
    if (total == 0) {
        statusText = "No classes yet"
        statusColor = PrimaryBlue
    } else {
        val currentPct = (attended.toDouble() / total.toDouble()) * 100.0
        if (currentPct >= req) {
            var canMiss = 0
            while (((attended.toDouble() / (total + canMiss + 1).toDouble()) * 100.0) >= req) {
                canMiss++
            }
            if (canMiss > 0) {
                statusText = "Can miss $canMiss class${if (canMiss > 1) "es" else ""}"
                statusColor = Color(0xFF8BC34A)
            } else {
                statusText = "Can't miss any class"
                statusColor = Color(0xFFF39C12)
            }
        } else {
            var mustAttend = 0
            while ((((attended + mustAttend).toDouble() / (total + mustAttend).toDouble()) * 100.0) < req) {
                mustAttend++
            }
            statusText = "Attend $mustAttend class${if (mustAttend > 1) "es" else ""}"
            statusColor = Color(0xFFEA4335)
        }
    }

    Surface(
        color = colors.surface,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Progress Circle
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                    CircularProgressIndicator(
                        progress = { percentage.toFloat() / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = PrimaryBlue,
                        strokeWidth = 4.dp,
                        trackColor = PrimaryRed
                    )
                    Text(if (total == 0) "N/A" else "$percentage%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(subject.subjectCode, color = PrimaryOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(subject.name, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            if (subject.professorName.isNotEmpty()) {
                                Text(subject.professorName, color = colors.textSecondary, fontSize = 12.sp)
                            }
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.StickyNote2, 
                                contentDescription = "Sticky Notes", 
                                tint = PrimaryOrange, 
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onStickyNotesClick() }
                            )
                        }
                    }

                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Text("Attended: $attended", color = PrimaryBlue, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Missed: $missed", color = PrimaryRed, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Req.: ${req}%", color = colors.textSecondary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(statusText, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (attendanceStatus == null) {
                if (isEditable) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AttendanceButton("Present", PrimaryGreen, Modifier.weight(1f)) { onMarkAttendance("Present") }
                        AttendanceButton("Absent", PrimaryRed, Modifier.weight(1f)) { onMarkAttendance("Absent") }
                        AttendanceButton("Cancelled", colors.textSecondary, Modifier.weight(1f)) { onMarkAttendance("Cancelled") }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(45.dp).background(colors.surfaceVariant, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Not Marked", color = Color.Gray, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                }
            } else {
                val color = when (attendanceStatus) {
                    "Present" -> PrimaryGreen
                    "Absent" -> PrimaryRed
                    else -> colors.textSecondary
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, color, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(attendanceStatus, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun AttendanceButton(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        modifier = modifier.height(45.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StickyNotesScreen(
    subjectCode: String,
    viewModel: EchoViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddNote: () -> Unit
) {
    val colors = LocalAppColors.current
    val notes by viewModel.getStickyNotesForSubject(subjectCode).collectAsState(initial = emptyList())
    var showDeleteSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                }
                Text("Sticky Notes", color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onNavigateToAddNote) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Add", tint = colors.textPrimary, modifier = Modifier.size(28.dp))
                }
            }
        },
        containerColor = colors.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (notes.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
                         // Drawing overlapping squares to mimic the icons
                         Box(modifier = Modifier.size(80.dp).background(Color(0xFFE91E63), RoundedCornerShape(8.dp)))
                         Box(modifier = Modifier.size(80.dp).padding(start = 20.dp, top = 20.dp).background(Color(0xFFFFC107), RoundedCornerShape(8.dp)))
                         Box(modifier = Modifier.size(80.dp).padding(start = 40.dp, top = 40.dp).background(Color(0xFF8BC34A), RoundedCornerShape(8.dp)))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Capture your thoughts before\nthey fly away!", color = colors.textPrimary, textAlign = TextAlign.Center, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateToAddNote,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Get started", color = Color.White)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(notes) { note ->
                        Surface(
                            color = colors.surfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.border),
                            modifier = Modifier.width(180.dp).height(180.dp)
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                Column {
                                    Text(note.title, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text(note.description, color = colors.textSecondary, fontSize = 14.sp)
                                }
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = PrimaryRed,
                                    modifier = Modifier.align(Alignment.BottomEnd).clickable {
                                        viewModel.deleteStickyNote(note)
                                        showDeleteSuccess = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            if (showDeleteSuccess) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
                    color = colors.background,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sticky note deleted successfully!", color = colors.textPrimary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                    }
                }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showDeleteSuccess = false
                }
            }
        }
    }
}

@Composable
fun AddStickyNoteScreen(
    subjectCode: String,
    viewModel: EchoViewModel,
    onNavigateBack: () -> Unit
) {
    val colors = LocalAppColors.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                }
                Spacer(modifier = Modifier.width(32.dp))
                Text("Add Sticky Note", color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text("Enter title", color = colors.textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Title", color = Color.DarkGray) },
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = colors.textSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column {
                Text("Enter description", color = colors.textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Description", color = Color.DarkGray) },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = colors.textSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        viewModel.saveStickyNote(subjectCode, title, description)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp)) // Mimicking the add list icon
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentScreen: String, onScreenSelected: (String) -> Unit) {
    val colors = LocalAppColors.current
    val items = listOf(
        NavigationItem("Attendance", Icons.Default.BarChart),
        NavigationItem("Today", Icons.Default.Today),
        NavigationItem("Routine", Icons.Outlined.CalendarMonth),
        NavigationItem("Holidays", Icons.Default.Celebration),
        NavigationItem("Settings", Icons.Default.Settings)
    )

    NavigationBar(
        containerColor = colors.background,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.title,
                onClick = { onScreenSelected(item.title) },
                icon = { Icon(item.icon, contentDescription = item.title, modifier = Modifier.size(24.dp)) },
                label = { Text(item.title, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4A90E2),
                    selectedTextColor = Color(0xFF4A90E2),
                    unselectedIconColor = Color(0xFF555555),
                    unselectedTextColor = Color(0xFF555555),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavigationItem(val title: String, val icon: ImageVector)

@Composable
fun AttendanceScreen(
    viewModel: EchoViewModel,
    scrollState: androidx.compose.foundation.lazy.LazyListState,
    onNavigateToAddSubject: () -> Unit,
    onEditSubject: (SubjectEntity) -> Unit,
    onNavigateToStickyNotes: (String) -> Unit,
    onNavigateToSubjectDetail: (String) -> Unit,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    val colors = LocalAppColors.current
    var selectedTab by remember { mutableStateOf("Theory") }
    val subjects by viewModel.allSubjects.collectAsState()

    val displayDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date())
    val todayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = colors.textPrimary,
                modifier = Modifier.size(24.dp).clickable { onThemeToggle() }
            )
            Text("Attendance Tracker", fontSize = 20.sp, fontWeight = FontWeight.Normal, color = colors.textPrimary)
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = "Add",
                tint = colors.textPrimary,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onNavigateToAddSubject() }
            )
        }

        // Date Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = colors.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text(
                    displayDate,
                    color = Color(0xFF4A90E2),
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
            Surface(
                color = colors.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text(
                    todayName,
                    color = Color(0xFF4A90E2),
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Theory/Lab Toggle
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceVariant)
        ) {
            listOf("Theory", "Lab").forEach { tab ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (selectedTab == tab) Color(0xFF444444) else Color.Transparent)
                        .clickable { selectedTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(tab, color = colors.textPrimary, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (subjects.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Empty",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Your presence matters more\nthan you think!",
                        color = colors.textPrimary, fontSize = 16.sp, textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { onNavigateToAddSubject() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Get started", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                items(subjects.filter { it.category == selectedTab }) { subject ->
                    SubjectCard(
                        subject = subject, 
                        onEdit = { onEditSubject(subject) },
                        onDelete = { viewModel.deleteSubject(subject) },
                        onStickyNotesClick = { onNavigateToStickyNotes(subject.subjectCode) },
                        onCalendarClick = { onNavigateToSubjectDetail(subject.subjectCode) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectCard(
    subject: SubjectEntity, 
    onEdit: () -> Unit, 
    onDelete: () -> Unit,
    onStickyNotesClick: () -> Unit, 
    onCalendarClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val attended = subject.attended
    val missed = subject.missed
    val total = attended + missed
    val req = subject.requiredPercentage
    
    val percentage = if (total > 0) (attended * 100 / total) else 100
    
    // Status Logic
    var statusText = ""
    var statusColor = Color.White
    
    if (total == 0) {
        statusText = "No classes yet"
        statusColor = PrimaryBlue
    } else {
        val currentPct = (attended.toDouble() / total.toDouble()) * 100.0
        if (currentPct >= req) {
            // Can miss logic
            var canMiss = 0
            while (((attended.toDouble() / (total + canMiss + 1).toDouble()) * 100.0) >= req) {
                canMiss++
            }
            if (canMiss > 0) {
                statusText = "Can miss $canMiss class${if (canMiss > 1) "es" else ""}"
                statusColor = Color(0xFF8BC34A) // Greenish
            } else {
                statusText = "Can't miss any class"
                statusColor = Color(0xFFF39C12) // Orange
            }
        } else {
            // Must attend logic
            var mustAttend = 0
            while ((((attended + mustAttend).toDouble() / (total + mustAttend).toDouble()) * 100.0) < req) {
                mustAttend++
            }
            statusText = "Attend $mustAttend class${if (mustAttend > 1) "es" else ""}"
            statusColor = Color(0xFFEA4335) // Red
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart) {
                showDeleteDialog = true
            }
            false
        }
    )

    if (showDeleteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = colors.cardBackground,
            title = {
                Text(text = "Are you sure?", color = Color.White, fontSize = 20.sp)
            },
            text = {
                Text("Do you want to delete this?", color = Color.White)
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Yes", color = Color(0xFFE57373))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No", color = Color.White)
                }
            }
        )
    }

    androidx.compose.material3.SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = Color(0xFF8B0000)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
                    .padding(24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        content = {
            Surface(
                color = colors.surface,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Progress Circle
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(70.dp)) {
                    CircularProgressIndicator(
                        progress = { percentage.toFloat() / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF4A90E2),
                        strokeWidth = 6.dp,
                        trackColor = Color(0xFFEA4335)
                    )
                    Text(if (total == 0) "N/A" else "$percentage%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(subject.subjectCode, color = PrimaryOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                subject.name,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            if (subject.professorName.isNotEmpty()) {
                                Text(subject.professorName, color = colors.textSecondary, fontSize = 12.sp)
                            }
                        }
                        Row {
                            Icon(
                                Icons.Default.StickyNote2, 
                                contentDescription = "Sticky Notes", 
                                tint = Color(0xFFF1C40F), 
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onStickyNotesClick() }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                Icons.Default.CalendarToday, 
                                contentDescription = "Calendar", 
                                tint = Color(0xFF2ECC71), 
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onCalendarClick() }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                Icons.Default.Edit, 
                                contentDescription = "Edit", 
                                tint = Color(0xFF4A90E2), 
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onEdit() }
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Surface(
                            color = colors.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Attended: ${subject.attended}",
                                color = Color(0xFF4A90E2),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = colors.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Missed: ${subject.missed}",
                                color = Color(0xFFEA4335),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = colors.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Req.: ${subject.requiredPercentage} %",
                                color = Color(0xFF555555),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = statusText,
                color = statusColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 86.dp)
            )
        }
    }
    })
}

@Composable
fun RoutineScreen(
    viewModel: EchoViewModel,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    val colors = LocalAppColors.current
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val displayDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    val routines by viewModel.allRoutines.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()

    // Helper to convert time string to minutes from midnight
    fun timeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(" ")
        if (parts.size != 2) return 0
        val timeParts = parts[0].split(":")
        var h = timeParts[0].toIntOrNull() ?: 0
        val m = if (timeParts.size > 1) timeParts[1].toIntOrNull() ?: 0 else 0
        val amPm = parts[1]
        if (amPm == "PM" && h < 12) h += 12
        if (amPm == "AM" && h == 12) h = 0
        return h * 60 + m
    }

    // Extract all unique start times from the database to make it dynamic
    val timeSlots = remember(routines) {
        routines.map { it.startTime }
            .distinct()
            .sortedBy { timeToMinutes(it) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = colors.textPrimary,
                modifier = Modifier.clickable { onThemeToggle() }
            )
            Text("Routine", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Spacer(modifier = Modifier.width(24.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        val horizontalScrollState = rememberScrollState()

        Column(modifier = Modifier.fillMaxSize().horizontalScroll(horizontalScrollState)) {
            // Day Headers
            Row(modifier = Modifier.background(Color.Transparent)) {
                Box(modifier = Modifier.width(80.dp).padding(12.dp), contentAlignment = Alignment.Center) {
                    Text("Time", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                displayDays.forEach { day ->
                    Box(modifier = Modifier.width(100.dp).padding(12.dp), contentAlignment = Alignment.Center) {
                        Text(day, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (timeSlots.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No routine set. Add subjects with schedule first.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                // Dynamic Time Grid
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(timeSlots) { time ->
                        val currentMinutes = timeToMinutes(time)
                        Row(modifier = Modifier.height(80.dp)) {
                            // Dynamic Time Column
                            Box(
                                modifier = Modifier.width(80.dp).fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(time, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            }

                            // Subject Slots
                            days.forEach { day ->
                                // Find any routine that covers this specific time slot
                                val coveringRoutine = routines.find { routine ->
                                    routine.dayOfWeek == day &&
                                    timeToMinutes(routine.startTime) <= currentMinutes &&
                                    timeToMinutes(routine.endTime) > currentMinutes
                                }

                                val subject = coveringRoutine?.let { r -> subjects.find { it.subjectCode == r.subjectCode } }

                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .fillMaxHeight()
                                        .padding(4.dp)
                                        .border(0.5.dp, Color.DarkGray.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (subject != null) {
                                        Surface(
                                            color = getSubjectColor(subject.subjectCode),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                                                Text(
                                                    text = subject.name,
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 3
                                                )
                                            }
                                        }
                                    } else {
                                        // Empty slot
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(subjectCode: String, viewModel: EchoViewModel, onNavigateBack: () -> Unit) {
    val colors = LocalAppColors.current
    val subjects by viewModel.allSubjects.collectAsState()
    val subject = subjects.find { it.subjectCode == subjectCode } ?: return
    
    val allRecords by viewModel.getAllAttendanceRecordsForSubject(subjectCode).collectAsState(initial = emptyList())
    
    var showMarkingSheet by remember { mutableStateOf(false) }
    var selectedDateForMarking by remember { mutableStateOf<Date?>(null) }
    
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(subject.name, color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = colors.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            // Stats Row
            Surface(
                color = colors.surfaceVariant,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem("${subject.attended + subject.missed}", "Total", colors.textPrimary)
                    StatItem("${subject.attended}", "Attended", PrimaryGreen)
                    StatItem("${subject.missed}", "Missed", PrimaryRed)
                    StatItem("${allRecords.count { it.status == "Cancelled" }}", "Cancelled", colors.textSecondary)
                    
                    val pct = if (subject.attended + subject.missed > 0) 
                        (subject.attended * 100 / (subject.attended + subject.missed)) 
                        else 100
                    StatItem("$pct%", "Current", PrimaryGreen)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Attendance Activity", color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            // Legend
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LegendItem("Present", PrimaryGreen)
                LegendItem("Absent", PrimaryRed)
                LegendItem("Cancelled", colors.textSecondary)
                LegendItem("Holiday", HolidayYellow)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Tap on date to enter attendance", 
                color = colors.textSecondary, 
                fontSize = 12.sp, 
                modifier = Modifier.fillMaxWidth(), 
                textAlign = TextAlign.Center
            )

            val allHolidays by viewModel.allHolidays.collectAsState()
            val routinesForSubject = remember(subjectCode, viewModel.allRoutines.collectAsState().value) {
                viewModel.allRoutines.value.filter { it.subjectCode == subjectCode }
            }

            val context = androidx.compose.ui.platform.LocalContext.current

            // Calendar Grid
            AttendanceCalendarGrid(
                records = allRecords,
                holidays = allHolidays,
                routines = routinesForSubject,
                onDateClick = { date ->
                    selectedDateForMarking = date
                    showMarkingSheet = true
                },
                onMarkingRestricted = { message ->
                    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    if (showMarkingSheet && selectedDateForMarking != null) {
        ModalBottomSheet(
            onDismissRequest = { showMarkingSheet = false },
            sheetState = sheetState,
            containerColor = colors.surfaceVariant,
            dragHandle = { Spacer(modifier = Modifier.height(24.dp)) }
        ) {
            AttendanceMarkingSheet(
                date = selectedDateForMarking!!,
                existingRecord = allRecords.find { 
                    it.date == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDateForMarking!!)
                },
                onSave = { isOffDay, attendedCount, missedCount ->
                    viewModel.updateManualAttendance(subjectCode, selectedDateForMarking!!, isOffDay, attendedCount, missedCount)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showMarkingSheet = false
                    }
                },
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showMarkingSheet = false
                    }
                }
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    val colors = LocalAppColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = colors.textSecondary, fontSize = 10.sp)
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    val colors = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = colors.textSecondary, fontSize = 12.sp)
    }
}

@Composable
fun AttendanceCalendarGrid(
    records: List<AttendanceRecordEntity>,
    holidays: List<HolidayEntity>,
    routines: List<RoutineEntity>,
    onDateClick: (Date) -> Unit,
    onMarkingRestricted: (String) -> Unit
) {
    val colors = LocalAppColors.current
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = 1200,
        pageCount = { 2400 }
    )

    androidx.compose.foundation.pager.HorizontalPager(state = pagerState) { page ->
        val monthOffset = page - 1200
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, monthOffset)

        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dates = remember(monthOffset) {
            val list = mutableListOf<Date?>()
            // Add padding for start of month
            repeat(firstDayOfWeek - 1) { list.add(null) }
            // Add actual dates
            for (i in 1..daysInMonth) {
                val c = calendar.clone() as Calendar
                c.set(Calendar.DAY_OF_MONTH, i)
                list.add(c.time)
            }
            list
        }

        val rows = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        Surface(
            color = colors.surface,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Month Header
                val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
                Text(monthName, color = colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

                // Day Labels horizontal
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rows.forEach {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(it, color = colors.textPrimary, fontSize = 12.sp)
                        }
                    }
                }

                // Grid of dates
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(dates) { date ->
                        if (date != null) {
                            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                            val dayRecords = records.filter { it.date == dateStr }
                            val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(date)
                            val dayNameFull = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
                            
                            val isHoliday = holidays.any { it.date == dateStr }
                            
                            // Date Comparison for Future
                            val todayCal = Calendar.getInstance().apply {
                                time = Date()
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val targetCal = Calendar.getInstance().apply {
                                time = date
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val isFuture = targetCal.after(todayCal)
                            val isClassDay = routines.any { it.dayOfWeek == dayNameFull }

                            val bgColor = if (isHoliday) {
                                HolidayYellow
                            } else if (dayRecords.any { it.status == "Cancelled" }) {
                                colors.textSecondary
                            } else if (dayRecords.any { it.status == "Present" }) {
                                PrimaryGreen
                            } else if (dayRecords.any { it.status == "Absent" }) {
                                PrimaryRed
                            } else if (isFuture) {
                                Color.DarkGray // Locked future color
                            } else {
                                colors.surfaceVariant
                            }
                            
                            val contentAlpha = if (isFuture || (!isClassDay && !isHoliday)) 0.5f else 1.0f

                            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(bgColor.copy(alpha = contentAlpha), RoundedCornerShape(4.dp))
                                        .clickable {
                                            if (isFuture) {
                                                onMarkingRestricted("Attendance cannot be marked for future dates!")
                                            } else if (isHoliday) {
                                                onMarkingRestricted("Attendance cannot be marked on holidays!")
                                            } else if (!isClassDay) {
                                                onMarkingRestricted("No class scheduled for $dayNameFull")
                                            } else {
                                                onDateClick(date)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(dayOfMonth, color = (if (isHoliday) Color.Black else colors.textPrimary).copy(alpha = contentAlpha), fontSize = 12.sp)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceMarkingSheet(
    date: Date,
    existingRecord: AttendanceRecordEntity?,
    onSave: (Boolean, Int, Int) -> Unit,
    onCancel: () -> Unit
) {
    val colors = LocalAppColors.current
    var isOffDay by remember { mutableStateOf(existingRecord?.status == "Cancelled") }
    var selectedStatus by remember { 
        mutableStateOf(
            if (existingRecord?.status == "Absent") "Absent" else "Present"
        ) 
    }

    val dateFormatted = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)

    Column(
        modifier = Modifier.padding(24.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(dayName, color = colors.textSecondary, fontSize = 14.sp)
        Text(dateFormatted, color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mark as Cancelled", color = colors.textPrimary, fontWeight = FontWeight.Bold)
            Switch(
                checked = isOffDay, 
                onCheckedChange = { isOffDay = it },
                colors = SwitchDefaults.colors(checkedThumbColor = colors.textPrimary, checkedTrackColor = PrimaryBlue)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val presentColor = if (isOffDay) Color.DarkGray else if (selectedStatus == "Present") PrimaryGreen else Color.Gray
            val absentColor = if (isOffDay) Color.DarkGray else if (selectedStatus == "Absent") PrimaryRed else Color.Gray

            AttendanceButton(
                text = "Present", 
                color = presentColor, 
                modifier = Modifier.weight(1f)
            ) {
                if (!isOffDay) selectedStatus = "Present"
            }

            AttendanceButton(
                text = "Absent", 
                color = absentColor, 
                modifier = Modifier.weight(1f)
            ) {
                if (!isOffDay) selectedStatus = "Absent"
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("Cancel", color = colors.textSecondary)
            }
            Button(
                onClick = {
                    val attended = if (!isOffDay && selectedStatus == "Present") 1 else 0
                    val missed = if (!isOffDay && selectedStatus == "Absent") 1 else 0
                    onSave(isOffDay, attended, missed)
                },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Save", color = colors.textPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
