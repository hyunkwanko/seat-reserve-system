package com.sample.demo.seat.controller.member

import com.sample.demo.seat.controller.ApiResponse
import com.sample.demo.seat.domain.entity.Member
import com.sample.demo.seat.service.MemberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v1/"])
@Tag(name = "Member Controller", description = "Member API")
class MemberController(
    private val memberService: MemberService
) {
    @Operation(summary = "테스트 직원 생성 API", description = "People Count 만큼의 테스트 직원을 생성합니다.")
    @PostMapping("members")
    fun saveTestMembers(): ApiResponse<Boolean> {
        return ApiResponse.ok(memberService.saveTestMembers(150))
    }

    @Operation(
        summary = "전 직원 근무형태 현황 조회 API",
        description = "1. 모든 직원의 근무형태를 조회합니다. 2. 오피스 출근 직원의 경우 좌석번호를 함께 제공합니다. 3. 20명씩 페이지네이션 처리합니다."
    )
    @GetMapping("members")
    fun getMembers(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int
    ): ApiResponse<Page<Member>> {
        return ApiResponse.ok(memberService.getMembers(PageRequest.of(page, size, Sort.by("createdAt").ascending())))
    }
}