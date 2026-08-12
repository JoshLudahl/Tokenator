package com.token.tokenator.ui.settings

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type
import com.token.tokenator.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class)
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

    val yellow = Color(0xFFFD7014)
    val lightBlue = Color(0xFF0195E6)
    val blackish = Color(0xFF222831)

    val groupedCharacters = remember(allCharacters) {
        allCharacters.groupBy { it.category }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
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
        ) {
            // ... Passphrase and No Repeat sections remain unchanged ...
            Text(
                text = stringResource(R.string.passphrase),
                color = lightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF393E46), shape = MaterialTheme.shapes.small)
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_passphrase),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(16.dp))
                TextField(
                    value = passphraseText,
                    onValueChange = { passphraseText = it },
                    placeholder = { Text(stringResource(R.string.enter_a_passphrase)) },
                    modifier = Modifier.weight(1f),
                    colors =
                        TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                    maxLines = 1,
                )
                IconButton(onClick = {
                    viewModel.insertPassphrase(passphraseText)
                    Toast.makeText(context, R.string.passphrase_saved, Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save_round),
                        contentDescription = null,
                        tint = yellow,
                    )
                }
                Switch(
                    checked = switchPassphrase,
                    onCheckedChange = { viewModel.updatePassphrase(it) },
                )
            }

            Text(
                text = stringResource(R.string.no_repeat_characters),
                color = lightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF393E46), shape = MaterialTheme.shapes.small)
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_repeat),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.no_repeat_characters),
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = switchNoRepeat,
                    onCheckedChange = { viewModel.updateNoRepeat(it) },
                )
            }

            Text(
                text = stringResource(R.string.characters),
                color = lightBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )

            // Sections for each type
            CategorySection(
                title = stringResource(R.string.uppercase_letters),
                items = groupedCharacters[Type.UPPERCASE] ?: emptyList(),
                onItemClick = { viewModel.updateItems(it) }
            )
            CategorySection(
                title = stringResource(R.string.lowercase_letters),
                items = groupedCharacters[Type.LOWERCASE] ?: emptyList(),
                onItemClick = { viewModel.updateItems(it) }
            )
            CategorySection(
                title = stringResource(R.string.numeric),
                items = groupedCharacters[Type.NUMERIC] ?: emptyList(),
                onItemClick = { viewModel.updateItems(it) }
            )
            CategorySection(
                title = stringResource(R.string.special_characters),
                items = groupedCharacters[Type.SPECIAL] ?: emptyList(),
                onItemClick = { viewModel.updateItems(it) }
            )
        }
    }
}

@Composable
fun CategorySection(
    title: String,
    items: List<SettingsItem>,
    onItemClick: (SettingsItem) -> Unit
) {
    if (items.isEmpty()) return

    Text(
        text = title,
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )

    CharacterGrid(
        items = items,
        onItemClick = onItemClick
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterGrid(
    items: List<SettingsItem>,
    onItemClick: (SettingsItem) -> Unit,
) {
    FlowRow(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color(0xFF393E46), shape = MaterialTheme.shapes.small)
                .padding(8.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        items.forEach { item ->
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(
                            if (item.included) Color(0xFFFD7014) else Color.White,
                            shape = MaterialTheme.shapes.extraSmall,
                        )
                        .clickable {
                            val newItem = item.copy(included = !item.included)
                            Log.d(
                                "SettingsScreen",
                                "Tapped: ${item.item}, Old: ${item.included}, New: ${newItem.included}, ID: ${newItem.id}"
                            )
                            onItemClick(newItem)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.item,
                    color = if (item.included) Color.Black else Color.DarkGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
