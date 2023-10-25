package com.sample.demo.seat.api.controller.member.request

data class SeatReserveRequest(
    val memberId: Long,
    val seatId: Long
)