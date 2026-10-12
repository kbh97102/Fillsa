package com.arakene.domain.repository

interface InstallationIdProvider {
    suspend fun getId(): String
}
