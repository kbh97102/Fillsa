package com.arakene.fillsa

import com.arakene.domain.repository.InstallationIdProvider
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseInstallationIdProvider : InstallationIdProvider {
    override suspend fun getId(): String = suspendCancellableCoroutine { continuation ->
        FirebaseInstallations.getInstance().id
            .addOnSuccessListener { continuation.resume(it) }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }
}
