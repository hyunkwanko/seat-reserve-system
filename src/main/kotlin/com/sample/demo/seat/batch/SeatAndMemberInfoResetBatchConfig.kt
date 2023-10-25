package com.sample.demo.seat.batch

import com.sample.demo.seat.batch.tasklets.SeatAndMemberInfoResetTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SeatAndMemberInfoResetBatchConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val seatAndMemberInfoResetTasklet: SeatAndMemberInfoResetTasklet
) {

    @Bean
    fun seatAndMemberInfoResetJob(): Job {
        return jobBuilderFactory.get("seatAndMemberInfoResetJob")
            .start(seatAndMemberInfoResetStep()) // Step Setting
            .build()
    }

    @Bean
    fun seatAndMemberInfoResetStep(): Step {
        return stepBuilderFactory.get("seatAndMemberInfoResetStep")
            .tasklet(seatAndMemberInfoResetTasklet) // Tasklet Setting
            .build()
    }
}
