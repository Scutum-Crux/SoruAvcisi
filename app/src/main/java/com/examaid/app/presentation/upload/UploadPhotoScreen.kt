package com.examaid.app.presentation.upload

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.examaid.app.R
import com.examaid.app.domain.model.PhotoNote
import com.examaid.app.domain.model.PhotoReason
import com.examaid.app.presentation.components.ButtonVariant
import com.examaid.app.presentation.components.ExamAidButton
import com.examaid.app.presentation.components.ExamAidTextField
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UploadPhotoScreen(
    onBack: () -> Unit,
    onSaved: (PhotoNote) -> Unit = {},
    viewModel: UploadPhotoViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val uiState by viewModel.state.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var lastCapturedUri by remember { mutableStateOf<Uri?>(null) }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            viewModel.resetAfterImageChange()
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.upload_photo_not_selected)
                )
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && lastCapturedUri != null) {
            selectedImageUri = lastCapturedUri
            viewModel.resetAfterImageChange()
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.upload_photo_capture_failed)
                )
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera(context) { uri ->
                lastCapturedUri = uri
                cameraLauncher.launch(uri)
            } ?: coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.upload_photo_capture_failed)
                )
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.upload_photo_camera_permission_denied)
                )
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeError()
        }
    }

    LaunchedEffect(uiState.savedNote) {
        val savedNote = uiState.savedNote
        if (savedNote != null) {
            snackbarHostState.showSnackbar(context.getString(R.string.upload_photo_saved_message))
            onSaved(savedNote)
            selectedImageUri = null
            viewModel.resetAfterImageChange()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.upload_photo_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // TopAppBar'ın boşluğunu uygula
        ) {
            val horizontalPadding = when {
                maxWidth < 360.dp -> 16.dp
                maxWidth < 600.dp -> 24.dp
                else -> 32.dp
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 16.dp, // İçeriğin üstten boşluğu
                        bottom = 16.dp // <-- DEĞİŞİKLİK BURADA (O devasa beyaz boşluğu sildik)
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(id = R.string.upload_photo_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                UploadPreview(uri = selectedImageUri)

                Spacer(modifier = Modifier.height(20.dp))

                ExamAidButton(
                    text = stringResource(id = R.string.upload_photo_camera),
                    onClick = {
                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                        if (granted) {
                            launchCamera(context) { uri ->
                                lastCapturedUri = uri
                                cameraLauncher.launch(uri)
                            } ?: coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.upload_photo_capture_failed)
                                )
                            }
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isSaving.not()
                )

                Spacer(modifier = Modifier.height(12.dp))

                ExamAidButton(
                    text = stringResource(id = R.string.upload_photo_gallery),
                    onClick = {
                        galleryPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    variant = ButtonVariant.Secondary,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isSaving.not()
                )

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle(text = stringResource(id = R.string.upload_photo_subject_title))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.subjects.forEach { subject ->
                        FilterChip(
                            selected = uiState.selectedSubject == subject,
                            onClick = { viewModel.selectSubject(subject) },
                            label = { Text(subject) },
                            shape = MaterialTheme.shapes.small,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(text = stringResource(id = R.string.upload_photo_reason_title))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.reasons.forEach { reason ->
                        FilterChip(
                            selected = uiState.selectedReason == reason,
                            onClick = { viewModel.selectReason(reason) },
                            label = { Text(reason.toDisplayText(context)) },
                            shape = MaterialTheme.shapes.small,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                selectedLabelColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(text = stringResource(id = R.string.upload_photo_note_title))

                ExamAidTextField(
                    value = uiState.note,
                    onValueChange = viewModel::updateNote,
                    label = stringResource(id = R.string.upload_photo_note_hint),
                    singleLine = false,
                    enabled = selectedImageUri != null
                )

                Text(
                    text = stringResource(
                        id = R.string.upload_photo_note_counter,
                        uiState.note.length,
                        UploadPhotoViewModel.MAX_NOTE_LENGTH
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                ExamAidButton(
                    text = stringResource(id = R.string.upload_photo_save),
                    onClick = {
                        val uri = selectedImageUri
                        if (uri == null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.upload_photo_select_hint)
                                )
                            }
                        } else {
                            viewModel.savePhotoNote(uri)
                        }
                    },
                    enabled = selectedImageUri != null && !uiState.isSaving,
                    isLoading = uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun UploadPreview(
    uri: Uri?
) {
    val background = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (uri == null) {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.6f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.upload_photo_placeholder_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.upload_photo_placeholder_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = TextAlign.Center
            )
        } else {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(28.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }
    }
}

private fun launchCamera(
    context: Context,
    onUriReady: (Uri) -> Unit
): Uri? {
    val uri = createImageUri(context)
    if (uri != null) {
        onUriReady(uri)
    }
    return uri
}

private fun createImageUri(context: Context): Uri? {
    return runCatching {
        val imagesDir = File(context.cacheDir, "images").apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val image = File.createTempFile("capture_", ".jpg", imagesDir)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            image
        )
    }.getOrNull()
}