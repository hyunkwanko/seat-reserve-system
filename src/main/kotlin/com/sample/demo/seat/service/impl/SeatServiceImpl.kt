package com.sample.demo.seat.service.impl

import com.sample.demo.seat.api.controller.member.request.SeatCancelRequest
import com.sample.demo.seat.api.controller.member.request.SeatReserveRequest
import com.sample.demo.seat.domain.entity.Seat
import com.sample.demo.seat.domain.enums.ReserveAvailableType
import com.sample.demo.seat.domain.enums.ReserveType
import com.sample.demo.seat.repository.SeatRepository
import com.sample.demo.seat.service.MemberService
import com.sample.demo.seat.service.SeatHistoryService
import com.sample.demo.seat.service.SeatService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.LockModeType

@Service
class SeatServiceImpl(
    private val seatRepository: SeatRepository,
    private val seatHistoryService: SeatHistoryService,
    private val memberService: MemberService
) : SeatService {

    @Transactional(readOnly = true)
    override fun getSeats(pageable: Pageable): Page<Seat> {
        return seatRepository.findAll(pageable)
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 동시성 문제
    override fun reserveSeat(seatReserveRequest: SeatReserveRequest): Boolean {
        val seat = getSeatById(seatReserveRequest.seatId)
        val member = memberService.getMemberById(seatReserveRequest.memberId)

        // 존재하지 않는 직원인 경우
        if (member.isEmpty) {
            throw IllegalArgumentException("존재하지 않는 직원입니다.")
        }

        // 존재하지 않는 좌석인 경우
        if (seat.isEmpty) {
            throw IllegalArgumentException("없는 좌석 번호입니다.")
        }

        // 이미 좌석 예약 내역이 존재하는 경우 (직원은 1개의 좌석만 예약이 가능)
        if (member.get().seatNumber != null) {
            throw IllegalArgumentException("직원은 1개의 좌석만 예약이 가능합니다.")
        }

        // 이미 예약된 좌석인 경우 (좌석은 1명의 직원만 사용 가능)
        if (ReserveAvailableType.Y != seat.get().reserveAvailableType) {
            throw IllegalArgumentException("좌석이 이미 예약되었습니다.")
        }

        // 동일한 좌석은 하루에 1번만 예약이 가능
        if (seatHistoryService.isSeatHistoryByMemberIdAndSeatIdAndCreatedAtIsAfter(
                seatReserveRequest.memberId, seatReserveRequest.seatId
            )
        ) {
            throw IllegalArgumentException("동일한 좌석은 하루에 1번만 예약이 가능합니다.")
        }

        // 좌석 예약
        seat.get().reserveAvailableType = ReserveAvailableType.N

        // 직원 좌석 정보 저장
        memberService.saveMemberSeatInfo(seatReserveRequest.memberId, seat.get().seatNumber)

        // 좌석 히스토리 저장
        seatHistoryService.saveSeatHistory(
            memberId = seatReserveRequest.memberId,
            seatId = seatReserveRequest.seatId,
            seatNumber = seat.get().seatNumber,
            reserveType = ReserveType.RESERVATION
        )

        // 좌석이 모두 예약되는 경우, 예약하지 못한 직원은 자동으로 재택근무 형태가 지정
        if (seatRepository.findAllByReserveAvailableType(ReserveAvailableType.Y).isEmpty()) {
            memberService.updateAllMemberWorkTypeRemoteByAllSeatReserved()
        }

        return true
    }

    @Transactional
    override fun cancelSeat(seatCancelRequest: SeatCancelRequest): Boolean {
        val seat = seatRepository.findById(seatCancelRequest.seatId)
        val member = memberService.getMemberById(seatCancelRequest.memberId)

        // 존재하지 않는 직원인 경우
        if (member.isEmpty) {
            throw IllegalArgumentException("존재하지 않는 직원입니다.")
        }

        // 존재하지 않는 좌석인 경우
        if (seat.isEmpty) {
            throw IllegalArgumentException("존재하지 않는 좌석 번호입니다.")
        }

        // 본인의 예약 내역이 아닌 경우
        if (member.get().seatNumber != seat.get().seatNumber) {
            throw IllegalArgumentException("본인의 예약 내역만 취소 가능합니다.")
        }

        // 예약이 가능한 좌석인 경우
        if (seat.get().reserveAvailableType == ReserveAvailableType.Y) {
            throw IllegalArgumentException("예약 가능한 좌석입니다.")
        }

        // 좌석 예약 취소
        seat.get().reserveAvailableType = ReserveAvailableType.Y

        // 직원 좌석 취소 정보 저장
        memberService.saveMemberSeatInfo(seatCancelRequest.memberId, null)

        // 좌석 히스토리 저장
        seatHistoryService.saveSeatHistory(
            memberId = seatCancelRequest.memberId,
            seatId = seatCancelRequest.seatId,
            seatNumber = seat.get().seatNumber,
            reserveType = ReserveType.CANCEL
        )

        return true
    }

    @Transactional
    override fun updateAllSeatReserveAvailableTypeResetByBatch() {
        // 매일 자정(0시) 모든 좌석을 예약 가능으로 변경
        seatRepository.findAll().forEach { seat: Seat? ->
            if (seat != null) {
                seat.reserveAvailableType = ReserveAvailableType.Y
            }
        }
    }

    @Transactional
    override fun saveTestSeats(seatCount: Int): Boolean {
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

        seatRepository.saveAll(seats)
        return true
    }

    @Transactional(readOnly = true)
    override fun getSeatById(seatId: Long): Optional<Seat> {
        return seatRepository.findById(seatId)
    }
}