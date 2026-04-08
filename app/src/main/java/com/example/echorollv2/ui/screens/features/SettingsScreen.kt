package com.example.echorollv2.ui.screens.features

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echorollv2.ui.theme.LocalAppColors
import com.example.echorollv2.ui.theme.PrimaryBlue

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.echorollv2.ui.viewmodels.EchoViewModel

@Composable
fun SettingsScreen(
    currentCountryCode: String,
    currentSubdivisionCode: String,
    fetchStatus: EchoViewModel.HolidayFetchStatus,
    errorMessage: String?,
    onSaveRegion: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onFetchHolidays: (String, String) -> Unit,
    onResetFetchStatus: () -> Unit
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    var countryInput by remember { mutableStateOf(currentCountryCode) }
    var stateInput by remember { mutableStateOf(currentSubdivisionCode) }
    var showStateDialog by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }
    var countrySearchQuery by remember { mutableStateOf("") }

    val countryList = remember {
        java.util.Locale.getISOCountries().map { code ->
            val locale = java.util.Locale("", code)
            code to locale.displayCountry
        }.sortedBy { it.second }
    }

    val filteredCountries = remember(countrySearchQuery) {
        if (countrySearchQuery.isEmpty()) countryList
        else countryList.filter { it.second.contains(countrySearchQuery, ignoreCase = true) }
    }

    val indianStates = remember {
        listOf(
            "None" to "",
            "Andhra Pradesh" to "AP",
            "Arunachal Pradesh" to "AR",
            "Assam" to "AS",
            "Bihar" to "BR",
            "Chhattisgarh" to "CT",
            "Delhi" to "DL",
            "Goa" to "GA",
            "Gujarat" to "GJ",
            "Haryana" to "HR",
            "Himachal Pradesh" to "HP",
            "Jharkhand" to "JH",
            "Karnataka" to "KA",
            "Kerala" to "KL",
            "Madhya Pradesh" to "MP",
            "Maharashtra" to "MH",
            "Manipur" to "MN",
            "Meghalaya" to "ML",
            "Mizoram" to "MZ",
            "Nagaland" to "NL",
            "Odisha" to "OR",
            "Punjab" to "PB",
            "Rajasthan" to "RJ",
            "Sikkim" to "SK",
            "Tamil Nadu" to "TN",
            "Telangana" to "TG",
            "Tripura" to "TR",
            "Uttar Pradesh" to "UP",
            "Uttarakhand" to "UT",
            "West Bengal" to "WB"
        )
    }

    LaunchedEffect(fetchStatus) {
        when (fetchStatus) {
            EchoViewModel.HolidayFetchStatus.SUCCESS -> {
                Toast.makeText(context, "Holidays Fetched successfully!", Toast.LENGTH_SHORT).show()
                onResetFetchStatus()
            }
            EchoViewModel.HolidayFetchStatus.ERROR -> {
                val msg = errorMessage ?: "Server error or invalid data. Please try again later."
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                onResetFetchStatus()
            }
            EchoViewModel.HolidayFetchStatus.EMPTY -> {
                Toast.makeText(context, "No automated holidays found for this region/year.", Toast.LENGTH_LONG).show()
                onResetFetchStatus()
            }
            EchoViewModel.HolidayFetchStatus.NO_INTERNET -> {
                Toast.makeText(context, "Please check your internet connection and try again.", Toast.LENGTH_LONG).show()
                onResetFetchStatus()
            }
            else -> {}
        }
    }

    if (showCountryDialog) {
        AlertDialog(
            onDismissRequest = { showCountryDialog = false; countrySearchQuery = "" },
            title = { Text("Select Country", color = colors.textPrimary) },
            text = {
                Column {
                    OutlinedTextField(
                        value = countrySearchQuery,
                        onValueChange = { countrySearchQuery = it },
                        placeholder = { Text("Search Country...") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                    ) {
                        items(filteredCountries.size) { index ->
                            val (code, name) = filteredCountries[index]
                            DropdownMenuItem(
                                text = { Text(name, color = colors.textPrimary) },
                                onClick = {
                                    countryInput = code
                                    showCountryDialog = false
                                    countrySearchQuery = ""
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCountryDialog = false; countrySearchQuery = "" }) { Text("Cancel", color = colors.textPrimary) }
            },
            containerColor = colors.surfaceVariant
        )
    }

    if (showStateDialog) {
        AlertDialog(
            onDismissRequest = { showStateDialog = false },
            title = { Text("Select State", color = colors.textPrimary) },
            text = {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                ) {
                    items(indianStates.size) { index ->
                        val (name, code) = indianStates[index]
                        DropdownMenuItem(
                            text = { Text(name, color = colors.textPrimary) },
                            onClick = {
                                stateInput = code
                                showStateDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStateDialog = false }) { Text("Cancel", color = colors.textPrimary) }
            },
            containerColor = colors.surfaceVariant
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
                }
                Spacer(modifier = Modifier.width(32.dp))
                Text("Settings", color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column {
                Text("Region Setting", color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Select your Country and State to automatically fetch local holidays.", color = colors.textSecondary, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Select Country", color = colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showCountryDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                ) {
                    val countryName = countryList.find { it.first == countryInput }?.second ?: "Select Country"
                    Text(countryName)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (countryInput == "IN") {
                    Text("Select State", color = colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showStateDialog = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                    ) {
                        val stateName = indianStates.find { it.second == stateInput }?.first ?: "Select State"
                        Text(stateName)
                    }
                } else {
                    Text("State/Subdivision Code (Optional)", color = colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = stateInput,
                        onValueChange = { stateInput = it.uppercase() },
                        placeholder = { Text("e.g. NY for New York", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { 
                        onSaveRegion(countryInput, stateInput)
                        onFetchHolidays(countryInput, stateInput)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(25.dp),
                    enabled = fetchStatus != EchoViewModel.HolidayFetchStatus.LOADING
                ) {
                    if (fetchStatus == EchoViewModel.HolidayFetchStatus.LOADING) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Fetch Holidays")
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                HorizontalDivider(color = colors.textSecondary.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(24.dp))

                Text("Notifications & Reliability", color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("If you missed a reminder, use these tools to verify and reset your class alarms.", color = colors.textSecondary, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        com.example.echorollv2.services.NotificationHelper.sendNotification(
                            context,
                            "Test Notification \uD83D\uDD14",
                            "If you can see this, notifications are working perfectly!",
                            9999
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                ) {
                    Text("Send Test Notification")
                }

                Spacer(modifier = Modifier.height(12.dp))

                val workManager = androidx.work.WorkManager.getInstance(context)
                Button(
                    onClick = {
                        val immediateRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.echorollv2.services.DailyCheckWorker>().build()
                        workManager.enqueueUniqueWork(
                            "DailyCheckManual",
                            androidx.work.ExistingWorkPolicy.REPLACE,
                            immediateRequest
                        )
                        Toast.makeText(context, "Alarms Refreshed for Today!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = com.example.echorollv2.ui.theme.PrimaryOrange)
                ) {
                    Text("Refresh Class Alarms", color = Color.White)
                }
            }
        }
    }
}
