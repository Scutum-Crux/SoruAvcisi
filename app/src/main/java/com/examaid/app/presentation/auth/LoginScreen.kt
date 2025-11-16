package com.examaid.app.presentation.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examaid.app.R
import com.examaid.app.core.navigation.Screen
import com.examaid.app.core.util.UiEvent
import com.examaid.app.presentation.components.ButtonVariant
import com.examaid.app.presentation.components.ExamAidButton
import com.examaid.app.presentation.components.ExamAidTextField
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val googleSignInClient = remember {
        val webClientId = context.getString(R.string.default_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken.isNullOrBlank()) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Google girişi başarısız.")
                }
            } else {
                viewModel.onEvent(LoginEvent.LoginWithGoogle(idToken))
            }
        } catch (e: ApiException) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Google girişi başarısız: ${e.localizedMessage ?: ""}")
            }
        }
    }
    
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    when (event.route) {
                        "home" -> onNavigateToHome()
                        Screen.Dashboard.route -> onNavigateToHome()
                        "register" -> onNavigateToRegister()
                        "forgot_password" -> onNavigateToForgotPassword()
                    }
                }
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> Unit
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo or App Name
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Sınavlarınıza hazırlanın",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Email Field
            ExamAidTextField(
                value = state.email,
                onValueChange = {
                    if (it.length <= 96) {
                        viewModel.onEvent(LoginEvent.EmailChanged(it))
                    }
                },
                label = stringResource(R.string.email),
                placeholder = "ornek@email.com",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password Field
            ExamAidTextField(
                value = state.password,
                onValueChange = {
                    if (it.length <= 64) {
                        viewModel.onEvent(LoginEvent.PasswordChanged(it))
                    }
                },
                label = stringResource(R.string.password),
                placeholder = "••••••••",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                isPassword = true,
                isPasswordVisible = state.isPasswordVisible,
                onPasswordVisibilityToggle = {
                    viewModel.onEvent(LoginEvent.TogglePasswordVisibility)
                },
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Forgot Password
            TextButton(
                onClick = { viewModel.onEvent(LoginEvent.ForgotPassword) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.forgot_password))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Login Button
            ExamAidButton(
                text = stringResource(R.string.login),
                onClick = { viewModel.onEvent(LoginEvent.Login) },
                isLoading = state.isLoading,
                enabled = state.email.isNotBlank() && state.password.isNotBlank()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Divider with "or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.or_continue_with),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Google Sign In Button
            ExamAidButton(
                text = stringResource(R.string.google_sign_in),
                onClick = {
                    if (!state.isLoading) {
                        googleSignInClient.signOut()
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                },
                variant = ButtonVariant.Secondary,
                enabled = !state.isLoading
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Register Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hesabınız yok mu?",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = { viewModel.onEvent(LoginEvent.NavigateToRegister) }
                ) {
                    Text(
                        text = stringResource(R.string.register),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

