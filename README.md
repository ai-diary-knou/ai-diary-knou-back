# Backend

## MVP Core Skills
- AI Text Mining / Image Creation(차후)
    - Open AI API를 활용하여 일기 문단 분석
    - prompt1 : emotion, self-thought, core value, todo
    - prompt2 : Find a poetic literacy sentence from books or generate one related to the situation in korean
    - AI Image 생성을 통해 하루 일기에 맞는 이미지 자동 생성(차후)
- 암호화
    - AES+RSA를 통해 토큰 정보 및 일기 문단 암복호화
    - SHA를 통해 비밀번호 단방향 암호화
- Spring Cloud (MSA)
    - Spring Cloud의 Discovery, Gateway 기능 사용.
    - Eureka를 통해 MSA 모듈 관리
- Database
    - Azure Mysql Database
- Devops & ETC
    - AWS EC2/Load balancer/SSL Certificate
    - Gabia Domain
    - Docker/Jenkins

### Restful API
> 1. URI에 정보의 자원을 복수형 명사로 표시 ex. /users
> 2. 행위 표시는 GET/POST/PUT/PATCH/DELETE를 적절히 사용
> 3. 계층 관계일 때 /를 사용 
> 4. 긴 경로일 때 -을 사용 ex. /users/daily-statistics
> 5. 파일 확장자 미포함
> 6. 컨트롤 자원일 때 동사를 허용 ex. /diaries/duplicate
> 7. 상세 정보의 경우 ex. /users/{userId}
> 8. 소유 관계의 경우 ex. /users/{userId}/info
> 9. 연관 관계의 경우 예외적으로 - 사용 가능

### Response Json Format
Success Example
```json
{
  "status": "SUCCESS",
  "data": {
    "id" : 1,
    "email" : "test@gmail.com",
    "phoneNumber" : "01012345678",
    "nickName" : "TESTER"
  }
}
```
Error Example
```json
{
  "status": "FAIL", // FAIL(custom-exception), ERROR(system-error 500)
  "code" : "INVALID_PARAMETER",
  "message": "invalid email form - regex : ~~"
}
```

## Commit Convention
- feat : 새로운 기능 추가
- fix : 버그 수정
- docs : 문서 수정
- style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
- refactor : 코드 리펙토링
- test : 테스트 코드, 리펙토링 테스트 코드 추가
- chore : 빌드 업무 수정, 패키지 매니저 수정

## Branch Commit Rule
```shell
- main으로 rebase
- develop으로 squash 
- feature 머지 후 삭제 
```
- main
- develop
- feature 

## CI/CD Process
```shell
-- Local : H2
-- Develop : 
[1] Azure Instance 
[2] AWS RDS 
```

## API
회원
- 회원 가입
    - 이메일 인증
    - 내 정보 등록
- 로그인
- 비밀번호 찾기
- 내 정보 수정

다이어리
- 일기 목록 조회
- 일기 상세 조회
- 일기 작성/수정
- AI 분석 결과 확인