package com.arakene.data.util

import com.arakene.data.db.LocalQuoteInfoEntity
import com.arakene.domain.requests.LocalQuoteInfo

fun LocalQuoteInfoEntity.toDomain() = LocalQuoteInfo(
    id,
    dailyQuoteSeq,
    korQuote, engQuote, korAuthor, engAuthor, typing, likeYn, memo
)

fun LocalQuoteInfo.toEntity() = LocalQuoteInfoEntity(
    id, dailyQuoteSeq, korQuote, engQuote, korAuthor, engAuthor, typing, likeYn, memo
)