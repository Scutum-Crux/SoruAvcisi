package com.examaid.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.examaid.app.presentation.auth.LoginScreen
import com.examaid.app.presentation.auth.ForgotPasswordScreen
import com.examaid.app.presentation.auth.RegisterScreen
import com.examaid.app.presentation.dashboard.DashboardScreen
import com.examaid.app.presentation.onboarding.OnboardingScreen
import com.examaid.app.presentation.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Flow
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onSkip = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onGetStarted = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        
        // Additional flows kept for future expansion
        composable(Screen.RepeatList.route) {
            // RepeatListScreen()
        }
        
        composable(
            route = Screen.ScheduleSelector.route,
            arguments = listOf(navArgument("flashcardId") { type = NavType.StringType })
        ) {
            // ScheduleSelectorScreen()
        }
        
        composable(
            route = Screen.StudySession.route,
            arguments = listOf(navArgument("scheduleId") { type = NavType.StringType })
        ) {
            // StudySessionScreen()
        }
        
        // Literature Section
        composable(Screen.LiteratureHome.route) {
            // LiteratureHomeScreen()
        }
        
        composable(Screen.TopicList.route) {
            // TopicListScreen()
        }
        
        composable(
            route = Screen.TopicDetail.route,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) {
            // TopicDetailScreen()
        }
        
        composable(
            route = Screen.TestRunner.route,
            arguments = listOf(navArgument("testId") { type = NavType.StringType })
        ) {
            // TestRunnerScreen()
        }
        
        composable(
            route = Screen.ExamRunner.route,
            arguments = listOf(navArgument("examId") { type = NavType.StringType })
        ) {
            // ExamRunnerScreen()
        }
        
        composable(Screen.AuthorWorkList.route) {
            // AuthorWorkListScreen()
        }
        
        composable(
            route = Screen.AuthorWorkDetail.route,
            arguments = listOf(navArgument("authorId") { type = NavType.StringType })
        ) {
            // AuthorWorkDetailScreen()
        }
        
        composable(Screen.Profile.route) {
            // ProfileScreen()
        }
        
        composable(Screen.Subscription.route) {
            // SubscriptionScreen()
        }
    }
}

