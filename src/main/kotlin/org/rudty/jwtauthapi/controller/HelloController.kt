package org.rudty.jwtauthapi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class HelloController {
    /**
     * 미인증 시 입장가능
     */
    @GetMapping("/")
    fun root() = "HELLO WORLD"

    /**
     * 인증 시 입장 가능
     */
    @GetMapping("/auth")
    fun auth() = "HELLO AUTH"

    /**
     * 아무튼 실패
     */
    @GetMapping("/fail")
    fun fail() = "FAIL"
}