package com.shin.vicmusic.core.network.di
import com.shin.vicmusic.feature.auth.AuthViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthViewModelEntryPoint {
    fun getAuthViewModel(): AuthViewModel
}