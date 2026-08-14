package com.token.tokenator.ui.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.token.tokenator.R
import com.token.tokenator.model.Token
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.navigation.Route
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.Encryption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val tokens by viewModel.tokens.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var expanded by rememberSaveable { mutableStateOf(false) }

    var tokenToDelete by remember { mutableStateOf<Token?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBar(
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = searchQuery,
                                onQueryChange = { viewModel.setSearchQuery(it) },
                                onSearch = { expanded = false },
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                placeholder = { Text(stringResource(id = R.string.search_passwords)) },
                                leadingIcon = {
                                    if (expanded) {
                                        IconButton(onClick = { expanded = false }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                                contentDescription = stringResource(id = R.string.search_back),
                                            )
                                        }
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_search),
                                            contentDescription = stringResource(id = R.string.search_passwords),
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.close),
                                                contentDescription = stringResource(id = R.string.search_clear),
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                    }
                                },
                            )
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(max = if (expanded) Dp.Unspecified else 56.dp),
                        colors =
                            SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                dividerColor = Color.Transparent,
                            ),
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            items(tokens, key = { it.id }) { token ->
                                VaultTokenItem(
                                    token = token,
                                    onCopy = {
                                        val fullToken = Encryption.decrypt(token.token) ?: ""
                                        Clipuous.copyToClipboard(fullToken, context, isSensitive = true)
                                        Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                                    },
                                    onCopyUsername = {
                                        val login = token.login?.let { Encryption.decrypt(it) } ?: ""
                                        if (login.isNotEmpty()) {
                                            Clipuous.copyToClipboard(login, context)
                                            Toast.makeText(context, R.string.toast_username_copied, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onEdit = { navigator.navigate(Route.PasswordDetail(token.id)) },
                                    onDelete = { tokenToDelete = token },
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (!expanded) {
                        IconButton(
                            onClick = { navigator.navigate(Route.Settings) },
                            modifier =
                                Modifier
                                    .testTag("SETTINGS_BUTTON")
                                    .semantics {
                                        contentDescription = "SETTINGS_BUTTON"
                                        testTag = "SETTINGS_BUTTON"
                                    },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_settings_round),
                                contentDescription = stringResource(id = R.string.settings),
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigator.navigate(Route.AddPassword) },
                modifier =
                    Modifier
                        .padding(bottom = 8.dp)
                        .testTag("ADD_PASSWORD_FAB")
                        .semantics {
                            contentDescription = "ADD_PASSWORD_FAB"
                            testTag = "ADD_PASSWORD_FAB"
                        },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(id = R.string.new_password),
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(24.dp),
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.new_password),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.extraLarge,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { focusManager.clearFocus() }
                    .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) stringResource(id = R.string.search_results_count, tokens.size) else stringResource(id = R.string.all_passwords),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Password List
            if (tokens.isEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tokenator),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Unspecified,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) stringResource(id = R.string.no_passwords_matching, searchQuery) else stringResource(R.string.no_saved_passwords),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        if (searchQuery.isBlank()) {
                            Button(
                                onClick = { navigator.navigate(Route.AddPassword) },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.add_your_first_password),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 90.dp),
                ) {
                    items(tokens, key = { it.id }) { token ->
                        VaultTokenItem(
                            token = token,
                            onCopy = {
                                val fullToken = Encryption.decrypt(token.token) ?: ""
                                Clipuous.copyToClipboard(fullToken, context, isSensitive = true)
                                Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                            },
                            onCopyUsername = {
                                val login = token.login?.let { Encryption.decrypt(it) } ?: ""
                                if (login.isNotEmpty()) {
                                    Clipuous.copyToClipboard(login, context)
                                    Toast.makeText(context, R.string.toast_username_copied, Toast.LENGTH_SHORT).show()
                                }
                            },
                            onEdit = { navigator.navigate(Route.PasswordDetail(token.id)) },
                            onDelete = { tokenToDelete = token },
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (tokenToDelete != null) {
        AlertDialog(
            onDismissRequest = { tokenToDelete = null },
            title = {
                Text(
                    text = stringResource(id = R.string.delete_password_confirmation_title),
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(text = stringResource(id = R.string.delete_password_confirmation_message_named, tokenToDelete?.title ?: ""))
            },
            confirmButton = {
                TextButton(onClick = {
                    tokenToDelete?.let { viewModel.delete(it) }
                    tokenToDelete = null
                    Toast.makeText(context, R.string.toast_password_deleted, Toast.LENGTH_SHORT).show()
                }) {
                    Text(stringResource(id = R.string.delete), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { tokenToDelete = null }) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            shape = MaterialTheme.shapes.large,
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@Composable
fun VaultTokenItem(
    token: Token,
    onCopy: () -> Unit,
    onCopyUsername: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val decryptedLogin =
        remember(token.login) {
            token.login?.let { Encryption.decrypt(it) } ?: ""
        }
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onEdit() },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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
                    text = token.title.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = token.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = decryptedLogin.ifEmpty { stringResource(id = R.string.no_username_saved) },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = stringResource(id = R.string.more_options),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.copy_password)) },
                        onClick = {
                            showMenu = false
                            onCopy()
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_content_copy_round),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.copy_username)) },
                        onClick = {
                            showMenu = false
                            onCopyUsername()
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_login_round),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        enabled = decryptedLogin.isNotEmpty(),
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.delete), color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete_round),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                    )
                }
            }
        }
    }
}
