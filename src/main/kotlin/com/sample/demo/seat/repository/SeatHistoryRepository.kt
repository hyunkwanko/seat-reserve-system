package com.sample.demo.seat.repository

import com.sample.demo.seat.domain.entity.SeatHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SeatHistoryRepository : JpaRepository<SeatHistory, Long> {

    fun findAllByMemberIdAndSeatIdAndCreatedAtIsAfter(memberId: Long, seatId: Long, createdAt: LocalDateTime): List<SeatHistory>
    fun findAllByMemberId(memberId: Long, pageable: Pageable): Page<SeatHistory>
}