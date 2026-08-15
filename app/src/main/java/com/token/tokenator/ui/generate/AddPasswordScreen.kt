package com.token.tokenator.ui.generate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.GeneratingTokens
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.token.tokenator.R
import com.token.tokenator.model.Type
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.navigation.Route
import com.token.tokenator.ui.main.MainViewModel
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.Tokenator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(
    navigator: Navigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val token by viewModel.token.collectAsStateWithLifecycle()
    val switchUpperCase by viewModel.switchUpperCase.collectAsStateWithLifecycle()
    val switchLowerCase by viewModel.switchLowerCase.collectAsStateWithLifecycle()
    val switchNumeric by viewModel.switchNumeric.collectAsStateWithLifecycle()
    val switchSpecial by viewModel.switchSpecialCharacter.collectAsStateWithLifecycle()
    val noRepeat by viewModel.noRepeatFlow.collectAsStateWithLifecycle(initialValue = true)
    val passphrase by viewModel.passphrase.collectAsStateWithLifecycle()
    val switchPassphrase by viewModel.switchPassphrase.collectAsStateWithLifecycle()
    val allCharacters by viewModel.allCharacters.collectAsStateWithLifecycle()

    var tokenName by remember { mutableStateOf("") }
    var loginName by remember { mutableStateOf("") }
    var sliderValue by remember { mutableFloatStateOf(16f) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copiedText = stringResource(id = R.string.toast_copied_to_clipboard)

    // Helper function to trigger generation
    val generateNewToken = {
        val types = mutableListOf<Type>()
        if (switchUpperCase) types.add(Type.UPPERCASE)
        if (switchLowerCase) types.add(Type.LOWERCASE)
        if (switchNumeric) types.add(Type.NUMERIC)
        if (switchSpecial) types.add(Type.SPECIAL)

        val excluded = allCharacters.filter { !it.included }.map { it.item }
        val generated =
            Tokenator.generate(
                length = sliderValue.toInt(),
                includesTypesList = types,
                excludedCharacters = excluded,
                doNotRepeat = noRepeat,
                includePhrase = if (switchPassphrase) passphrase?.phrase ?: "" else "",
            )

        if (generated.isNotEmpty()) {
            viewModel.setToken(generated)
            viewModel.setLength(sliderValue)
        }
    }

    // Auto-generate initial password if empty
    LaunchedEffect(Unit) {
        if (token.isEmpty()) {
            generateNewToken()
        }
    }

    // Calculate password strength indicator
    val passwordStrength =
        remember(token) {
            when {
                token.length >= 16 && (switchUpperCase && switchLowerCase && switchNumeric && switchSpecial) -> "VERY STRONG"
                token.length >= 12 -> "STRONG"
                token.length >= 8 -> "MEDIUM"
                token.isEmpty() -> "NONE"
                else -> "WEAK"
            }
        }

    val strengthColor =
        when (passwordStrength) {
            "VERY STRONG" -> Color(0xFF10B981)
            "STRONG" -> Color(0xFF10B981)
            "MEDIUM" -> Color(0xFFF59E0B)
            else -> Color(0xFFEF4444)
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "New Password",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigator.navigate(Route.Settings) }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Hero Card showing generated password
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            if (token.isNotEmpty()) {
                                Clipuous.copyToClipboard(token, context, isSensitive = true)
                                scope.launch {
                                    snackbarHostState.showSnackbar(copiedText)
                                }
                            }
                        }.padding(24.dp),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "GENERATED PASSWORD",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelMedium,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        // Strength badge
                        if (token.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = strengthColor.copy(alpha = 0.2f),
                            ) {
                                Text(
                                    text = passwordStrength,
                                    color = strengthColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier =
                                        Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 4.dp,
                                        ),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = token.ifEmpty { "••••••••••••••••" },
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Tap box to copy",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodySmall,
                        )

                        IconButton(
                            onClick = {
                                if (token.isNotEmpty()) {
                                    Clipuous.copyToClipboard(token, context, isSensitive = true)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(copiedText)
                                    }
                                }
                            },
                            modifier =
                                Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.1f,
                                        ),
                                    ),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Generate Password Button
            Button(
                onClick = generateNewToken,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("GENERATE_TOKEN_BUTTON")
                        .semantics {
                            contentDescription = "GENERATE_TOKEN_BUTTON"
                            testTag = "GENERATE_TOKEN_BUTTON"
                        },
                shape = MaterialTheme.shapes.extraLarge,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.GeneratingTokens,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.generated_token),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save to Vault Form
            QuickSettingsHeader(text = stringResource(id = R.string.save_to_vault))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = tokenName,
                        onValueChange = { tokenName = it },
                        label = { Text(stringResource(id = R.string.app_website_name)) },
                        placeholder = { Text(stringResource(id = R.string.app_website_name_placeholder)) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .testTag("TOKEN_NAME_FIELD")
                                .semantics {
                                    contentDescription = "TOKEN_NAME_FIELD"
                                    testTag = "TOKEN_NAME_FIELD"
                                },
                        shape = MaterialTheme.shapes.large,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Label,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        singleLine = true,
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor =
                                    MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.1f,
                                    ),
                                unfocusedContainerColor =
                                    MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.2f,
                                    ),
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = loginName,
                        onValueChange = { loginName = it },
                        label = { Text(stringResource(id = R.string.username_email)) },
                        placeholder = { Text(stringResource(id = R.string.username_email_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Login,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        singleLine = true,
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor =
                                    MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.1f,
                                    ),
                                unfocusedContainerColor =
                                    MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.2f,
                                    ),
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (tokenName.isBlank()) {
                                viewModel.showSnackbar(R.string.error_enter_app_name)
                            } else if (token.isEmpty()) {
                                viewModel.showSnackbar(R.string.error_generate_password_first)
                            } else {
                                viewModel.insert(tokenName, token, loginName)
                                viewModel.showSnackbar(R.string.password_saved)
                                navigator.goBack()
                            }
                        },
                        enabled = tokenName.isNotBlank() && token.isNotEmpty(),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("SAVE_TOKEN_BUTTON")
                                .semantics {
                                    contentDescription = "SAVE_TOKEN_BUTTON"
                                    testTag = "SAVE_TOKEN_BUTTON"
                                },
                    ) {
                        Text(
                            text = stringResource(id = R.string.save_password_to_vault),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Quick Settings header right below Generate Button
            QuickSettingsHeader(text = stringResource(id = R.string.quick_settings))

            // Quick Settings Container (Length Slider + Toggles)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Length Row Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Password Length",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Text(
                                text = "${sliderValue.toInt()} chars",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = sliderValue,
                        onValueChange = {
                            sliderValue = it
                            generateNewToken()
                        },
                        valueRange = 8f..100f,
                        steps = 92,
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.secondary,
                                inactiveTrackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                            ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Included Character Types",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )

                    QuickToggleItem(
                        label = stringResource(R.string.uppercase_letters),
                        subtext = "A-Z",
                        checked = switchUpperCase,
                        onCheckedChange = {
                            viewModel.saveSwitchState(Type.UPPERCASE, it)
                            generateNewToken()
                        },
                        modifier =
                            Modifier
                                .testTag("SWITCH_UPPERCASE")
                                .semantics {
                                    contentDescription = "SWITCH_UPPERCASE"
                                    testTag = "SWITCH_UPPERCASE"
                                },
                    )
                    QuickToggleItem(
                        label = stringResource(R.string.lowercase_letters),
                        subtext = "a-z",
                        checked = switchLowerCase,
                        onCheckedChange = {
                            viewModel.saveSwitchState(Type.LOWERCASE, it)
                            generateNewToken()
                        },
                        modifier =
                            Modifier
                                .testTag("SWITCH_LOWERCASE")
                                .semantics {
                                    contentDescription = "SWITCH_LOWERCASE"
                                    testTag = "SWITCH_LOWERCASE"
                                },
                    )
                    QuickToggleItem(
                        label = stringResource(R.string.numeric),
                        subtext = "0-9",
                        checked = switchNumeric,
                        onCheckedChange = {
                            viewModel.saveSwitchState(Type.NUMERIC, it)
                            generateNewToken()
                        },
                        modifier =
                            Modifier
                                .testTag("SWITCH_NUMERIC")
                                .semantics {
                                    contentDescription = "SWITCH_NUMERIC"
                                    testTag = "SWITCH_NUMERIC"
                                },
                    )
                    QuickToggleItem(
                        label = stringResource(R.string.special_characters),
                        subtext = "!@#$%^&*",
                        checked = switchSpecial,
                        onCheckedChange = {
                            viewModel.saveSwitchState(Type.SPECIAL, it)
                            generateNewToken()
                        },
                        modifier =
                            Modifier
                                .testTag("SWITCH_SPECIAL")
                                .semantics {
                                    contentDescription = "SWITCH_SPECIAL"
                                    testTag = "SWITCH_SPECIAL"
                                },
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun QuickSettingsHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
private fun QuickToggleItem(
    label: String,
    subtext: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent =
                if (checked) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                            tint = MaterialTheme.colorScheme.surface,
                        )
                    }
                } else {
                    null
                },
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                ),
        )
    }
}
