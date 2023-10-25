package com.sample.demo.seat.repository

import com.sample.demo.seat.domain.entity.Seat
import com.sample.demo.seat.domain.enums.ReserveAvailableType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SeatRepository : JpaRepository<Seat, Long> {

    fun findAllByReserveAvailableType(reserveAvailableType: ReserveAvailableType): List<Seat>
}