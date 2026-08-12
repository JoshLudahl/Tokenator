package com.token.tokenator.ui.savedpassword

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            TopAppBar(
                title = { Text(text = stringResource(R.string.saved_passwords)) },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_circle_left),
                            contentDescription = null,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFD7014),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                    ),
            )
        },
        containerColor = Color(0xFF222831), // blackish
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
        ) {
            if (noTokens) {
                Text(
                    text = stringResource(R.string.no_saved_passwords),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(tokens) { token ->
                        TokenItem(
                            token = token,
                            onDelete = { tokenToDelete = it },
                            onEdit = {
                                navigator.navigate(Route.PasswordDetail(it.id))
                            },
                        )
                    }
                }
            }
        }
    }

    if (tokenToDelete != null) {
        AlertDialog(
            onDismissRequest = { tokenToDelete = null },
            title = { Text(text = stringResource(R.string.alert_confirm_delete)) },
            text = { Text(text = stringResource(R.string.alert_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    tokenToDelete?.let { viewModel.delete(it) }
                    tokenToDelete = null
                }) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { tokenToDelete = null }) {
                    Text(text = stringResource(R.string.no))
                }
            },
        )
    }
}

@Composable
fun TokenItem(
    token: Token,
    onDelete: (Token) -> Unit,
    onEdit: (Token) -> Unit,
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }
    val decryptedToken =
        remember(token.token, isVisible) {
            if (isVisible) Encryption.decrypt(token.token) ?: "Error" else "********"
        }
    val decryptedLogin =
        remember(token.login) {
            token.login?.let { Encryption.decrypt(it) } ?: ""
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onEdit(token) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF393E46)), // dark_gray
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.pencil_circle),
                        contentDescription = stringResource(R.string.edit_saved_token),
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = token.title,
                        color = Color(0xFF9EABC1), // grayish
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                if (decryptedLogin.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_login_round),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF9EABC1),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = decryptedLogin,
                            color = Color(0xFFFD7014),
                            fontSize = 14.sp,
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_password_round),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF9EABC1),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = decryptedToken,
                        color = Color(0xFFFD7014),
                        fontSize = 14.sp,
                    )
                }
            }

            Row {
                IconButton(
                    onClick = { isVisible = !isVisible },
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape),
                ) {
                    Icon(
                        painter =
                            painterResource(
                                id = if (isVisible) R.drawable.ic_visibility_round else R.drawable.ic_visibility_off_round,
                            ),
                        contentDescription = null,
                        tint = Color(0xFF222831),
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = {
                        val fullToken = Encryption.decrypt(token.token) ?: ""
                        Clipuous.copyToClipboard(fullToken, context)
                        Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                    },
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_content_copy_round),
                        contentDescription = null,
                        tint = Color(0xFF222831),
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { onDelete(token) },
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete_round),
                        contentDescription = null,
                        tint = Color(0xFF222831),
                    )
                }
            }
        }
    }
}
