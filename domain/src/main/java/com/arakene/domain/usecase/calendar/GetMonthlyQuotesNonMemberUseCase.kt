package com.arakene.domain.usecase.calendar

import com.arakene.domain.repository.CalendarRepository
import javax.inject.Inject

class GetMonthlyQuotesNonMemberUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    suspend operator fun invoke(yearMonth: String) =
        calendarRepository.getQuotesMonthlyNonMember(yearMonth)

}