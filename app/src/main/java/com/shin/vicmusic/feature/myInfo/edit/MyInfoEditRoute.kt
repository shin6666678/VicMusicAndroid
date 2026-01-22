package com.shin.vicmusic.feature.myInfo.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.feature.common.MyAsyncImage
import com.shin.vicmusic.feature.common.bar.BarActionItem
import com.shin.vicmusic.feature.common.bar.BarTabItem
import com.shin.vicmusic.feature.common.bar.CommonTopBarSelect

@Preview
@Composable
fun MyInfoEditPreview() {
    MyInfoEditScreen(
        uiState = MyInfoEditUiState(
            name = "Shin",
            headImg = "https://picsum.photos/200",
            bgImg = "https://picsum.photos/200",
            slogan = "Hello, World!",
            sex = 1,
            mail = "shin@vicmusic.com"
        ),
        onNameChange = {},
        onSloganChange = {},
        onSexChange = {},
        onAvatarClick = {},
        onSaveClick = {},
        onBackClick = {}
    )
}

@Composable
fun MyInfoEditRoute(
    viewModel: MyInfoEditViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            navController.popBackStack()
        }
    }

    MyInfoEditScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onSloganChange = viewModel::onSloganChange,
        onSexChange = viewModel::onSexChange,
        onAvatarClick = { /* TODO: Implement Image Picker */ },
        onSaveClick = viewModel::saveChanges,
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInfoEditScreen(
    uiState: MyInfoEditUiState,
    onNameChange: (String) -> Unit,
    onSloganChange: (String) -> Unit,
    onSexChange: (Int) -> Unit,
    onAvatarClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    // --- 颜色定义 (亮色模式) ---
    val backgroundColor = Color.White
    val contentColor = Color(0xFF212121)
    val dividerColor = Color(0xFFEEEEEE)
    val primaryBrandColor = Color(0xFFFF5722)

    // 性别下拉框状态
    var genderExpanded by remember { mutableStateOf(false) }
    // 选项映射: 显示文本 -> 值
    val genderOptions = listOf("男" to 1, "女" to 0, "保密" to 2)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CommonTopBarSelect(
                backImageVictor=Icons.Default.Close,
                onBackClick = onBackClick,
                tabs = listOf(
                    BarTabItem(
                        isSelected = true,
                        name = "编辑个人资料",
                        onClick = {}
                    ),
                ),
                actions = listOf(
                    BarActionItem(
                        icon = Icons.Default.Check,
                        onClick = onSaveClick
                    )
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // --- 头像区域 ---
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(100.dp)
                    .clickable(onClick = onAvatarClick)
            ) {
                if (uiState.headImg.isNotEmpty()) {
                    MyAsyncImage(
                        model = uiState.headImg,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(primaryBrandColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.name.firstOrNull()?.toString()?.uppercase() ?: "S",
                            style = TextStyle(color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .offset(x = (-4).dp, y = (-4).dp)
                        .size(28.dp)
                        .background(Color(0xFF333333), RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- 表单区域 ---

            // 1. 昵称
            EditProfileItem(
                label = "名称",
                value = uiState.name,
                onValueChange = onNameChange,
                textColor = contentColor,
                dividerColor = dividerColor,
                cursorColor = primaryBrandColor
            )

            // 2. 签名
            EditProfileItem(
                label = "签名",
                value = uiState.slogan,
                onValueChange = onSloganChange,
                textColor = contentColor,
                dividerColor = dividerColor,
                cursorColor = primaryBrandColor
            )

            // 3. 性别 (修改点 2：下拉框 Dropdown)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { genderExpanded = true }, // 点击整行触发下拉
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Label
                    Text(
                        text = "性别",
                        style = TextStyle(color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.width(80.dp)
                    )

                    // Value Area (Dropdown Anchor)
                    Box(modifier = Modifier.weight(1f)) {
                        // 获取当前性别对应的文本
                        val currentText = genderOptions.find { it.second == uiState.sex }?.first ?: "保密"

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentText,
                                style = TextStyle(color = contentColor, fontSize = 16.sp),
                                modifier = Modifier.weight(1f)
                            )
                            // 可选：添加一个小箭头提示是下拉菜单
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }

                        // 下拉菜单内容
                        DropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            genderOptions.forEach { (label, value) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = label,
                                            // 选中项高亮显示
                                            color = if(uiState.sex == value) primaryBrandColor else contentColor
                                        )
                                    },
                                    onClick = {
                                        onSexChange(value)
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
            }
        }
    }
}

/**
 * 自定义单行输入项
 */
@Composable
fun EditProfileItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    textColor: Color,
    dividerColor: Color,
    cursorColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = TextStyle(color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.width(80.dp)
            )

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = textColor, fontSize = 16.sp),
                singleLine = true,
                cursorBrush = SolidColor(cursorColor),
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
    }
}