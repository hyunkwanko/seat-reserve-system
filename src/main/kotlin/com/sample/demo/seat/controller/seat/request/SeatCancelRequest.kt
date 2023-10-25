package com.sample.demo.seat.api.controller.member.request

data class SeatCancelRequest(
    val memberId: Long,
    val seatId: Long
)