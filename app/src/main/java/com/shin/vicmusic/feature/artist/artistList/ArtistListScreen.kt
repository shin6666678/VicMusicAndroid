package com.shin.vicmusic.feature.artist.artistList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shin.vicmusic.core.design.composition.LocalNavController
import com.shin.vicmusic.core.domain.Artist
import com.shin.vicmusic.core.ui.DiscoveryPreviewParameterData.ARTISTS
import com.shin.vicmusic.feature.common.ItemArtist
import com.shin.vicmusic.feature.artist.artistDetail.navigateToArtistDetail
import com.shin.vicmusic.feature.artist.artistList.component.FilterSection
import com.shin.vicmusic.feature.artist.artistList.component.TopArtistSection
import com.shin.vicmusic.feature.common.CommonTopBar
import com.shin.vicmusic.feature.search.navigateToSearch

@Preview
@Composable
fun ArtistListScreenPreview() {
    ArtistListScreen(artists = ARTISTS)
}

@Composable
fun ArtistListRoute(
    viewModel: ArtistListViewModel = hiltViewModel(),
) {
    val artists by viewModel.artist.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val navController = LocalNavController.current
    ArtistListScreen(
        artists = artists,
        filterState = filterState,
        popBackStack = navController::popBackStack,
        navigateToSearch = navController::navigateToSearch,
        onItemClick = navController::navigateToArtistDetail,
        onRegionChange = viewModel::updateRegion,
        onTypeChange = viewModel::updateType,
        onStyleChange = viewModel::updateStyle,
        onFollowClick = viewModel::followArtist
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistListScreen(
    artists: List<Artist> = ARTISTS,
    filterState: ArtistFilterState = ArtistFilterState(),
    popBackStack: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    onRegionChange: (String) -> Unit = {},
    onTypeChange: (String) -> Unit = {},
    onStyleChange: (String) -> Unit = {},
    onFollowClick:(String)->Unit={}
) {

    Scaffold(
        topBar = { CommonTopBar(
            popBackStack = popBackStack,
            navigateToSearch = navigateToSearch,
            midText = "歌手"
        ) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // 歌手列表
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    // 常听歌手/关注歌手 区域
                    TopArtistSection()

                    // 筛选区域
                    FilterSection(
                        selectedRegion = filterState.region,
                        selectedType = filterState.type,
                        selectedStyle = filterState.style,
                        onRegionChange = onRegionChange,
                        onTypeChange = onTypeChange,
                        onStyleChange = onStyleChange
                    )
                }
                items(artists) { artist ->
                    ItemArtist(
                        artist = artist,
                        onClick = onItemClick,
                        onFollowClick=onFollowClick,
                        showFollowStatus = true
                    )
                }
            }
        }
    }
}


