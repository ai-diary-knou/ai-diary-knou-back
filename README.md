# Backend

## MVP Core Skills
- AI Text Mining / Image Creation
    - Open AI API를 활용하여 일기 문단에서 텍스트 마이닝
    - prompt1 : emotion, self-thought, core value, todo
    - prompt2 : Find a poetic literacy sentence from books or generate one related to the situation in korean
    - AI Image 생성을 통해 하루 일기에 맞는 이미지 자동 생성
- Spring Cloud (MSA)
    - Spring Cloud의 Discovery, Gateway 기능 사용.
    - Eureka를 통해 MSA 모듈 관리
- Database
    - H2 
    - AWS RDS (mysql or postgre)
- Devops
    - EC2 / Docker
    - RDS
    - Jenkins

## Convention
### Domain Driven Design Structure
```
ㄴ presentation 
  - UserController
ㄴ application
  ㄴ dto
  ㄴ service
ㄴ domain
  ㄴ entity
  ㄴ repository
    - UserRepository
ㄴ infrastructure
  ㄴ config
  ㄴ persistence
    - UserRepositoryImpl
  ㄴ transport
``` 
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
  "code": 200,
  "message": "",
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
  "code": 400, // 기본 Http error code 외 커스텀 code 사용 가능하도록
  "message": "Invalid Request Parameter",
  "status": "INVALID_PARAMETER",
  "errors": [ // error 메세지가 여러개인 경우
    {
      "domain": "example.com",
      "status": "INVALID_USER_NAME",
      "message": "Invalid Name Value : '_asdf12'. Allowed Regex:[a-zA-Z]"
    },
    {
      "domain": "example.com",
      "status": "INVALID_PHONE_NUMBER",
      "message": "Invalid Phone Number Value : '#$%'. Allowed Regex:[]"
    }
  ]
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

## CI/CD Process

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