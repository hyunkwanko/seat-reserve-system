package com.sample.demo.seat.service.impl

import com.sample.demo.seat.domain.entity.SeatHistory
import com.sample.demo.seat.domain.enums.ReserveType
import com.sample.demo.seat.repository.SeatHistoryRepository
import com.sample.demo.seat.service.SeatHistoryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SeatHistoryServiceImpl(
    private val seatHistoryRepository: SeatHistoryRepository
) : SeatHistoryService {

    @Transactional(readOnly = true)
    override fun getSeatHistoriesByMemberId(memberId: Long, pageable: Pageable): Page<SeatHistory> {
        return seatHistoryRepository.findAllByMemberId(memberId, pageable)
    }

    @Transactional
    override fun saveSeatHistory(memberId: Long, seatId: Long, seatNumber: String, reserveType: ReserveType) {
        seatHistoryRepository.save(
            SeatHistory(
                memberId = memberId,
                seatId = seatId,
                seatNumber = seatNumber,
                reserveType = reserveType
            )
        )
    }

    @Transactional
    override fun isSeatHistoryByMemberIdAndSeatIdAndCreatedAtIsAfter(memberId: Long, seatId: Long): Boolean {
        // 당일 오전 12시
        return seatHistoryRepository
            .findAllByMemberIdAndSeatIdAndCreatedAtIsAfter(memberId, seatId, LocalDate.now().atStartOfDay())
            .isNotEmpty()
    }
}