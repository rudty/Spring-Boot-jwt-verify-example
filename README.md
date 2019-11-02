# Spring-Boot-jwt-verify-example
Spring Boot Security Jwt Token Auth (Verify only)

Spring Boot 에서 Security를 활용, Jwt 토큰 인증 처리 (읽기만 구현하였음)


- 토큰의 발급, 추가적인 재 갱신 등의 처리는 여기서 구현하지 않으며 Authorization 헤더에 bearer + jwt 토큰이 포함되었을 때의 인증만 처리
- 토큰 없이 방문, 토큰 만료 처리 구현 [테스트](https://github.com/rudty/Spring-Boot-jwt-verify-example/blob/master/src/test/kotlin/org/rudty/jwtauthapi/controller/HelloControllerTest.kt) 참고

### 응답
|요청|응답 본문|curl|
|-|-|-|
|/|HELLO WORLD|curl "http://localhost:8080"|
|/auth (성공)|HELLO AUTH|curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE5OTMyMDgzMzV9.WGpmMPuq_650CwX8QaTFjV6EgadFP3irdEhKoSaPw5g" "http://localhost:8080/auth"|
|/auth (실패)|FAIL|curl "http://localhost:8080/auth"|
|/auth (실패2)|FAIL|curl -H "Authorization: Bearer 123123" "http://localhost:8080/auth"|
