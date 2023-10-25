package com.sample.demo.seat.service

import com.sample.demo.seat.domain.entity.SeatHistory
import com.sample.demo.seat.domain.enums.ReserveType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

interface SeatHistoryService {

    @Transactional(readOnly = true)
    fun getSeatHistoriesByMemberId(memberId: Long, pageable: Pageable): Page<SeatHistory>

    @Transactional
    fun saveSeatHistory(memberId: Long, seatId : Long, seatNumber: String, reserveType: ReserveType)

    @Transactional
    fun isSeatHistoryByMemberIdAndSeatIdAndCreatedAtIsAfter(memberId: Long, seatId : Long): Boolean
}