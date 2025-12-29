package com.shin.vicmusic.feature.splash

// import android.content.Intent // [移除] 不需要 Intent 了
// import android.net.Uri // [移除] 不需要 Uri 了
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
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
    // 使用 hiltViewModel 以便注入 Repository
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val timeLeft by viewModel.timeLeft.collectAsStateWithLifecycle()
    val navigateToMain by viewModel.navigateToMain.collectAsState()
    val updateInfo by viewModel.updateState.collectAsStateWithLifecycle()
    val releaseNotes by viewModel.releaseNoteState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景页面
        SplashScreen(
            year = SuperDateUtil.currentYear(),
            timeLeft = timeLeft,
            onSkipAdClick = viewModel::onSkipAdClick
        )

        // 优先级1：本次更新内容说明（首次进入时显示）
        if (releaseNotes != null) {
            ReleaseNoteDialog(
                content = releaseNotes!!,
                onConfirm = viewModel::onReleaseNoteConfirm
            )
        }
        // 优先级2：检查是否有更新的版本（UpdateInfo）
        else if (updateInfo != null) {
            UpdateDialog(
                updateInfo = updateInfo!!,
                onUpdate = { _ ->
                    viewModel.onConfirmUpdate()
                },
                onCancel = {
                    // 暂不更新 -> 进入主页
                    viewModel.onIgnoreUpdate()
                }
            )
        }
    }

    // 只有在没有更新弹窗且倒计时结束时才导航
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
        // 只有倒计时大于0才显示倒计时按钮
        if (timeLeft > 0) {
            Text(
                text = "倒计时,$timeLeft",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 100.dp)
                    .clickable {
                        onSkipAdClick()
                    }
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
        onDismissRequest = { /* 强制用户点击确认，不允许点击外部关闭 */ },
        title = { Text(text = "更新说明") },
        text = {
            Text(text = content)
        },
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
    onUpdate: (String?) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // 如果不是强制更新，允许点击外部关闭
            if (!updateInfo.isForce) {
                onCancel()
            }
        },
        title = { Text(text = "发现新版本 ${updateInfo.version}") },
        text = {
            Text(text = updateInfo.content ?: "为了更好的体验，请升级到最新版本")
        },
        confirmButton = {
            TextButton(onClick = { onUpdate(updateInfo.downloadUrl) }) {
                Text(text = "立即更新")
            }
        },
        dismissButton = {
            // 仅非强制更新显示取消按钮
            if (!updateInfo.isForce) {
                TextButton(onClick = onCancel) {
                    Text(text = "暂不更新", color = Color.Gray)
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