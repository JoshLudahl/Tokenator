package com.token.tokenator.ui.savedpassword.passworddetails

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.token.tokenator.R
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.IntentHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenDetailScreen(
    passwordId: Int,
    navigator: Navigator,
    viewModel: PasswordDetailViewModel = hiltViewModel(),
) {
    val token by viewModel.token.collectAsStateWithLifecycle()
    val shouldShowWarning by viewModel.shouldShowWarning.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var tokenName by remember(token) { mutableStateOf(token?.title ?: "") }
    var loginName by remember(token) { mutableStateOf(token?.login ?: "") }
    var passwordValue by remember(token) { mutableStateOf(token?.token ?: "") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val hasChanges =
        remember(token, tokenName, loginName, passwordValue) {
            token?.let {
                tokenName != it.title || loginName != (it.login ?: "") || passwordValue != it.token
            } ?: false
        }

    LaunchedEffect(passwordId) {
        viewModel.getToken(passwordId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_circle_left),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete_round),
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Large Logo/Icon
            Box(
                modifier =
                    Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                // Icon Badge
                Box(
                    modifier =
                        Modifier
                            .size(52.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.onSecondaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = token?.title?.take(1)?.uppercase() ?: "P",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = tokenName.ifEmpty { "Tokenator" },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(32.dp))

            ModernDetailField(
                label = "Name",
                value = tokenName,
                onValueChange = { tokenName = it },
                leadingIcon = R.drawable.ic_label_round,
                onCopy = {
                    Clipuous.copyToClipboard(tokenName, context)
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                        Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModernDetailField(
                label = stringResource(R.string.login_input_field),
                value = loginName,
                onValueChange = { loginName = it },
                leadingIcon = R.drawable.ic_login_round,
                onCopy = {
                    Clipuous.copyToClipboard(loginName, context)
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                        Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModernDetailField(
                label = stringResource(R.string.token),
                value = passwordValue,
                onValueChange = { passwordValue = it },
                leadingIcon = R.drawable.ic_password_round,
                onCopy = {
                    Clipuous.copyToClipboard(passwordValue, context, isSensitive = true)
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                        Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                    }
                },
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateToken(tokenName, passwordValue, loginName)
                    Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
                },
                enabled = hasChanges,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text(
                    text = "SAVE CHANGES",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (shouldShowWarning) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_danger_circle),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "SECURITY ALERT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                            Text(
                                text = stringResource(R.string.outdated_password_message),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { /* Action to update */ }) {
                            Text("Fix now", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            IconButton(
                onClick = { IntentHelper.handleShareClick(passwordValue, context) },
                modifier =
                    Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surface),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share_round),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Password") },
                text = { Text("Are you sure you want to delete this password? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            token?.let {
                                viewModel.deleteToken(it)
                                showDeleteDialog = false
                                navigator.goBack()
                            }
                        },
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                },
            )
        }
    }
}

@Composable
fun ModernDetailField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: Int,
    onCopy: (() -> Unit)? = null,
    isPassword: Boolean = false,
) {
    var passwordVisible by remember { mutableStateOf(!isPassword) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp),
                ) {
                    if (isPassword) {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = if (passwordVisible) R.drawable.ic_visibility_round else R.drawable.ic_visibility_off_round),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    if (onCopy != null) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_content_copy_round),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
        )
    }
}
