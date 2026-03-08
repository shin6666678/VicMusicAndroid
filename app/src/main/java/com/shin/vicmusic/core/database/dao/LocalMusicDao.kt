package com.shin.vicmusic.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shin.vicmusic.core.database.entity.LocalMusicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalMusicDao {
    @Query("SELECT * FROM local_music ORDER BY title ASC")
    fun observeAllMusic(): Flow<List<LocalMusicEntity>>

    @Query("SELECT * FROM local_music ORDER BY title ASC")
    suspend fun getAllMusic(): List<LocalMusicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusic(musicList: List<LocalMusicEntity>)

    @Query("DELETE FROM local_music")
    suspend fun clearAll()
}
