package com.sample.demo.seat.service

import com.sample.demo.seat.domain.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface MemberService {

    @Transactional(readOnly = true)
    fun getMembers(pageable: Pageable): Page<Member>

    @Transactional(readOnly = true)
    fun getMemberById(memberId: Long): Optional<Member>

    @Transactional
    fun saveMemberSeatInfo(memberId: Long, seatNumber: String?)

    @Transactional
    fun updateAllMemberWorkTypeRemoteByAllSeatReserved()

    @Transactional
    fun updateAllMemberSeatNumberResetByBatch()

    @Transactional
    fun saveTestMembers(memberCount: Int): Boolean
}