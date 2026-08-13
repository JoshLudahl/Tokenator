package com.token.tokenator.ui.generate

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.token.tokenator.R
import com.token.tokenator.model.Type
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.ui.main.MainViewModel
import com.token.tokenator.ui.theme.FinSurfaceDark
import com.token.tokenator.ui.theme.FinTextDark
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.Tokenator

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
    val allCharacters by viewModel.allCharacters.collectAsStateWithLifecycle()

    var tokenName by remember { mutableStateOf("") }
    var loginName by remember { mutableStateOf("") }
    var sliderValue by remember { mutableFloatStateOf(16f) }

    // Helper function to trigger generation
    val generateNewToken = {
        val types = mutableListOf<Type>()
        if (switchUpperCase) types.add(Type.UPPERCASE)
        if (switchLowerCase) types.add(Type.LOWERCASE)
        if (switchNumeric) types.add(Type.NUMERIC)
        if (switchSpecial) types.add(Type.SPECIAL)

        val excluded = allCharacters.filter { !it.included }.map { it.item }
        val generated = Tokenator.generate(
            length = sliderValue.toInt(),
            includesTypesList = types,
            excludedCharacters = excluded,
            doNotRepeat = noRepeat,
            includePhrase = passphrase?.phrase ?: "",
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
    val passwordStrength = remember(token) {
        when {
            token.length >= 16 && (switchUpperCase && switchLowerCase && switchNumeric && switchSpecial) -> "VERY STRONG"
            token.length >= 12 -> "STRONG"
            token.length >= 8 -> "MEDIUM"
            token.isEmpty() -> "NONE"
            else -> "WEAK"
        }
    }

    val strengthColor = when (passwordStrength) {
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
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_circle_left),
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Hero Card showing generated password
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(FinSurfaceDark)
                    .clickable {
                        if (token.isNotEmpty()) {
                            Clipuous.copyToClipboard(token, context)
                            Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GENERATED PASSWORD",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.labelMedium,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Strength badge
                        if (token.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = strengthColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = passwordStrength,
                                    color = strengthColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (token.isEmpty()) "••••••••••••••••" else token,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tap box to copy",
                            color = Color.White.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.bodySmall
                        )

                        IconButton(
                            onClick = {
                                if (token.isNotEmpty()) {
                                    Clipuous.copyToClipboard(token, context)
                                    Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_content_copy_round),
                                contentDescription = "Copy",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Generate Password Button
            Button(
                onClick = generateNewToken,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Password",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Quick Settings header right below Generate Button
            QuickSettingsHeader(text = "Quick Settings")

            // Quick Settings Container (Length Slider + Toggles)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Length Row Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password Length",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = FinTextDark
                        )
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${sliderValue.toInt()} chars",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.secondary,
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )

                    Text(
                        text = "Character Types",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Quick Character Toggles
                    QuickToggleItem(
                        label = stringResource(R.string.uppercase_letters),
                        subtext = "A-Z",
                        checked = switchUpperCase,
                        onCheckedChange = {
                            viewModel.saveSwitchState(Type.UPPERCASE, it)
                            generateNewToken()
                        }
                    )
                    QuickToggleItem(
                        label = stringResource(R.string.lowercase_letters),
                        subtext = "a-z",
                        checked = switchLowerCase,
                        onCheckedChange = {
                            viewModel.saveSwitchState(Type.LOWERCASE, it)
                            generateNewToken()
                        }
                    )
                    QuickToggleItem(
                        label = stringResource(R.string.numeric),
                        subtext = "0-9",
                        checked = switchNumeric,
                        onCheckedChange = {
                            viewModel.saveSwitchState(Type.NUMERIC, it)
                            generateNewToken()
                        }
                    )
                    QuickToggleItem(
                        label = stringResource(R.string.special_characters),
                        subtext = "!@#$%^&*",
                        checked = switchSpecial,
                        onCheckedChange = {
                            viewModel.saveSwitchState(Type.SPECIAL, it)
                            generateNewToken()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save to Vault Form
            QuickSettingsHeader(text = "Save to Vault")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = tokenName,
                        onValueChange = { tokenName = it },
                        label = { Text("App / Website Name") },
                        placeholder = { Text("e.g. Google, Netflix, Work Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = loginName,
                        onValueChange = { loginName = it },
                        label = { Text("Username / Email") },
                        placeholder = { Text("e.g. alex@example.com") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (tokenName.isBlank()) {
                                Toast.makeText(context, "Please enter an app/website name", Toast.LENGTH_SHORT).show()
                            } else if (token.isEmpty()) {
                                Toast.makeText(context, "Please generate a password first", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.insert(tokenName, token, loginName)
                                Toast.makeText(context, R.string.password_saved, Toast.LENGTH_SHORT).show()
                                navigator.goBack()
                            }
                        },
                        enabled = tokenName.isNotBlank() && token.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Save Password to Vault",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
        color = FinTextDark,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun QuickToggleItem(
    label: String,
    subtext: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = FinTextDark
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
        )
    }
}
