package com.sample.demo.seat.controller.seatHistory

import com.sample.demo.seat.controller.ApiResponse
import com.sample.demo.seat.domain.entity.SeatHistory
import com.sample.demo.seat.service.SeatHistoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v1/"])
@Tag(name = "Seat History Controller", description = "Seat History API")
class SeatHistoryController(
    private val seatHistoryService: SeatHistoryService
) {

    @Operation(summary = "직원 좌석 예약 히스토리 정보 조회 API", description = "직원 좌석 예약 히스토리 정보 조회합니다.")
    @GetMapping("{memberId}/seatHistories")
    fun getSeatHistoriesByMemberId(
        @PathVariable("memberId") memberId: Long,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int
    ): ApiResponse<Page<SeatHistory>> {
        return ApiResponse.ok(seatHistoryService.getSeatHistoriesByMemberId(memberId, PageRequest.of(page, size, Sort.by("createdAt").ascending())))
    }
}