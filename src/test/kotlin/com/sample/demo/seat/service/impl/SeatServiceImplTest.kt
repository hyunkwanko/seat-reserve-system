package com.sample.demo.seat.service.impl

import com.sample.demo.seat.api.controller.member.request.SeatCancelRequest
import com.sample.demo.seat.api.controller.member.request.SeatReserveRequest
import com.sample.demo.seat.domain.entity.Member
import com.sample.demo.seat.domain.entity.Seat
import com.sample.demo.seat.domain.enums.ReserveAvailableType
import com.sample.demo.seat.domain.enums.WorkType
import com.sample.demo.seat.repository.MemberRepository
import com.sample.demo.seat.repository.SeatHistoryRepository
import com.sample.demo.seat.repository.SeatRepository
import com.sample.demo.seat.service.SeatHistoryService
import com.sample.demo.seat.service.SeatService
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
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
internal class SeatServiceImplTest @Autowired constructor(
    private var memberRepository: MemberRepository,
    private var seatRepository: SeatRepository,
    private var seatHistoryRepository: SeatHistoryRepository,
    private var seatHistoryService: SeatHistoryService,
    private var seatService: SeatService
) {

    @AfterEach
    fun tearDown() {
        memberRepository.deleteAllInBatch()
        seatRepository.deleteAllInBatch()
        seatHistoryRepository.deleteAllInBatch()
    }

    @DisplayName("원하는 개수만큼 좌석을 생성한다.")
    @Test
    fun saveSeats() {
        // given
        val seatCount = 100
        seatRepository.saveAll(makeTestSeats(seatCount))

        // when
        val seats = seatRepository.findAll()

        // then
        assertThat(seats).hasSize(seatCount)
    }

    @DisplayName("전 좌석 현황 조회 시 20명씩 페이지네이션 처리한다.")
    @Test
    fun getSeatsByPaging() {
        // given
        val seatCount = 100
        val size = 20
        seatRepository.saveAll(makeTestSeats(seatCount))

        // when
        val seats1 = seatService.getSeats(PageRequest.of(0, size, Sort.by("createdAt").ascending()))
        val seats2 = seatService.getSeats(PageRequest.of(1, size, Sort.by("createdAt").ascending()))

        // then
        assertThat(seats1.get()).hasSize(size)
            .extracting("seatNumber", "reserveAvailableType")
            .contains(
                Assertions.tuple("001", ReserveAvailableType.Y),
                Assertions.tuple("020", ReserveAvailableType.Y)
            )

        assertThat(seats2.get()).hasSize(size)
            .extracting("seatNumber", "reserveAvailableType")
            .contains(
                Assertions.tuple("021", ReserveAvailableType.Y),
                Assertions.tuple("040", ReserveAvailableType.Y)
            )
    }

    @DisplayName("좌석 예약 시, 존재하지 않는 직원인 경우 에러 발생한다.")
    @Test
    fun reserveSeatByNotExistMemberError() {
        // given
        val memberCount = 150
        val seatCount = 100

        memberRepository.saveAll(makeTestMembers(memberCount))
        val seats = seatRepository.saveAll(makeTestSeats(seatCount))

        // when // then
        Assertions.assertThatThrownBy {
            seatService.reserveSeat(
                SeatReserveRequest(
                    memberId = -1,
                    seatId = seats[0].id!!
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("존재하지 않는 직원입니다.")
    }

    @DisplayName("좌석 예약 시, 존재하지 않는 좌석인 경우 에러 발생한다.")
    @Test
    fun reserveSeatByNotExistSeatError() {
        // given
        val memberCount = 150
        val seatCount = 100

        val members = memberRepository.saveAll(makeTestMembers(memberCount))
        seatRepository.saveAll(makeTestSeats(seatCount))

        // when // then
        Assertions.assertThatThrownBy {
            seatService.reserveSeat(
                SeatReserveRequest(
                    memberId = members[0].id!!,
                    seatId = -1
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("없는 좌석 번호입니다.")
    }

    @DisplayName("좌석 예약 시, 이미 좌석 예약 내역이 존재하는 경우 (직원은 1개의 좌석만 예약이 가능) 에러 발생한다.")
    @Test
    fun reserveSeatByAlreadySeatError() {
        // given
        val seatCount = 100

        val member = memberRepository.save(
            Member(
                seatNumber = "001",
                workType = WorkType.OFFICE
            )
        )

        val seats = seatRepository.saveAll(makeTestSeats(seatCount))

        // when // then
        Assertions.assertThatThrownBy {
            seatService.reserveSeat(
                SeatReserveRequest(
                    memberId = member.id!!,
                    seatId = seats[10].id!!
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("직원은 1개의 좌석만 예약이 가능합니다.")
    }

    @DisplayName("동일한 좌석은 하루에 1번만 예약이 가능하다.")
    @Test
    fun reserveSeatBySameSeatError() {
        // given
        val memberCount = 150
        val seatCount = 100

        val members = memberRepository.saveAll(makeTestMembers(memberCount))
        val seats = seatRepository.saveAll(makeTestSeats(seatCount))

        // when
        // 좌석 예약
        seatService.reserveSeat(
            SeatReserveRequest(
                memberId = members[0].id!!,
                seatId = seats[0].id!!
            )
        )

        // 좌석 취소
        seatService.cancelSeat(
            SeatCancelRequest(
                memberId = members[0].id!!,
                seatId = seats[0].id!!
            )
        )

        val seatHistoryByMemberIdAndSeatIdAndCreatedAtIsAfter =
            seatHistoryService.isSeatHistoryByMemberIdAndSeatIdAndCreatedAtIsAfter(members[0].id!!, seats[0].id!!)

        // then
        assertThat(seatHistoryByMemberIdAndSeatIdAndCreatedAtIsAfter).isTrue
    }

    @DisplayName("동일하지 않은 좌석은 예약이 가능이 가능하다.")
    @Test
    fun reserveSeatByNotSameSeat() {
        // given
        val memberCount = 150
        val seatCount = 100

        val members = memberRepository.saveAll(makeTestMembers(memberCount))
        val seats = seatRepository.saveAll(makeTestSeats(seatCount))

        // when
        // 좌석 예약
        // 좌석 예약
        seatService.reserveSeat(
            SeatReserveRequest(
                memberId = members[0].id!!,
                seatId = seats[0].id!!
            )
        )

        // 좌석 취소
        seatService.cancelSeat(
            SeatCancelRequest(
                memberId = members[0].id!!,
                seatId = seats[0].id!!
            )
        )

        val seatHistoryByMemberIdAndSeatIdAndCreatedAtIsAfter =
            seatHistoryService.isSeatHistoryByMemberIdAndSeatIdAndCreatedAtIsAfter(members[0].id!!, seats[1].id!!)

        // then
        assertThat(seatHistoryByMemberIdAndSeatIdAndCreatedAtIsAfter).isFalse
    }

    @DisplayName("좌석이 모두 예약되는 경우, 예약하지 못한 직원은 자동으로 재택근무 형태가 지정됩니다.")
    @Test
    fun reserveSeatByAllSeat() {
        // given
        val memberCount = 3
        val seatCount = 1

        val members = memberRepository.saveAll(makeTestMembers(memberCount))
        val seats = seatRepository.saveAll(makeTestSeats(seatCount))

        // when
        seatService.reserveSeat(
            SeatReserveRequest(
                memberId = members[0].id!!,
                seatId = seats[0].id!!
            )
        )

        val member1Optional = memberRepository.findById(members[1].id!!)
        val member2Optional = memberRepository.findById(members[2].id!!)

        // then
        assertThat(member1Optional.get().workType).isEqualTo(WorkType.REMOTE)
        assertThat(member2Optional.get().workType).isEqualTo(WorkType.REMOTE)
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

    fun makeTestSeats(seatCount: Int): ArrayList<Seat> {
        // Seat Count 만큼 좌석 생성
        val seats = ArrayList<Seat>()
        var i = 1

        while (i <= seatCount) {
            val seat = Seat(
                seatNumber = i.toString().padStart(3, '0'),
                reserveAvailableType = ReserveAvailableType.Y
            )

            seats.add(seat)

            i += 1
        }

        return seats
    }
}