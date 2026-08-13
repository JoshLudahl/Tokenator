package com.token.tokenator.ui.security

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.token.tokenator.R
import com.token.tokenator.navigation.Navigator

@Composable
fun SecurityScreen(
    navigator: Navigator,
    viewModel: SecurityViewModel = hiltViewModel(),
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // App Icon
        Box(
            modifier =
                Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_tokenator),
                contentDescription = stringResource(R.string.logo),
                modifier = Modifier.size(60.dp),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Enter Vault PIN",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Enter your 4-digit security code",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(48.dp))

        TextField(
            value = pin,
            onValueChange = { if (it.length <= 4) pin = it },
            modifier =
                Modifier
                    .width(200.dp)
                    .clip(MaterialTheme.shapes.medium),
            textStyle =
                MaterialTheme.typography.displayLarge.copy(
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp,
                ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { /* Handle enter */ },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            shape = MaterialTheme.shapes.medium,
            enabled = pin.length == 4,
        ) {
            Text(
                text = stringResource(R.string.enter),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { /* Handle forgot PIN */ }) {
            Text(
                text = "Forgot PIN?",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
