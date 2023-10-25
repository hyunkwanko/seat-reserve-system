package com.sample.demo.seat.controller

import org.springframework.http.HttpStatus

class ApiResponse<T>(status: HttpStatus, message: String, data: T) {
    var code: Int
    var status: HttpStatus
    var message: String
    var data: T

    init {
        this.code = status.value()
        this.status = status
        this.message = message
        this.data = data
    }

    companion object {
        fun <T> of(httpStatus: HttpStatus, message: String, data: T): ApiResponse<T> {
            return ApiResponse(httpStatus, message, data)
        }

        fun <T> of(httpStatus: HttpStatus, data: T): ApiResponse<T> {
            return of(httpStatus, httpStatus.name, data)
        }

        fun <T> ok(data: T): ApiResponse<T> {
            return of(HttpStatus.OK, HttpStatus.OK.name, data)
        }
    }
}