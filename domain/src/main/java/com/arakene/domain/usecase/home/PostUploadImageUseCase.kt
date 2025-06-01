package com.arakene.domain.usecase.home

import android.net.Uri
import com.arakene.domain.repository.HomeRepository
import java.io.File
import javax.inject.Inject

class PostUploadImageUseCase @Inject constructor(
    private val repository: HomeRepository
) {

    suspend operator fun invoke(dailyQuoteSeq: Int, imageFile: File) = repository.postUploadImage(dailyQuoteSeq = dailyQuoteSeq, imageFile = imageFile)

}