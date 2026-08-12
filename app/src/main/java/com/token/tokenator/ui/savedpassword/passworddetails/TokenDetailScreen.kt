package com.token.tokenator.ui.savedpassword.passworddetails

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    val yellow = Color(0xFFFD7014)
    val lightBlue = Color(0xFF0195E6)
    val blackish = Color(0xFF222831)

    LaunchedEffect(passwordId) {
        viewModel.getToken(passwordId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.password_detail),
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_circle_left),
                            contentDescription = null,
                            tint = Color.Black,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = yellow,
                        titleContentColor = blackish,
                        navigationIconContentColor = blackish,
                    ),
            )
        },
        containerColor = blackish,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DetailTextField(
                label = stringResource(R.string.name_entry_field),
                value = tokenName,
                onValueChange = { tokenName = it },
                iconRes = R.drawable.ic_label_round,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailTextField(
                label = stringResource(R.string.login_input_field),
                value = loginName,
                onValueChange = { loginName = it },
                iconRes = R.drawable.ic_login_round,
                onCopy = {
                    Clipuous.copyToClipboard(loginName, context)
                    Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailTextField(
                label = stringResource(R.string.token),
                value = passwordValue,
                onValueChange = { passwordValue = it },
                iconRes = R.drawable.ic_password_round,
                onCopy = {
                    Clipuous.copyToClipboard(passwordValue, context)
                    Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                },
            )

            Button(
                onClick = {
                    viewModel.insert(tokenName, passwordValue, loginName)
                    Toast.makeText(context, R.string.password_saved, Toast.LENGTH_SHORT).show()
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
                enabled = tokenName.isNotEmpty(),
            ) {
                Text(text = stringResource(R.string.save_button))
            }

            if (shouldShowWarning) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_danger_circle),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color.Unspecified,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.outdated_password_message),
                        color = yellow,
                        fontSize = 14.sp,
                    )
                }
            }

            IconButton(
                onClick = { IntentHelper.handleShareClick(passwordValue, context) },
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share_round),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = yellow,
                )
            }
        }
    }
}

@Composable
fun DetailTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    iconRes: Int,
    onCopy: (() -> Unit)? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray,
                )
            },
            trailingIcon =
                if (onCopy != null) {
                    {
                        IconButton(onClick = onCopy) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_content_copy_round),
                                contentDescription = null,
                                tint = Color.Gray,
                            )
                        }
                    }
                } else {
                    null
                },
            modifier = Modifier.fillMaxWidth(),
            colors =
                TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF393E46),
                    unfocusedContainerColor = Color(0xFF393E46),
                    focusedIndicatorColor = Color(0xFFFD7014),
                    unfocusedIndicatorColor = Color.Transparent,
                ),
        )
    }
}
