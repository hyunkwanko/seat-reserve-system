package com.sample.demo.seat.batch.tasklets

import com.sample.demo.seat.service.MemberService
import com.sample.demo.seat.service.SeatService
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SeatAndMemberInfoResetTasklet(
    private val seatService: SeatService,
    private val memberService: MemberService
) : Tasklet {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        seatService.updateAllSeatReserveAvailableTypeResetByBatch()
        memberService.updateAllMemberSeatNumberResetByBatch()

        log.info("매일 자정(0시) 좌석 정보 초기화 -> 일시: {}", LocalDateTime.now())

        return RepeatStatus.FINISHED
    }
}