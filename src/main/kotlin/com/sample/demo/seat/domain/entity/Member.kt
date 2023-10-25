package com.sample.demo.seat.domain.entity

import com.sample.demo.seat.domain.BaseEntity
import com.sample.demo.seat.domain.enums.WorkType
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import org.hibernate.annotations.Comment
import javax.persistence.*

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("ID")
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Comment("좌석번호")
    @Column(name = "seat_number", length = 10)
    var seatNumber: String?,

    @Enumerated(EnumType.STRING)
    @Comment("근무형태 타입")
    @Column(name = "work_type", length = 30)
    var workType: WorkType
) : BaseEntity()