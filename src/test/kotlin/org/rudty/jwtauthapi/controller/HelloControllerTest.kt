package org.rudty.jwtauthapi.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@AutoConfigureMockMvc
@SpringBootTest
class HelloControllerTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun 모든권한_루트에_요청하기() {
        mvc.perform(MockMvcRequestBuilders
                .get("/")
                .accept(MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun 권한없이_AUTH에_요청하기() {
        mvc.perform(MockMvcRequestBuilders
                .get("/auth")
                .accept(MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.forwardedUrl("/fail"))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun 권한으로_AUTH에_요청하기() {
        mvc.perform(MockMvcRequestBuilders
                .get("/auth")
                .header("Authorization", "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE5OTMyMDgzMzV9.WGpmMPuq_650CwX8QaTFjV6EgadFP3irdEhKoSaPw5g")
                .accept(MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("HELLO AUTH"))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun 만료된_토큰_AUTH에_요청하기() {
        mvc.perform(MockMvcRequestBuilders
                .get("/auth")
                .header("Authorization", "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjExOTMyMDgzMzV9.5U4B8u8xfgOxOwXB_gSMZLoM9GBN3oVnQsjrkLj7fTo")
                .accept(MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.forwardedUrl("/fail"))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun 올바르지_않은_토큰_AUTH에_요청하기() {
        mvc.perform(MockMvcRequestBuilders
                .get("/auth")
                .header("Authorization", "bearer hello_world")
                .accept(MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.forwardedUrl("/fail"))
                .andDo(MockMvcResultHandlers.print())
    }
}