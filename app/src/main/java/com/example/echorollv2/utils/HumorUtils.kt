package com.example.echorollv2.utils

object HumorUtils {
    private val holidayMessages = listOf(
        "Stay in bed, you beautiful disaster. No classes today!",
        "It's a holiday! Time to pretend you have a social life.",
        "Today's agenda: Absolute nothingness. Enjoy the holiday!",
        "Warning: High levels of relaxation detected. It's a holiday!",
        "Go touch some grass. Or just sleep. It's a holiday anyway.",
        "The professor is probably sleeping too. It's a holiday!",
        "Class is cancelled by the universe. Happy holiday!"
    )

    private val attendanceReminders = listOf(
        "The suffering is almost over! Mark %s present before you vanish.",
        "5 minutes left! Don't let your %s attendance die a silent death.",
        "Quick! Mark %s before the professor realizes you're just a hologram.",
        "The bell is about to ring. Do you exist in %s? Prove it!",
        "Last call for %s! Don't be a ghost in the system.",
        "Class is ending. If you don't mark %s, did you even study?",
        "Time to wake up! Mark %s attendance before you leave."
    )

    private val followUpMessages = listOf(
        "Hey ghost, were you even in %s? Mark your attendance before I report you!",
        "Still no attendance for %s? Your academic future is crying in a corner.",
        "Missing attendance for %s. Are you a ninja or just forgetful?",
        "I checked the logs. You didn't mark %s. Do it now or forever hold your peace.",
        "Your attendance in %s is currently 'Imaginary'. Make it real!",
        "Are you waiting for %s to mark itself? Not happening!",
        "Tick tock! %s attendance is still missing. Don't be that person.",
        "Legend says if you don't mark %s, a degree disappears.",
        "I'm not mad, just disappointed. Mark %s already!"
    )

    private val funnyQuotes = listOf(
        "Somewhere, a textbook is crying because you're not reading it.",
        "Attendance is like a gym membership. You pay for it and then don't go.",
        "If only sleeping in class counted as extra credit.",
        "Your future self is judging your current attendance record.",
        "I see you. You see me. Now go mark that attendance!",
        "Life is short, but this class was long. Mark it and escape!",
        "Error 404: %s Attendance Not Found. Please rectify.",
        "Don't make me use my 'Sassy AI' voice. Mark %s!"
    )

    private val lowAttendanceMessages = listOf(
        "Your attendance is lower than my self-esteem. Fix it!",
        "Warning: You are biologically becoming a dropout. Go to class!",
        "75%% is a dream at this point. Wake up and attend!",
        "Predicting your future... Oh wait, you need to attend class first.",
        "Your attendance record looks like a game of Minesweeper. Too many holes!"
    )

    fun getHolidayMessage(): String = holidayMessages.random()
    fun getAttendanceReminder(subjectName: String): String = attendanceReminders.random().format(subjectName)
    fun getFollowUpMessage(subjectName: String): String = followUpMessages.random().format(subjectName)
    fun getLowAttendanceMessage(): String = lowAttendanceMessages.random()
    
    fun getRandomNotification(subjectName: String): String {
        return (followUpMessages + funnyQuotes).random().format(subjectName)
    }
}
