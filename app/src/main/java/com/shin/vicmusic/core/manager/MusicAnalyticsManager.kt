package com.shin.vicmusic.core.manager

import com.shin.vicmusic.core.data.repository.SongRepository
import com.shin.vicmusic.core.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicAnalyticsManager @Inject constructor(
    private val songRepository: SongRepository,
    private val userRepository: UserRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var reportPlayJob: Job? = null
    private var reportTimeJob: Job? = null

    fun startTrackPlayReport(songId: String) {
        reportPlayJob?.cancel()
        reportPlayJob = scope.launch {
            delay(10000)
            try {
                songRepository.playSong(songId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startDurationReport() {
        if (reportTimeJob?.isActive == true) return
        reportTimeJob = scope.launch {
            while (isActive) {
                delay(60000)
                try {
                    userRepository.reportDuration(60)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun stopDurationReport() {
        reportTimeJob?.cancel()
        reportTimeJob = null
    }

    fun cancelAll() {
        reportPlayJob?.cancel()
        reportTimeJob?.cancel()
    }
}