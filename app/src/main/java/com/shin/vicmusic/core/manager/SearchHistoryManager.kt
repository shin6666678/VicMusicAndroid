package com.shin.vicmusic.core.manager

import com.shin.vicmusic.core.database.dao.SearchHistoryDao
import com.shin.vicmusic.core.database.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryManager @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) {

    val historyFlow: Flow<List<String>> = searchHistoryDao.getHistoryFlow(10)

    suspend fun addSearchTerm(term: String) {
        if (term.isBlank()) return
        searchHistoryDao.insert(SearchHistoryEntity(term = term, timestamp = System.currentTimeMillis()))
    }

    suspend fun clearHistory() {
        searchHistoryDao.clearAll()
    }
}