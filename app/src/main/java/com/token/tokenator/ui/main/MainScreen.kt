package com.token.tokenator.ui.main

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.token.tokenator.R
import com.token.tokenator.model.Type
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.navigation.Route
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.IntentHelper
import com.token.tokenator.utilities.Tokenator
import com.token.tokenator.utilities.shareAppIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val token by viewModel.token.collectAsStateWithLifecycle()
    val length by viewModel.length.collectAsStateWithLifecycle()
    val switchUpperCase by viewModel.switchUpperCase.collectAsStateWithLifecycle()
    val switchLowerCase by viewModel.switchLowerCase.collectAsStateWithLifecycle()
    val switchNumeric by viewModel.switchNumeric.collectAsStateWithLifecycle()
    val switchSpecial by viewModel.switchSpecialCharacter.collectAsStateWithLifecycle()
    val showTokenNameLabel by viewModel.tokenNameEditTextLabelVisibility.collectAsStateWithLifecycle()
    val showTokenNameField by viewModel.tokenNameEditTextFieldVisibility.collectAsStateWithLifecycle()
    val noRepeat by viewModel.noRepeatFlow.collectAsStateWithLifecycle(initialValue = true)
    val passphrase by viewModel.passphrase.collectAsStateWithLifecycle()
    val allCharacters by viewModel.allCharacters.collectAsStateWithLifecycle()

    var tokenName by remember { mutableStateOf("") }
    var loginName by remember { mutableStateOf("") }
    var sliderValue by remember { mutableStateOf(8f) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    val yellow = Color(0xFFFD7014)
    val lightBlue = Color(0xFF0195E6)
    val blackish = Color(0xFF222831)

    LaunchedEffect(viewModel.shouldShowEasterEggToast) {
        viewModel.shouldShowEasterEggToast.collect {
            if (it) {
                Toast.makeText(context, "You make touch", Toast.LENGTH_SHORT).show()
                viewModel.setShouldShowToastToFalse()
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_tokenator),
                    contentDescription = stringResource(R.string.touch),
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clickable { viewModel.showEasterEggToast() },
                )
                Row {
                    IconButton(onClick = { shareAppIntent(context) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share_round),
                            contentDescription = null,
                            tint = yellow,
                        )
                    }
                    IconButton(onClick = { navigator.navigate(Route.Settings) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings_round),
                            contentDescription = null,
                            tint = yellow,
                        )
                    }
                    IconButton(onClick = { navigator.navigate(Route.SavedToken) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bookmark_round),
                            contentDescription = null,
                            tint = yellow,
                        )
                    }
                }
            }
        },
        containerColor = blackish,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.quick_settings),
                color = lightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            SettingsSwitch(
                label = stringResource(R.string.uppercase_letters),
                iconRes = R.drawable.ic_abc_upper,
                checked = switchUpperCase,
                onCheckedChange = { viewModel.saveSwitchState(Type.UPPERCASE, it) },
            )
            SettingsSwitch(
                label = stringResource(R.string.lowercase_letters),
                iconRes = R.drawable.ic_abc_lower,
                checked = switchLowerCase,
                onCheckedChange = { viewModel.saveSwitchState(Type.LOWERCASE, it) },
            )
            SettingsSwitch(
                label = stringResource(R.string.numeric),
                iconRes = R.drawable.ic_123,
                checked = switchNumeric,
                onCheckedChange = { viewModel.saveSwitchState(Type.NUMERIC, it) },
            )
            SettingsSwitch(
                label = stringResource(R.string.special_characters),
                iconRes = R.drawable.ic_special,
                checked = switchSpecial,
                onCheckedChange = { viewModel.saveSwitchState(Type.SPECIAL, it) },
            )

            Text(
                text = stringResource(R.string.length),
                color = lightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 8f..100f,
                    steps = 92,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(Color.White, shape = MaterialTheme.shapes.small),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = sliderValue.toInt().toString(), color = Color.Black)
                }
            }

            Button(
                onClick = {
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
                            includePhrase = passphrase?.phrase ?: "",
                        )

                    if (generated.isNotEmpty()) {
                        viewModel.setToken(generated)
                        viewModel.setLength(sliderValue)
                        viewModel.setTokenNameEditTextLabelVisible()
                        viewModel.setTokenNameEditTextFieldVisibility()
                    } else {
                        Toast.makeText(context, R.string.toast_length_warning, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
            ) {
                Text(text = stringResource(R.string.generate_token_button_text))
            }

            if (showTokenNameLabel) {
                Text(
                    text = stringResource(R.string.optional),
                    color = lightBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (showTokenNameField) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = tokenName,
                        onValueChange = { tokenName = it },
                        placeholder = { Text(stringResource(R.string.enter_name_for_password)) },
                        modifier = Modifier.weight(1f),
                    )
                    if (tokenName.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.insert(tokenName, token, loginName)
                            Toast.makeText(context, R.string.password_saved, Toast.LENGTH_SHORT).show()
                            tokenName = ""
                            loginName = ""
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = null,
                                tint = yellow,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = loginName,
                    onValueChange = { loginName = it },
                    placeholder = { Text(stringResource(R.string.enter_name_for_login)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Text(
                text = stringResource(R.string.generated_token),
                color = lightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray, shape = MaterialTheme.shapes.medium)
                        .clickable {
                            if (token.isNotEmpty()) {
                                Clipuous.copyToClipboard(token, context)
                                Toast
                                    .makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }.padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (token.isEmpty()) stringResource(R.string.click_to_generate_password) else token,
                    color = if (token.isEmpty()) Color.Gray else yellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (token.isNotEmpty()) {
                IconButton(
                    onClick = { IntentHelper.handleShareClick(token, context) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share_round),
                        contentDescription = null,
                        tint = yellow,
                    )
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.tokenator),
                    color = Color.Gray,
                    fontSize = 24.sp,
                )
                Text(
                    text = viewModel.version,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Text(
                    text = stringResource(R.string.privacy_policy),
                    color = yellow,
                    modifier =
                        Modifier
                            .clickable { showPrivacyPolicy = true }
                            .padding(bottom = 32.dp),
                )
            }
        }
    }

    if (showPrivacyPolicy) {
        com.token.tokenator.ui.components.PrivacyPolicyDialog(
            onDismiss = { showPrivacyPolicy = false },
        )
    }
}

@Composable
fun SettingsSwitch(
    label: String,
    iconRes: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(Color.DarkGray, shape = MaterialTheme.shapes.small)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = Color.White, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
