package com.token.tokenator.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.token.tokenator.R
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type
import com.token.tokenator.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TokenSettingsScreen(
    navigator: Navigator,
    viewModel: TokenSettingsViewModel = hiltViewModel(),
) {
    val allCharacters by viewModel.allCharacters.collectAsStateWithLifecycle()
    val passphrase by viewModel.passphrase.collectAsStateWithLifecycle()
    val switchPassphrase by viewModel.switchPassphrase.collectAsStateWithLifecycle()
    val switchNoRepeat by viewModel.switchNoRepeat.collectAsStateWithLifecycle()
    val switchBiometric by viewModel.switchBiometric.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var passphraseText by remember(passphrase) { mutableStateOf(passphrase?.phrase ?: "") }

    val groupedCharacters =
        remember(allCharacters) {
            allCharacters.groupBy { it.category }
        }

    var showPrivacyPolicy by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.token_settings),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
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
                    .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Passphrase Section
            SectionHeader(text = stringResource(id = R.string.passphrase))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.use_custom_phrase),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = switchPassphrase,
                            onCheckedChange = { viewModel.updatePassphrase(it) },
                            thumbContent =
                                if (switchPassphrase) {
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

                    if (switchPassphrase) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = passphraseText,
                                onValueChange = { passphraseText = it },
                                placeholder = { Text(stringResource(R.string.enter_a_passphrase)) },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium,
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
                                    ),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = {
                                    viewModel.insertPassphrase(passphraseText)
                                    Toast
                                        .makeText(
                                            context,
                                            R.string.passphrase_saved,
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                },
                                enabled = passphraseText.isNotEmpty() && passphraseText != passphrase?.phrase,
                                modifier =
                                    Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
                                colors =
                                    IconButtonDefaults.iconButtonColors(
                                        // Enabled state
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        // Disabled state
                                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        disabledContentColor = MaterialTheme.colorScheme.outline,
                                    ),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Save,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Security Options
            SectionHeader(text = stringResource(id = R.string.security))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column {
                    Row(
                        modifier =
                            Modifier
                                .clickable { viewModel.updateNoRepeat(!switchNoRepeat) }
                                .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.no_repeat_characters),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = switchNoRepeat,
                            onCheckedChange = { viewModel.updateNoRepeat(it) },
                            thumbContent =
                                if (switchNoRepeat) {
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

                    Row(
                        modifier =
                            Modifier
                                .clickable { viewModel.updateBiometric(!switchBiometric) }
                                .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.biometric_auth),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = switchBiometric,
                            onCheckedChange = { viewModel.updateBiometric(it) },
                            thumbContent =
                                if (switchBiometric) {
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
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Characters Section
            SectionHeaderWithButton(
                text = stringResource(id = R.string.active_characters),
                textButton = stringResource(id = R.string.clear_all),
                onClick = { viewModel.clearAllItems() },
            )

            val types =
                listOf(
                    Type.UPPERCASE to stringResource(id = R.string.uppercase),
                    Type.LOWERCASE to stringResource(id = R.string.lowercase),
                    Type.NUMERIC to stringResource(id = R.string.numbers),
                    Type.SPECIAL to stringResource(id = R.string.symbols),
                )

            types.forEach { (type, title) ->
                val items = groupedCharacters[type] ?: emptyList()
                if (items.isNotEmpty()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 16.dp),
                    )

                    FlowRow(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        items.forEach { item ->
                            CharacterBox(item = item) { viewModel.updateItems(it) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Legal Section
            SectionHeader(text = stringResource(id = R.string.legal))
            Card(
                onClick = { showPrivacyPolicy = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = { showPrivacyPolicy = true },
                    ) {
                        Text(
                            text = stringResource(id = R.string.privacy_policy),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { showPrivacyPolicy = true }) {
                        Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Version Info
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = viewModel.version,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp),
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showPrivacyPolicy) {
        com.token.tokenator.ui.components.PrivacyPolicyDialog(
            onDismiss = { showPrivacyPolicy = false },
        )
    }
}

@Composable
fun CharacterBox(
    item: SettingsItem,
    onClick: (SettingsItem) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .padding(6.dp)
                .size(44.dp)
                .clip(MaterialTheme.shapes.small)
                .background(
                    if (item.included) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    },
                )
                .clickable {
                    onClick(item.copy(included = !item.included))
                },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = item.item,
            style = MaterialTheme.typography.titleMedium,
            color =
                if (item.included) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                },
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
private fun SectionHeaderWithButton(
    text: String,
    textButton: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp),
    ) {
        SectionHeader(text)

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onClick) {
            Text(
                text = textButton,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
