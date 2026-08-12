package com.token.tokenator.ui.security

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.token.tokenator.R
import com.token.tokenator.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    navigator: Navigator,
    viewModel: SecurityViewModel = hiltViewModel(),
) {
    var pin by remember { mutableStateOf("") }
    val yellow = Color(0xFFFD7014)
    val blackish = Color(0xFF222831)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(yellow)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 4) pin = it },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
            textStyle =
                LocalTextStyle.current.copy(
                    fontSize = 64.sp,
                    textAlign = TextAlign.Center,
                    color = yellow,
                ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors =
                TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = blackish,
                    unfocusedContainerColor = blackish,
                ),
            shape = MaterialTheme.shapes.large,
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Handle enter */ },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = blackish,
                    contentColor = yellow,
                ),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(text = stringResource(R.string.enter))
        }

        Spacer(modifier = Modifier.height(64.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_tokenator),
            contentDescription = stringResource(R.string.logo),
            modifier = Modifier.size(68.dp),
        )
    }
}
