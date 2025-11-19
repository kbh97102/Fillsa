package com.arakene.data.util

import com.arakene.data.db.LocalQuoteInfoEntity
import com.arakene.data.db.StreakInfoEntity
import com.arakene.data.db.WidgetQuoteInfoEntity
import com.arakene.domain.model.StreakInfo
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.util.YN
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalQuoteInfoEntity.toDomain() = LocalQuoteInfo(
    dailyQuoteSeq,
    korQuote, engQuote, korAuthor, engAuthor, korTyping, engTyping, likeYn, memo,
    date, dayOfWeek
)

fun LocalQuoteInfo.toEntity() = LocalQuoteInfoEntity(
    dailyQuoteSeq = dailyQuoteSeq,
    korQuote = korQuote,
    engQuote = engQuote,
    korAuthor = korAuthor,
    engAuthor = engAuthor,
    korTyping = korTyping,
    engTyping = engTyping,
    likeYn = likeYn,
    memo = memo,
    dayOfWeek = dayOfWeek,
    date = date
)

fun DailyQuoteDto.toEntity(date: String): WidgetQuoteInfoEntity {
    return WidgetQuoteInfoEntity(
        dailyQuoteSeq = this.dailyQuoteSeq,
        likeYn = this.likeYn,
        imagePath = this.imagePath,
        korQuote = this.korQuote,
        engQuote = this.engQuote,
        korAuthor = this.korAuthor,
        engAuthor = this.engAuthor,
        authorUrl = this.authorUrl,
        date = date
    )
}

fun WidgetQuoteInfoEntity.toDto(): DailyQuoteDto {
    return DailyQuoteDto(
        likeYn = this.likeYn,
        imagePath = this.imagePath,
        dailyQuoteSeq = this.dailyQuoteSeq,
        korQuote = this.korQuote,
        engQuote = this.engQuote,
        korAuthor = this.korAuthor,
        engAuthor = this.engAuthor,
        authorUrl = this.authorUrl
    ).apply {
        quoteDate = this@toDto.date
    }
}


fun DailyQuotaNoToken.toWidgetQuoteInfoEntity(date: String): WidgetQuoteInfoEntity {
    return WidgetQuoteInfoEntity(
        dailyQuoteSeq = this.dailyQuoteSeq,
        likeYn = YN.N.name,
        imagePath = "",
        korQuote = this.korQuote,
        engQuote = this.engQuote,
        korAuthor = this.korAuthor,
        engAuthor = this.engAuthor,
        authorUrl = this.authorUrl,
        date = date
    )
}


fun WidgetQuoteInfoEntity.toDailyQuotaNoToken(): DailyQuotaNoToken {
    return DailyQuotaNoToken(
        dailyQuoteSeq = this.dailyQuoteSeq,
        korQuote = this.korQuote,
        engQuote = this.engQuote,
        korAuthor = this.korAuthor,
        engAuthor = this.engAuthor,
        authorUrl = this.authorUrl
    )
}

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
fun StreakInfo.toEntity() = StreakInfoEntity(
    date = dateFormatter.format(date),
    streakDateCount = streakDateCount,
    isDailyWritingCompleted = isDailyWritingCompleted
)

fun StreakInfoEntity.toDto() = StreakInfo(
    date = LocalDate.parse(date),
    streakDateCount = streakDateCount,
    isDailyWritingCompleted = isDailyWritingCompleted
)