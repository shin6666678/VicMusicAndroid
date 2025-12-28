package com.shin.vicmusic.feature.checkIn

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shin.vicmusic.feature.common.CommonTopBar

@Preview
@Composable
fun CheckInScreenPreview() {
    CheckInScreen(
        onCheckInClick = {},
        points = 100,
        isLoading = false
    )
}

@Composable
fun CheckInRoute(
    viewModel: CheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CheckInUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is CheckInUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    CheckInScreen(
        onCheckInClick = viewModel::checkIn,
        points = currentUser?.points ?: 0,
        isLoading = uiState is CheckInUiState.Loading
    )
}

@Composable
fun CheckInScreen(
    onBackClick: () -> Unit={},
    onCheckInClick: () -> Unit,
    points: Int,
    isLoading: Boolean
) {
    Scaffold(
        topBar = { CommonTopBar(midText = "每日签到", popBackStack = onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "当前积分",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Text(
                text = "$points",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 巨大的签到按钮
            Button(
                onClick = onCheckInClick,
                enabled = !isLoading,
                modifier = Modifier.size(150.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "签到", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Get Points", fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "每日签到可获得积分和经验奖励",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}