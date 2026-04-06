package com.example.echorollv2.ui.screens.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echorollv2.data.local.entity.HolidayEntity
import com.example.echorollv2.ui.theme.LocalAppColors
import com.example.echorollv2.ui.theme.PrimaryBlue
import com.example.echorollv2.ui.theme.PrimaryRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HolidaysScreen(
    holidays: List<HolidayEntity>,
    onNavigateBack: () -> Unit,
    onDeleteHoliday: (HolidayEntity) -> Unit,
    onAddManualHoliday: (String, String) -> Unit,
    onUpdateHoliday: (HolidayEntity) -> Unit
) {
    val colors = LocalAppColors.current
    var showAddDialog by remember { mutableStateOf(false) }
    var holidayToEdit by remember { mutableStateOf<HolidayEntity?>(null) }

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
                Text("Holidays", color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = colors.textPrimary)
                }
            }
        },
        containerColor = colors.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (holidays.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No holidays found. Add some or fetch them in Settings!", color = colors.textSecondary)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(holidays) { holiday ->
                        Surface(
                            color = colors.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(holiday.name, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(holiday.date, color = PrimaryBlue, fontSize = 14.sp)
                                    Text(holiday.type, color = colors.textSecondary, fontSize = 12.sp)
                                }
                                Row {
                                    IconButton(onClick = { holidayToEdit = holiday }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = colors.textSecondary)
                                    }
                                    IconButton(onClick = { onDeleteHoliday(holiday) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PrimaryRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (showAddDialog) {
            var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
            var name by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Manual Holiday", color = colors.textPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary,
                                focusedLabelColor = colors.textPrimary,
                                unfocusedLabelColor = colors.textSecondary
                            )
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Holiday Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary,
                                focusedLabelColor = colors.textPrimary,
                                unfocusedLabelColor = colors.textSecondary
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (name.isNotBlank() && date.isNotBlank()) {
                            onAddManualHoliday(date, name)
                            showAddDialog = false
                        }
                    }) {
                        Text("Add", color = PrimaryBlue)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = colors.textSecondary) }
                },
                containerColor = colors.surfaceVariant
            )
        }

        holidayToEdit?.let { holiday ->
            var date by remember(holiday) { mutableStateOf(holiday.date) }
            var name by remember(holiday) { mutableStateOf(holiday.name) }
            
            AlertDialog(
                onDismissRequest = { holidayToEdit = null },
                title = { Text("Edit Holiday", color = colors.textPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary,
                                focusedLabelColor = colors.textPrimary,
                                unfocusedLabelColor = colors.textSecondary
                            )
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Holiday Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary,
                                focusedLabelColor = colors.textPrimary,
                                unfocusedLabelColor = colors.textSecondary
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (name.isNotBlank() && date.isNotBlank()) {
                            onUpdateHoliday(holiday.copy(name = name, date = date))
                            holidayToEdit = null
                        }
                    }) {
                        Text("Update", color = PrimaryBlue)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { holidayToEdit = null }) { Text("Cancel", color = colors.textSecondary) }
                },
                containerColor = colors.surfaceVariant
            )
        }
    }
}
