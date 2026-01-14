package com.shin.vicmusic.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shin.vicmusic.R
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.design.theme.VicMusicTheme
import com.shin.vicmusic.core.model.api.AppUpdateDto
import com.shin.vicmusic.feature.main.navigateToMain
import com.shin.vicmusic.util.SuperDateUtil

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val timeLeft by viewModel.timeLeft.collectAsStateWithLifecycle()
    val navigateToMain by viewModel.navigateToMain.collectAsState()
    val updateInfo by viewModel.updateState.collectAsStateWithLifecycle()
    val releaseNotes by viewModel.releaseNoteState.collectAsStateWithLifecycle()
    val downloadProgress by viewModel.downloadProgress.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        SplashScreen(
            year = SuperDateUtil.currentYear(),
            timeLeft = timeLeft,
            onSkipAdClick = viewModel::onSkipAdClick
        )

        if (releaseNotes != null) {
            ReleaseNoteDialog(
                content = releaseNotes!!,
                onConfirm = viewModel::onReleaseNoteConfirm
            )
        } else if (updateInfo != null) {
            UpdateDialog(
                updateInfo = updateInfo!!,
                downloadProgress = downloadProgress,
                onUpdate = {
                    viewModel.onConfirmUpdate()
                },
                onCancel = {
                    viewModel.onIgnoreUpdate()
                }
            )
        }
    }

    LaunchedEffect(navigateToMain) {
        if (navigateToMain) {
            navController.navigateToMain()
        }
    }
}

@Composable
fun SplashScreen(
    year: Int = 2024,
    onSkipAdClick: () -> Unit = {},
    timeLeft: Long = 0
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 120.dp)
                .align(Alignment.TopCenter)
                .size(200.dp)
        )
        Text(
            text = "维克音乐 , 一起聆听世界",
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = 70.dp)
                .align(Alignment.BottomCenter)
        )
        Text(
            text = stringResource(id = R.string.copyright, year),
            color = Color.Black,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(30.dp)
                .align(Alignment.BottomCenter)
        )
        if (timeLeft > 0) {
            Text(
                text = "倒计时,$timeLeft",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 100.dp)
                    .clickable { onSkipAdClick() }
            )
        }
    }
}

@Composable
fun ReleaseNoteDialog(
    content: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "更新说明") },
        text = { Text(text = content) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "知道了")
            }
        }
    )
}

@Composable
fun UpdateDialog(
    updateInfo: AppUpdateDto,
    downloadProgress: Int,
    onUpdate: () -> Unit,
    onCancel: () -> Unit
) {
    val isDownloading = downloadProgress in 0..99

    AlertDialog(
        onDismissRequest = {
            if (!updateInfo.isForce && !isDownloading) {
                onCancel()
            }
        },
        title = { Text(text = "发现新版本 ${updateInfo.version}") },
        text = {
            Column {
                Text(text = updateInfo.content ?: "为了更好的体验，请升级到最新版本")
                if (downloadProgress >= 0) {
                    Spacer(Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { downloadProgress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if(downloadProgress == 100) "下载完成" else "正在下载: $downloadProgress%",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onUpdate, enabled = !isDownloading) {
                Text(text = if(isDownloading) "下载中..." else "立即更新")
            }
        },
        dismissButton = {
            if (!updateInfo.isForce) {
                TextButton(onClick = onCancel, enabled = !isDownloading) {
                    Text(text = "暂不更新", color = if(isDownloading) Color.Gray.copy(alpha=0.5f) else Color.Gray)
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun SplashRoutePreView() {
    VicMusicTheme {
        SplashScreen()
    }
}