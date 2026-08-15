package com.token.tokenator.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.token.tokenator.R
import com.token.tokenator.model.Token
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.navigation.Route
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.Encryption
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val tokens by viewModel.tokens.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentSortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val messageText = snackbarMessage?.let { stringResource(id = it) }

    val searchBarState = rememberSearchBarState()
    val searchTextFieldState = rememberTextFieldState(initialText = searchQuery)

    var tokenToDelete by remember { mutableStateOf<Token?>(null) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var sortButtonX by remember { mutableFloatStateOf(0f) }

    val listState = rememberLazyListState()
    val searchListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val copiedText = stringResource(id = R.string.toast_copied_to_clipboard)
    val usernameCopiedText = stringResource(id = R.string.toast_username_copied)
    val passwordDeletedText = stringResource(id = R.string.toast_password_deleted)

    LaunchedEffect(currentSortOrder) {
        listState.animateScrollToItem(0)
        searchListState.animateScrollToItem(0)
    }

    LaunchedEffect(messageText) {
        messageText?.let { text ->
            snackbarHostState.showSnackbar(text)
            viewModel.clearSnackbar()
        }
    }

    LaunchedEffect(searchTextFieldState) {
        snapshotFlow { searchTextFieldState.text }.collect {
            viewModel.setSearchQuery(it.toString())
        }
    }

    // Sync external searchQuery changes to TextFieldState (e.g. on clear)
    LaunchedEffect(searchQuery) {
        if (searchQuery != searchTextFieldState.text.toString()) {
            searchTextFieldState.setTextAndPlaceCursorAtEnd(searchQuery)
        }
    }

    val floatingToolbarScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(
            exitDirection = FloatingToolbarExitDirection.Bottom,
        )

    Scaffold(
        modifier =
            Modifier
                .nestedScroll(floatingToolbarScrollBehavior)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { sortMenuExpanded = false },
        topBar = {
            val inputField = @Composable {
                SearchBarDefaults.InputField(
                    textFieldState = searchTextFieldState,
                    searchBarState = searchBarState,
                    onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                    placeholder = { Text(stringResource(id = R.string.search_passwords)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = stringResource(id = R.string.search_passwords),
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    trailingIcon = {
                        if (searchTextFieldState.text.isNotEmpty()) {
                            IconButton(onClick = { searchTextFieldState.clearText() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.close),
                                    contentDescription = stringResource(id = R.string.search_clear),
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    },
                )
            }

            TopAppBar(
                title = {
                    SearchBar(
                        state = searchBarState,
                        inputField = inputField,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .heightIn(max = 56.dp),
                        colors =
                            SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                dividerColor = Color.Transparent,
                            ),
                    )
                },
                actions = {
                    // Settings button moved to HorizontalFloatingToolbar
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 80.dp),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                        color = MaterialTheme.colorScheme.primary,
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
                        state = listState,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 90.dp),
                    ) {
                        items(tokens, key = { it.id }) { token ->
                            Box(modifier = Modifier.animateItem()) {
                                VaultTokenItem(
                                    token = token,
                                    onCopy = {
                                        val fullToken = Encryption.decrypt(token.token) ?: ""
                                        Clipuous.copyToClipboard(fullToken, context, isSensitive = true)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(copiedText)
                                        }
                                    },
                                    onCopyUsername = {
                                        val login = token.login?.let { Encryption.decrypt(it) } ?: ""
                                        if (login.isNotEmpty()) {
                                            Clipuous.copyToClipboard(login, context)
                                            scope.launch {
                                                snackbarHostState.showSnackbar(usernameCopiedText)
                                            }
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

            HorizontalFloatingToolbar(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = paddingValues.calculateBottomPadding() + FloatingToolbarDefaults.ScreenOffset)
                        .zIndex(1f),
                expanded = true,
                leadingContent = {
                    IconButton(
                        onClick = { sortMenuExpanded = !sortMenuExpanded },
                        modifier =
                            Modifier.onGloballyPositioned { coordinates ->
                                sortButtonX = coordinates.positionInRoot().x
                            },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = stringResource(id = R.string.sort),
                        )
                    }
                },
                trailingContent = {
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
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(id = R.string.settings),
                            modifier = Modifier.size(28.dp),
                        )
                    }
                },
                content = {
                    FilledIconButton(
                        modifier = Modifier.width(64.dp),
                        onClick = { navigator.navigate(Route.AddPassword) },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(id = R.string.new_password),
                        )
                    }
                },
                scrollBehavior = floatingToolbarScrollBehavior,
            )

            // FAB Menu (Sort Options)
            AnimatedVisibility(
                visible = sortMenuExpanded,
                enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 },
                modifier =
                    Modifier
                        .offset {
                            androidx.compose.ui.unit
                                .IntOffset(sortButtonX.toInt(), 0)
                        }.align(Alignment.BottomStart)
                        .padding(bottom = 100.dp + paddingValues.calculateBottomPadding())
                        .zIndex(2f),
            ) {
                VerticalFloatingToolbar(
                    expanded = true,
                    content = {
                        IconButton(
                            onClick = {
                                viewModel.setSortOrder(TokenSortOrder.DATE)
                                sortMenuExpanded = false
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Update,
                                contentDescription = stringResource(id = R.string.sort_by_updated),
                                tint = if (currentSortOrder == TokenSortOrder.DATE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.setSortOrder(TokenSortOrder.NAME)
                                sortMenuExpanded = false
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SortByAlpha,
                                contentDescription = stringResource(id = R.string.sort_a_z),
                                tint = if (currentSortOrder == TokenSortOrder.NAME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                )
            }

            // Expanded Search Results
            ExpandedFullScreenSearchBar(
                state = searchBarState,
                inputField = {
                    SearchBarDefaults.InputField(
                        textFieldState = searchTextFieldState,
                        searchBarState = searchBarState,
                        onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                        placeholder = { Text(stringResource(id = R.string.search_passwords)) },
                        leadingIcon = {
                            IconButton(onClick = { scope.launch { searchBarState.animateToCollapsed() } }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(id = R.string.search_back),
                                )
                            }
                        },
                        trailingIcon = {
                            if (searchTextFieldState.text.isNotEmpty()) {
                                IconButton(onClick = { searchTextFieldState.clearText() }) {
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
            ) {
                LazyColumn(
                    state = searchListState,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(tokens, key = { it.id }) { token ->
                        Box(modifier = Modifier.animateItem()) {
                            VaultTokenItem(
                                token = token,
                                onCopy = {
                                    val fullToken = Encryption.decrypt(token.token) ?: ""
                                    Clipuous.copyToClipboard(fullToken, context, isSensitive = true)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(copiedText)
                                    }
                                },
                                onCopyUsername = {
                                    val login = token.login?.let { Encryption.decrypt(it) } ?: ""
                                    if (login.isNotEmpty()) {
                                        Clipuous.copyToClipboard(login, context)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(usernameCopiedText)
                                        }
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
                    scope.launch {
                        snackbarHostState.showSnackbar(passwordDeletedText)
                    }
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
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(start = 16.dp)
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
