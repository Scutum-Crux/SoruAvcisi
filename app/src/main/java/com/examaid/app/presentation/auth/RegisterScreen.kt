package com.examaid.app.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.examaid.app.R
import com.examaid.app.R.string.default_web_client_id
import com.examaid.app.core.util.UiEvent
import com.examaid.app.core.navigation.Screen
import com.examaid.app.core.navigation.Screen.ForgotPassword
import com.examaid.app.presentation.components.ButtonVariant
import com.examaid.app.presentation.components.ExamAidButton
import com.examaid.app.presentation.components.ExamAidTextField
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val googleSignInClient = remember {
        val webClientId = context.getString(default_web_client_id)
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
                viewModel.onEvent(RegisterEvent.LoginWithGoogle(idToken))
            }
        } catch (e: ApiException) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Google girişi başarısız: ${e.localizedMessage ?: ""}")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    when (event.route) {
                        "home" -> onNavigateToHome()
                        Screen.Dashboard.route -> onNavigateToHome()
                        "login" -> onNavigateToLogin()
                    }
                }
                UiEvent.NavigateUp -> onNavigateToLogin()
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
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
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconHeader()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Hesabını oluştur",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Aralıklı tekrar ve edebiyat içeriklerinin tamamına erişim için hesabını oluştur.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            ExamAidTextField(
                value = state.displayName,
                onValueChange = {
                    if (it.length <= 48) {
                        viewModel.onEvent(RegisterEvent.DisplayNameChanged(it))
                    }
                },
                label = "Ad Soyad",
                placeholder = "Örn. Ayşe Yılmaz",
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExamAidTextField(
                value = state.email,
                onValueChange = {
                    if (it.length <= 96) {
                        viewModel.onEvent(RegisterEvent.EmailChanged(it))
                    }
                },
                label = stringResource(id = R.string.email),
                placeholder = "ornek@mail.com",
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExamAidTextField(
                value = state.password,
                onValueChange = {
                    if (it.length <= 64) {
                        viewModel.onEvent(RegisterEvent.PasswordChanged(it))
                    }
                },
                label = stringResource(id = R.string.password),
                placeholder = "••••••••",
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                isPassword = true,
                isPasswordVisible = state.isPasswordVisible,
                onPasswordVisibilityToggle = { viewModel.onEvent(RegisterEvent.TogglePasswordVisibility) },
                isError = state.passwordError != null,
                errorMessage = state.passwordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExamAidTextField(
                value = state.confirmPassword,
                onValueChange = {
                    if (it.length <= 64) {
                        viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it))
                    }
                },
                label = "Şifreyi Doğrula",
                placeholder = "••••••••",
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                isPassword = true,
                isPasswordVisible = state.isConfirmPasswordVisible,
                onPasswordVisibilityToggle = { viewModel.onEvent(RegisterEvent.ToggleConfirmPasswordVisibility) },
                isError = state.confirmPasswordError != null,
                errorMessage = state.confirmPasswordError
            )

            Spacer(modifier = Modifier.height(24.dp))

            ExamAidButton(
                text = stringResource(id = R.string.register),
                onClick = { viewModel.onEvent(RegisterEvent.Register) },
                isLoading = state.isLoading,
                enabled = state.displayName.isNotBlank() &&
                    state.email.isNotBlank() &&
                    state.password.isNotBlank() &&
                    state.confirmPassword.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.or_continue_with),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            ExamAidButton(
                text = stringResource(id = R.string.google_sign_in),
                onClick = {
                    if (!state.isLoading) {
                        googleSignInClient.signOut()
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                },
                variant = ButtonVariant.Secondary,
                leadingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null
                    )
                },
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zaten hesabın var mı?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = onNavigateToLogin,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.login),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun IconHeader() {
    androidx.compose.material3.Icon(
        imageVector = Icons.Default.PersonAdd,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(8.dp)
            .size(48.dp)
    )
}

