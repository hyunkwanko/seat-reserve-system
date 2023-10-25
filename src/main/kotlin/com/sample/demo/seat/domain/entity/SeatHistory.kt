package com.sample.demo.seat.domain.entity

import com.sample.demo.seat.domain.BaseEntity
import com.sample.demo.seat.domain.enums.ReserveType
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import org.hibernate.annotations.Comment
import javax.persistence.*

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat_history")
@Entity
class SeatHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("ID")
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Comment("직원 ID")
    @Column(name = "member_id")
    val memberId: Long,

    @Comment("좌석 ID")
    @Column(name = "seat_id")
    val seatId: Long?,

    @Comment("좌석번호")
    @Column(name = "seat_number", length = 10)
    val seatNumber: String,

    @Enumerated(EnumType.STRING)
    @Comment("예약 타입")
    @Column(name = "reserve_type", length = 30)
    val reserveType: ReserveType
) : BaseEntity()