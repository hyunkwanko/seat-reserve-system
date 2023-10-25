package com.sample.demo.seat.service

import com.sample.demo.seat.api.controller.member.request.SeatCancelRequest
import com.sample.demo.seat.api.controller.member.request.SeatReserveRequest
import com.sample.demo.seat.controller.ApiResponse
import com.sample.demo.seat.domain.entity.Seat
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Lock
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.LockModeType

interface SeatService {

    @Transactional(readOnly = true)
    fun getSeats(pageable: Pageable): Page<Seat>

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 동시성 문제
    fun reserveSeat(seatReserveRequest: SeatReserveRequest): Boolean

    @Transactional
    fun cancelSeat(seatCancelRequest: SeatCancelRequest): Boolean

    @Transactional
    fun updateAllSeatReserveAvailableTypeResetByBatch()

    @Transactional
    fun saveTestSeats(seatCount: Int): Boolean

    @Transactional(readOnly = true)
    fun getSeatById(seatId: Long): Optional<Seat>
}