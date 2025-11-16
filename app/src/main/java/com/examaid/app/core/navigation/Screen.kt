package com.examaid.app.core.navigation

sealed class Screen(val route: String) {
    // Auth Flow
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    
    // Main Flow with bottom navigation
    data object Dashboard : Screen("dashboard")
    data object HomeFeed : Screen("home_feed")
    data object PhotoArchive : Screen("photo_archive")
    data object UploadPhoto : Screen("upload_photo")
    data object Schedule : Screen("schedule")
    data object Settings : Screen("settings")
    
    // Repeat System (legacy / upcoming features)
    data object RepeatList : Screen("repeat_list")
    data object ScheduleSelector : Screen("schedule_selector/{flashcardId}") {
        fun createRoute(flashcardId: String) = "schedule_selector/$flashcardId"
    }
    data object StudySession : Screen("study_session/{scheduleId}") {
        fun createRoute(scheduleId: String) = "study_session/$scheduleId"
    }
    
    // Literature Section
    data object LiteratureHome : Screen("literature_home")
    data object TopicList : Screen("topic_list")
    data object TopicDetail : Screen("topic_detail/{topicId}") {
        fun createRoute(topicId: String) = "topic_detail/$topicId"
    }
    data object TestRunner : Screen("test_runner/{testId}") {
        fun createRoute(testId: String) = "test_runner/$testId"
    }
    data object ExamRunner : Screen("exam_runner/{examId}") {
        fun createRoute(examId: String) = "exam_runner/$examId"
    }
    data object AuthorWorkList : Screen("author_work_list")
    data object AuthorWorkDetail : Screen("author_work_detail/{authorId}") {
        fun createRoute(authorId: String) = "author_work_detail/$authorId"
    }
    
    // Settings & Profile
    data object Profile : Screen("profile")
    data object Subscription : Screen("subscription")
}

