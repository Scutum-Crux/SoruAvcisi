package com.examaid.app.core.constants

object Constants {
    // Database
    const val DATABASE_NAME = "examaid_database"
    const val DATABASE_VERSION = 2
    
    // Preferences
    const val PREFERENCES_NAME = "examaid_preferences"
    const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_LANGUAGE = "language"
    const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val KEY_USER_ID = "user_id"
    
    // API
    const val BASE_URL = "https://api.examaid.app/"
    const val TIMEOUT_SECONDS = 30L
    
    // Repeat System Periods (in days)
    val REPEAT_PERIODS = listOf(1, 2, 5, 7, 14, 30, 60, 90)
    
    // Literature
    const val QUESTIONS_PER_PRACTICE_TEST = 20
    const val PRACTICE_TESTS_PER_TOPIC = 5
    const val QUESTIONS_PER_EXAM = 24
    const val TOTAL_EXAMS = 50
    
    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "examaid_reminders"
    const val NOTIFICATION_CHANNEL_NAME = "Study Reminders"
    
    // Work Manager
    const val REMINDER_WORKER_TAG = "reminder_worker"
    const val SYNC_WORKER_TAG = "sync_worker"
    
    // Subscription Types
    enum class SubscriptionType {
        FREE,
        REPEAT_ONLY,
        LITERATURE_ONLY,
        PREMIUM_ALL
    }
    
    // Image
    const val MAX_IMAGE_SIZE_MB = 10
    const val IMAGE_QUALITY = 90
}

