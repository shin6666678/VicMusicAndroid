package com.shin.vicmusic.feature.artist.artistList.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shin.vicmusic.core.design.theme.LocalAppColors

@Composable
fun FilterSection(
    selectedRegion: String,
    selectedType: String,
    selectedStyle: String,
    onRegionChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onStyleChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        FilterRow(
            options = listOf("全部", "内地", "港台", "欧美", "日本", "韩国"),
            selectedOption = selectedRegion,
            onOptionSelected = onRegionChange
        )
        FilterRow(
            options = listOf("全部", "男", "女", "组合"),
            selectedOption = selectedType,
            onOptionSelected = onTypeChange
        )
        FilterRow(
            options = listOf("全部", "流行", "说唱", "国风", "摇滚", "电子"),
            selectedOption = selectedStyle,
            onOptionSelected = onStyleChange
        )
    }
}

@Composable
fun FilterRow(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        items(options) { option ->
            val isSelected = option == selectedOption
            FilterChip(
                selected = isSelected,
                onClick = { onOptionSelected(option) },
                label = { Text(text = option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1DB954),
                    selectedLabelColor = Color.White,
                    containerColor = Color.Transparent,
                    labelColor = LocalAppColors.current.textColor
                ),
                border = null,
                shape = RoundedCornerShape(50)
            )
        }
    }
}