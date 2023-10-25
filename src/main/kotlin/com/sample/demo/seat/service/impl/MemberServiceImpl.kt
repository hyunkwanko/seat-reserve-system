package com.sample.demo.seat.service.impl

import com.sample.demo.seat.domain.entity.Member
import com.sample.demo.seat.domain.enums.WorkType
import com.sample.demo.seat.repository.MemberRepository
import com.sample.demo.seat.service.MemberService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MemberServiceImpl(
    private val memberRepository: MemberRepository
) : MemberService {

    @Transactional(readOnly = true)
    override fun getMembers(pageable: Pageable): Page<Member> {
        return memberRepository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    override fun getMemberById(memberId: Long): Optional<Member> {
        return memberRepository.findById(memberId)
    }

    @Transactional
    override fun saveMemberSeatInfo(memberId: Long, seatNumber: String?) {
        val member = memberRepository.findById(memberId)

        if (member.isEmpty) {
            throw IllegalArgumentException("존재하지 않는 직원입니다.")
        }

        member.get().seatNumber = seatNumber

        if (seatNumber == null) {
            member.get().workType = WorkType.REMOTE
        } else {
            member.get().workType = WorkType.OFFICE
        }
    }

    @Transactional
    override fun updateAllMemberWorkTypeRemoteByAllSeatReserved() {
        memberRepository.findAll().forEach { member: Member? ->
            if (member != null) {
                if (member.seatNumber == null) {
                    member.workType = WorkType.REMOTE
                }
            }
        }
    }

    @Transactional
    override fun updateAllMemberSeatNumberResetByBatch() {
        // 매일 자정(0시) 모든 직원들의 좌석 초기화
        memberRepository.findAll().forEach { member: Member? ->
            if (member != null) {
                member.seatNumber = null
            }
        }
    }

    @Transactional
    override fun saveTestMembers(memberCount: Int): Boolean {
        val members = ArrayList<Member>()

        var i = 1
        while (i <= memberCount) {
            val member = Member(
                seatNumber = null,
                workType = WorkType.values().toList().shuffled().first()
            )

            members.add(member)

            i += 1
        }

        memberRepository.saveAll(members)
        return true
    }
}