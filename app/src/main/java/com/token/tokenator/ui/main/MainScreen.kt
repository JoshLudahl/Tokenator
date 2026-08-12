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
import com.token.tokenator.model.Type
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.navigation.Route
import com.token.tokenator.ui.theme.FinSurfaceDark
import com.token.tokenator.ui.theme.FinTextDark
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.Tokenator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val token by viewModel.token.collectAsStateWithLifecycle()
    val switchUpperCase by viewModel.switchUpperCase.collectAsStateWithLifecycle()
    val switchLowerCase by viewModel.switchLowerCase.collectAsStateWithLifecycle()
    val switchNumeric by viewModel.switchNumeric.collectAsStateWithLifecycle()
    val switchSpecial by viewModel.switchSpecialCharacter.collectAsStateWithLifecycle()
    val showTokenNameField by viewModel.tokenNameEditTextFieldVisibility.collectAsStateWithLifecycle()
    val noRepeat by viewModel.noRepeatFlow.collectAsStateWithLifecycle(initialValue = true)
    val passphrase by viewModel.passphrase.collectAsStateWithLifecycle()
    val allCharacters by viewModel.allCharacters.collectAsStateWithLifecycle()

    var tokenName by remember { mutableStateOf("") }
    var loginName by remember { mutableStateOf("") }
    var sliderValue by remember { mutableStateOf(8f) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Menu action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_abc_lower), // Temporary placeholder for grid icon
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigator.navigate(Route.SavedToken) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bookmark_round),
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Generated Token Card (Premium Card Look)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
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
                    Text(
                        text = "Generated Token",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (token.isEmpty()) "••••••••" else token,
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "SECURE VAULT",
                            color = Color.White.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 2.sp
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tokenator),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = {
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
                        viewModel.setTokenNameEditTextLabelVisible()
                        viewModel.setTokenNameEditTextFieldVisibility()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
                    .height(64.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = "Generate Token",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showTokenNameField) {
                SectionHeader(text = "Save to Vault")
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp)
                ) {
                    OutlinedTextField(
                        value = tokenName,
                        onValueChange = { tokenName = it },
                        label = { Text("App/Website Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = loginName,
                        onValueChange = { loginName = it },
                        label = { Text("Login/Username") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )

                    if (tokenName.isNotEmpty()) {
                        Button(
                            onClick = {
                                viewModel.insert(tokenName, token, loginName)
                                Toast.makeText(context, R.string.password_saved, Toast.LENGTH_SHORT).show()
                                tokenName = ""
                                loginName = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                                .height(56.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Confirm Save")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Length Section
            SectionHeader(text = "Length")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 8f..100f,
                        steps = 92,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.secondary,
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = sliderValue.toInt().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Options Section
            SectionHeader(text = "Options")
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                FinanceSwitch(
                    label = stringResource(R.string.uppercase_letters),
                    checked = switchUpperCase,
                    onCheckedChange = { viewModel.saveSwitchState(Type.UPPERCASE, it) }
                )
                FinanceSwitch(
                    label = stringResource(R.string.lowercase_letters),
                    checked = switchLowerCase,
                    onCheckedChange = { viewModel.saveSwitchState(Type.LOWERCASE, it) }
                )
                FinanceSwitch(
                    label = stringResource(R.string.numeric),
                    checked = switchNumeric,
                    onCheckedChange = { viewModel.saveSwitchState(Type.NUMERIC, it) }
                )
                FinanceSwitch(
                    label = stringResource(R.string.special_characters),
                    checked = switchSpecial,
                    onCheckedChange = { viewModel.saveSwitchState(Type.SPECIAL, it) }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
            
            // Minimal Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Privacy",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { showPrivacyPolicy = true }
                )
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { navigator.navigate(Route.Settings) }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showPrivacyPolicy) {
        com.token.tokenator.ui.components.PrivacyPolicyDialog(
            onDismiss = { showPrivacyPolicy = false },
        )
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = FinTextDark,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun FinanceSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
    }
}
