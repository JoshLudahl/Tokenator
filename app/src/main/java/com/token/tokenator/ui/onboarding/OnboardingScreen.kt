package com.token.tokenator.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.token.tokenator.R
import com.token.tokenator.navigation.Navigator
import com.token.tokenator.navigation.Route
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    navigator: Navigator,
    viewModel: OnboardingViewModel = hiltViewModel<OnboardingViewModel>(),
) {
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()

    val pages =
        listOf(
            OnboardingPage(
                title = stringResource(R.string.onboarding_title_1),
                description = stringResource(R.string.onboarding_description_1),
            ),
            OnboardingPage(
                title = stringResource(R.string.onboarding_title_2),
                description = stringResource(R.string.onboarding_description_2),
            ),
            OnboardingPage(
                title = stringResource(R.string.onboarding_title_3),
                description = stringResource(R.string.onboarding_description_3),
            ),
        )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(3) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        }
                    Box(
                        modifier =
                            Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(10.dp),
                    )
                }
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {
                        viewModel.completeOnboarding()
                        navigator.navigate(Route.Main)
                    },
                ) {
                    Text(text = stringResource(R.string.onboarding_button_skip))
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            viewModel.completeOnboarding()
                            navigator.navigate(Route.Main)
                        }
                    },
                ) {
                    Text(
                        text =
                            if (pagerState.currentPage < 2) {
                                stringResource(R.string.onboarding_button_next)
                            } else {
                                stringResource(R.string.onboarding_button_get_started)
                            },
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
        )
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
)
