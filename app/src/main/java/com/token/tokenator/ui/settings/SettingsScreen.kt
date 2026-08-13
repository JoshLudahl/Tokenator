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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.token.tokenator.R
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type
import com.token.tokenator.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    navigator: Navigator,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val allCharacters by viewModel.allCharacters.collectAsStateWithLifecycle()
    val passphrase by viewModel.passphrase.collectAsStateWithLifecycle()
    val switchPassphrase by viewModel.switchPassphrase.collectAsStateWithLifecycle()
    val switchNoRepeat by viewModel.switchNoRepeat.collectAsStateWithLifecycle()
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
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_circle_left),
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
            SectionHeader(text = "Passphrase")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Use custom phrase",
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
                                modifier =
                                    Modifier
                                        .size(48.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.primary),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_save_round),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Security Options
            SectionHeader(text = "Security")
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

                    val isSecurityEnabled by viewModel.isSecurityEnabled.collectAsStateWithLifecycle()

                    Row(
                        modifier =
                            Modifier
                                .clickable { viewModel.updateSecurity(!isSecurityEnabled) }
                                .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "App Security (PIN/Pattern/Biometric)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = isSecurityEnabled,
                            onCheckedChange = { viewModel.updateSecurity(it) },
                            thumbContent =
                                if (isSecurityEnabled) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
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
            SectionHeader(text = "Active Characters")

            val types =
                listOf(
                    Type.UPPERCASE to "Uppercase",
                    Type.LOWERCASE to "Lowercase",
                    Type.NUMERIC to "Numbers",
                    Type.SPECIAL to "Symbols",
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
            SectionHeader(text = "Legal")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier =
                        Modifier
                            .clickable { showPrivacyPolicy = true }
                            .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                }
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
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ).clickable {
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
                    MaterialTheme.colorScheme.onSurface
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
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}
