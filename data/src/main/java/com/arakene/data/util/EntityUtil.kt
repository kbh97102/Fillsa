package com.arakene.data.util

import com.arakene.data.db.LocalQuoteInfoEntity
import com.arakene.domain.requests.LocalQuoteInfo

fun LocalQuoteInfoEntity.toDomain() = LocalQuoteInfo(
    dailyQuoteSeq,
    korQuote, engQuote, korAuthor, engAuthor, typing, likeYn, memo,
    date, dayOfWeek
)

fun LocalQuoteInfo.toEntity() = LocalQuoteInfoEntity(
    dailyQuoteSeq = dailyQuoteSeq,
    korQuote = korQuote,
    engQuote = engQuote,
    korAuthor = korAuthor,
    engAuthor = engAuthor,
    typing = typing,
    likeYn = likeYn,
    memo = memo
)