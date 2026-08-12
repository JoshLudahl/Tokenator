package com.token.tokenator.ui.savedpassword

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.token.tokenator.model.Token
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.navigation.Route
import com.token.tokenator.ui.theme.FinTextDark
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.Encryption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedTokenScreen(
    navigator: Navigator,
    viewModel: SavedTokenViewModel = hiltViewModel(),
) {
    val tokens by viewModel.tokens.collectAsStateWithLifecycle()
    val noTokens by viewModel.noTokens.collectAsStateWithLifecycle()
    var tokenToDelete by remember { mutableStateOf<Token?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Vault",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_circle_left),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search or filter */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_abc_upper), // Placeholder for dots/search
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats/Info Card (Optional, similar to "Budget for October" in image)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = FinTextDark)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Passwords",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = tokens.size.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tokenator),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Recent Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = FinTextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (noTokens) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.no_saved_passwords),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(tokens) { token ->
                        FinanceTokenItem(
                            token = token,
                            onDelete = { tokenToDelete = it },
                            onEdit = { navigator.navigate(Route.PasswordDetail(it.id)) }
                        )
                    }
                }
            }
        }
    }

    if (tokenToDelete != null) {
        AlertDialog(
            onDismissRequest = { tokenToDelete = null },
            title = { Text(text = "Delete Password?") },
            text = { Text(text = "Are you sure you want to remove this item from your vault?") },
            confirmButton = {
                TextButton(onClick = {
                    tokenToDelete?.let { viewModel.delete(it) }
                    tokenToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { tokenToDelete = null }) {
                    Text("Cancel")
                }
            },
            shape = MaterialTheme.shapes.large,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun FinanceTokenItem(
    token: Token,
    onDelete: (Token) -> Unit,
    onEdit: (Token) -> Unit,
) {
    val context = LocalContext.current
    val decryptedLogin = remember(token.login) {
        token.login?.let { Encryption.decrypt(it) } ?: ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(token) },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Soft-colored icon background
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tokenator),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = token.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FinTextDark,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (decryptedLogin.isNotEmpty()) decryptedLogin else "No login saved",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = {
                    val fullToken = Encryption.decrypt(token.token) ?: ""
                    Clipuous.copyToClipboard(fullToken, context)
                    Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_content_copy_round),
                        contentDescription = null,
                        tint = FinTextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
