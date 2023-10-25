package com.sample.demo.seat.controller.seat

import com.sample.demo.seat.controller.ApiResponse
import com.sample.demo.seat.api.controller.member.request.SeatCancelRequest
import com.sample.demo.seat.api.controller.member.request.SeatReserveRequest
import com.sample.demo.seat.domain.entity.Seat
import com.sample.demo.seat.service.SeatService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v1/"])
@Tag(name = "Seat Controller", description = "Seat API")
class SeatController(
    private val seatService: SeatService
) {
    @Operation(summary = "테스트 좌석 생성 API", description = "Seat Count 만큼의 테스트 좌석을 생성합니다.")
    @PostMapping("seats")
    fun saveTestSeats(): ApiResponse<Boolean> {
        return ApiResponse.ok(seatService.saveTestSeats(100))
    }

    @Operation(
        summary = "전 좌석 현황 조회 API",
        description = "1. 전 좌석의 현황을 조회합니다. 2. 20명씩 페이지네이션 처리합니다."
    )
    @GetMapping("seats")
    fun getMembers(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int
    ): ApiResponse<Page<Seat>> {
        return ApiResponse.ok(seatService.getSeats(PageRequest.of(page, size, Sort.by("createdAt").ascending())))
    }

    @Operation(summary = "좌석 예약 API", description = "좌석 예약합니다.")
    @PostMapping("seat")
    fun reserveSeat(@RequestBody seatReserveRequest: SeatReserveRequest): ApiResponse<Boolean> {
        return ApiResponse.ok(seatService.reserveSeat(seatReserveRequest))
    }

    @Operation(summary = "좌석 예약 취소 API", description = "좌석 취소합니다.")
    @PatchMapping("seat")
    fun cancelSeat(@RequestBody seatCancelRequest: SeatCancelRequest): ApiResponse<Boolean> {
        return ApiResponse.ok(seatService.cancelSeat(seatCancelRequest))
    }
}