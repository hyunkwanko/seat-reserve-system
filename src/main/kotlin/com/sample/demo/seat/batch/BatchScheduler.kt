package com.sample.demo.seat.batch

import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BatchScheduler(
    val jobLauncher: JobLauncher,
    val seatAndMemberInfoResetBatchConfig: SeatAndMemberInfoResetBatchConfig
) {

    // 초: 0 ~ 59 / 분: 0 ~ 59 / 시: 0 ~ 23 / 일: 1 ~ 31 / 월: 1 ~ 12 / 요일: 1 ~ 7 (Sun ~ Sat) / 년 (생략 가능)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 자정(0시) 좌석 정보 초기화
    fun seatAndMemberInfoResetJob() {
        // job parameter 설정
        val jobParameters = JobParametersBuilder()
            .addString("datetime", LocalDateTime.now().toString())
            .toJobParameters()

        jobLauncher.run(seatAndMemberInfoResetBatchConfig.seatAndMemberInfoResetJob(), jobParameters)
    }
}