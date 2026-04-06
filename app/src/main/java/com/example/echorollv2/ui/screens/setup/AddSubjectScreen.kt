package com.example.echorollv2.ui.screens.setup

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echorollv2.R
import com.example.echorollv2.data.local.entity.SubjectEntity
import com.example.echorollv2.ui.theme.CardBackground
import com.example.echorollv2.ui.theme.DarkBackground
import com.example.echorollv2.ui.theme.PrimaryBlue
import com.example.echorollv2.ui.theme.PrimaryOrange
import com.example.echorollv2.ui.theme.PrimaryPurple
import com.example.echorollv2.ui.theme.PrimaryRed
import com.example.echorollv2.ui.theme.TextGray
import com.example.echorollv2.ui.theme.TextWhite
import com.example.echorollv2.ui.theme.TheoryGreen
import java.util.Calendar

data class DaySchedule(
    val dayName: String,
    var isEnabled: Boolean = false,
    var startTime: String = "Set Time",
    var endTime: String = "Set Time"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectScreen(
    onNavigateBack: () -> Unit,
    onSaveSubject: (String, String, String, String, Int, Int, Int, List<DaySchedule>) -> Unit,
    onDeleteSubject: ((SubjectEntity) -> Unit)? = null,
    initialSubject: SubjectEntity? = null,
    initialRoutines: List<DaySchedule> = emptyList()
) {
    val isEditing = initialSubject != null

    // --- STATE MANAGEMENT ---
    var category by remember { mutableStateOf(initialSubject?.category ?: "Theory") }
    var subjectCode by remember { mutableStateOf(initialSubject?.subjectCode ?: "") }
    var subjectName by remember { mutableStateOf(initialSubject?.name ?: "") }
    var professorName by remember { mutableStateOf(initialSubject?.professorName ?: "") }

    var attended by remember { mutableStateOf(initialSubject?.attended ?: 0) }
    var missed by remember { mutableStateOf(initialSubject?.missed ?: 0) }
    var required by remember { mutableStateOf(initialSubject?.requiredPercentage ?: 75) }

    var isRoutineExpanded by remember { mutableStateOf(false) }

    val weeklySchedule = remember {
        mutableStateListOf<DaySchedule>().apply {
            val baseDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            baseDays.forEach { dayName ->
                val existing = initialRoutines.find { it.dayName == dayName }
                add(existing ?: DaySchedule(dayName))
            }
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Update Attendance" else "Add Attendance", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isEditing) {
                    Button(
                        onClick = { 
                            initialSubject?.let { onDeleteSubject?.invoke(it) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
                
                Button(
                    onClick = {
                        onSaveSubject(
                            subjectCode, subjectName, category, professorName,
                            attended, missed, required, weeklySchedule.toList()
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(if (isEditing) 1.5f else 1f)
                        .height(50.dp)
                ) {
                    Text(if (isEditing) "Update" else "Add Subject", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Info Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryPurple, RoundedCornerShape(12.dp))
                    .background(PrimaryPurple.copy(alpha = 0.1f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "• Swipe the card left <- right to Delete\n",
                    color = TextWhite,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 2. Category Toggle
            Column {
                Text("Category", color = TextGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryChip("Theory", category == "Theory") { category = "Theory" }
                    CategoryChip("Lab", category == "Lab") { category = "Lab" }
                }
            }

            // 3. Text Inputs
            CustomInputField("Enter Subject Code", subjectCode, "E.g., CS-301", enabled = !isEditing) { subjectCode = it }
            CustomInputField("Enter Subject Name", subjectName, "E.g., Operating Systems") { subjectName = it }
            CustomInputField("Enter Professor (Optional)", professorName, "E.g., Dr. Smith") { professorName = it }

            // 4. Number Steppers
            StepperRow("Classes attended", "Attended", attended, PrimaryBlue) { attended = it }
            StepperRow("Classes missed", "Missed", missed, PrimaryRed) { missed = it }
            StepperRow("% of classes required", "Required", required, TextWhite, isPercentage = true) { required = it }

            // 5. Weekly Schedule (Expandable)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isRoutineExpanded = !isRoutineExpanded }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Weekly Routine", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Set schedule for push notifications", color = TextGray, fontSize = 12.sp)
                    }
                    Icon(
                        imageVector = if (isRoutineExpanded) Icons.Default.Remove else Icons.Default.Add,
                        contentDescription = "Toggle",
                        tint = PrimaryOrange
                    )
                }

                AnimatedVisibility(visible = isRoutineExpanded) {
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        weeklySchedule.forEachIndexed { index, schedule ->
                            DayTimeRow(
                                schedule = schedule,
                                onUpdate = { updatedSchedule -> weeklySchedule[index] = updatedSchedule }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// --- REUSABLE COMPONENTS ---

@Composable
fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TheoryGreen else CardBackground)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(label, color = if (isSelected) Color.Black else TextWhite, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CustomInputField(label: String, value: String, placeholder: String, enabled: Boolean = true, onValueChange: (String) -> Unit) {
    Column {
        Text(label, color = TextGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.DarkGray) },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                disabledContainerColor = CardBackground.copy(alpha = 0.5f),
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                disabledTextColor = TextWhite.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

@Composable
fun StepperRow(title: String, label: String, value: Int, accentColor: Color, isPercentage: Boolean = false, onValueChange: (Int) -> Unit) {
    Column {
        Text(title, color = TextGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (value > 0) onValueChange(value - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Minus", tint = TextWhite)
                }
                Box(
                    modifier = Modifier.background(DarkBackground, RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${value}${if (isPercentage) "%" else ""}", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { if (value < 100) onValueChange(value + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Plus", tint = TextWhite)
                }
            }
        }
    }
}

@Composable
fun DayTimeRow(schedule: DaySchedule, onUpdate: (DaySchedule) -> Unit) {
    val context = LocalContext.current

    fun showTimePicker(isStart: Boolean) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(context, R.style.CustomTimePickerTheme, { _, hourOfDay, minute ->
            val amPm = if (hourOfDay >= 12) "PM" else "AM"
            val displayHour = if (hourOfDay > 12) hourOfDay - 12 else if (hourOfDay == 0) 12 else hourOfDay
            val timeString = String.format("%02d:%02d %s", displayHour, minute, amPm)

            if (isStart) {
                onUpdate(schedule.copy(startTime = timeString, isEnabled = true))
            } else {
                onUpdate(schedule.copy(endTime = timeString, isEnabled = true))
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Checkbox(
                checked = schedule.isEnabled,
                onCheckedChange = { isChecked ->
                    if (!isChecked) {
                        onUpdate(schedule.copy(isEnabled = false, startTime = "Set Time", endTime = "Set Time"))
                    } else {
                        onUpdate(schedule.copy(isEnabled = true))
                    }
                },
                colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
            )
            Text(schedule.dayName, color = if (schedule.isEnabled) TextWhite else TextGray, fontWeight = FontWeight.Bold)
        }

        if (schedule.isEnabled) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Start: ${schedule.startTime}",
                    color = PrimaryBlue,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { showTimePicker(isStart = true) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "End: ${schedule.endTime}",
                    color = PrimaryRed,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { showTimePicker(isStart = false) }
                )
            }
        } else {
            Text("Off", color = Color.DarkGray, fontSize = 12.sp)
        }
    }
}