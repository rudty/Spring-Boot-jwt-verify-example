package org.rudty.jwtauthapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter(){
    companion object val SECERET_KEY = "secret"

    /**
     * 한 클래스에 AuthenticationEntryPoint AccessDeniedHandler 를 implements 하였지만
     * 예제이기 때문에 간단히 구현하였고
     *
     * 실제로 구현할때는 분리해서 만들도록 합시다.
     */
    class AuthenticationFailure: AccessDeniedHandler, AuthenticationEntryPoint, Customizer<ExceptionHandlingConfigurer<HttpSecurity>> {
        override fun customize(t: ExceptionHandlingConfigurer<HttpSecurity>) {
            t.authenticationEntryPoint(this)
            t.accessDeniedHandler(this)
        }

        override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException) {
            /**
             * 권한이 없을때
             * Jwt 토큰 없이 접속을 시도할때
             */
            request.getRequestDispatcher("/fail").forward(request, response)
        }

        override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
            /**
             * 인증에 실패했을때
             * 올바르지 않은 Jwt 토큰, 유효기간 만료 토큰 등
             */
            request.getRequestDispatcher("/fail").forward(request, response)
        }
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .antMatchers("/auth/**")
                .authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .exceptionHandling(authenticationFailure())
    }

    @Bean
    fun authenticationFailure() = AuthenticationFailure()

    /**
     * 기본적인 JWT 인증 동작만을 위해서라면
     * @EnableResourceServer 를 Config 에 설정하는 것으로 사용이 가능합니다.
     * 그러나 jwt 인증 실패, 권한 문제 시 동작을 커스텀하기 어려우므로
     * 직접 filter 클래스의 Bean 을 만들어서 사용합니다
     */
    @Bean
    fun jwtAuthenticationProcessingFilter(): OAuth2AuthenticationProcessingFilter {
        val filter = OAuth2AuthenticationProcessingFilter()
        val oAuth2AuthenticationManager = oAuth2AuthenticationManager()
        filter.setAuthenticationManager(oAuth2AuthenticationManager)
        filter.setAuthenticationEntryPoint(authenticationFailure())
        return filter
    }

    @Bean
    fun oAuth2AuthenticationManager(): OAuth2AuthenticationManager {
        val oAuth2AuthenticationManager = OAuth2AuthenticationManager()
        oAuth2AuthenticationManager.setTokenServices(tokenService())
        return oAuth2AuthenticationManager
    }

    @Bean
    @Primary
    fun tokenService(): DefaultTokenServices {
        val defaultTokenServices = DefaultTokenServices()
        defaultTokenServices.setTokenStore(tokenStore())
        return defaultTokenServices
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JwtTokenStore(jwtAccessTokenConverter())
    }

    @Bean
    fun jwtAccessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.setSigningKey(SECERET_KEY)
        return converter
    }
}