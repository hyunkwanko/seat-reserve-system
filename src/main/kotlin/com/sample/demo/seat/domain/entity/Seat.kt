package com.sample.demo.seat.domain.entity

import com.sample.demo.seat.domain.BaseEntity
import com.sample.demo.seat.domain.enums.ReserveAvailableType
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import org.hibernate.annotations.Comment
import javax.persistence.*

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat")
@Entity
class Seat(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("ID")
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Comment("좌석번호")
    @Column(name = "seat_number", length = 10)
    var seatNumber: String,

    @Enumerated(EnumType.STRING)
    @Comment("예약 가능 여부 타입")
    @Column(name = "reserve_available_type")
    var reserveAvailableType: ReserveAvailableType
) : BaseEntity()