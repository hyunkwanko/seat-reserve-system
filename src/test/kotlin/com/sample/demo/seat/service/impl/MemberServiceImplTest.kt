package com.sample.demo.seat.service.impl

import com.sample.demo.seat.domain.entity.Member
import com.sample.demo.seat.domain.enums.WorkType
import com.sample.demo.seat.repository.MemberRepository
import com.sample.demo.seat.repository.SeatHistoryRepository
import com.sample.demo.seat.repository.SeatRepository
import com.sample.demo.seat.service.MemberService
import com.sample.demo.seat.service.SeatHistoryService
import com.sample.demo.seat.service.SeatService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
internal class MemberServiceImplTest @Autowired constructor(
    private var memberRepository: MemberRepository,
    private var seatRepository: SeatRepository,
    private var seatHistoryRepository: SeatHistoryRepository,
    private var memberService: MemberService
) {

    @AfterEach
    fun tearDown() {
        seatRepository.deleteAllInBatch()
        memberRepository.deleteAllInBatch()
        seatHistoryRepository.deleteAllInBatch()
    }

    @DisplayName("원하는 수만큼 직원을 생성한다.")
    @Test
    fun saveMembers() {
        // given
        val memberCount = 150
        memberRepository.saveAll(makeTestMembers(memberCount))

        // when
        val members = memberRepository.findAll()

        // then
        Assertions.assertThat(members).hasSize(memberCount)
    }

    @DisplayName("전 직원 근무형태 현황 조회 시 20명씩 페이지네이션 처리한다.")
    @Test
    fun getMembersByPaging() {
        // given
        val memberCount = 150
        val size = 20
        memberRepository.saveAll(makeTestMembers(memberCount))

        // when
        val members1 = memberService.getMembers(PageRequest.of(0, size, Sort.by("createdAt").ascending()))
        val members2 = memberService.getMembers(PageRequest.of(1, size, Sort.by("createdAt").ascending()))

        // then
        Assertions.assertThat(members1.get()).hasSize(size)
            .extracting("id")
            .contains(
                1L,
                20L
            )

        Assertions.assertThat(members2.get()).hasSize(size)
            .extracting("id")
            .contains(
                21L,
                40L
            )
    }

    fun makeTestMembers(memberCount: Int): ArrayList<Member> {
        // Member Count 만큼 좌석 생성
        val members = java.util.ArrayList<Member>()

        var i = 1
        while (i <= memberCount) {
            val member = Member(
                seatNumber = null,
                workType = WorkType.values().toList().shuffled().first()
            )

            members.add(member)

            i += 1
        }

        return members
    }
}