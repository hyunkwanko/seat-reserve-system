package com.sample.demo.seat.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    fun runtimeException(ex: IllegalArgumentException): Any {
        return ApiResponse.of(
            HttpStatus.BAD_REQUEST,
            ex.message.toString(),
            null
        )
    }
}