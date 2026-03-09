package com.shin.vicmusic.feature.playlist.sheet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shin.vicmusic.core.design.theme.LocalAppColors
import com.shin.vicmusic.core.domain.Playlist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSettingSheet(
    info: Playlist,
    onDismissRequest: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCoverClick: (String) -> Unit,
    onPublicStatusChange: (Int) -> Unit,
    onSaveClick: () -> Unit
) {
    // 1. 本地状态：用于实时编辑预览，不直接触发网络请求
    var localName by remember { mutableStateOf(info.name) }
    var localDescription by remember { mutableStateOf(info.description) }
    var localCover by remember { mutableStateOf(info.cover) }
    var localIsPublic by remember { mutableIntStateOf(info.isPublic) }

    // 图片选择器逻辑 (假设你已有通用图片选择封装)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            localCover = it.toString()
            onCoverClick(it.toString())
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = LocalAppColors.current.gradientMid,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "编辑歌单信息",
                color = LocalAppColors.current.textColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // --- 2. 封面编辑区 ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = localCover,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            }

            // --- 3. 标题编辑区 ---
            OutlinedTextField(
                value = localName,
                onValueChange = {
                    localName = it
                    onNameChange(it) // 同步回 ViewModel 的 UiState
                },
                label = { Text("歌单名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // --- 4. 简介编辑区 ---
            OutlinedTextField(
                value = localDescription,
                onValueChange = {
                    localDescription = it
                    onDescriptionChange(it)
                },
                label = { Text("歌单简介") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // --- 5. 隐私切换区 ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val next = if (localIsPublic == 1) 0 else 1
                        localIsPublic = next
                        onPublicStatusChange(next)
                    }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RemoveRedEye, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(text = "隐私歌单", color = LocalAppColors.current.textColor)
                }
                Switch(
                    checked = localIsPublic == 1,
                    onCheckedChange = {
                        val next = if (it) 1 else 0
                        localIsPublic = next
                        onPublicStatusChange(next)
                    }
                )
            }

            Button(
                onClick = {
                    onSaveClick()
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("保存修改")
            }
        }
    }
}