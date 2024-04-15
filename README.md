# Websocket 활용 Chatservice 구현

## 목표 구현 기능

- 실시간 메세지 전송
- 다수의 채팅방 기능
- 사용자 연결 상태 확인

## 기술 스택

- Springboot
- **Websocket**
- Gradle
- MongoDB

---

## 1. 기초 설정

### 프로젝트 설정 및 초기화
1. 프로젝트 생성 및 dependency 주입
2. `docker-compose`를 사용하여 yaml 파일 생성 후 `docker-compose up` 실행
3. 컨테이너 쉘 내부에서 MongoDB에 접속하여 admin 계정으로 사용자와 DB 생성
   - MongoDB는 데이터를 삽입하는 순간 DB가 생성됩니다.

### 문제 발생 및 해결

- **URI 에러**
  - 형식은 보통 `사용자이름:패스워드@port:port/DB명`이지만 로컬 환경 설정은 `psw:psw@localhost:27017/chatservice?`
  - 문제 해결: Docker 쉘 내에서 `psw` 유저를 다시 생성하고 권한을 부여하여 해결

- **JDBC 데이터 자동 연동 에러**
  - 스프링 실행 시 JDBC 데이터 연동 에러 발생. JDBC 의존성을 모두 주석 처리함에도 JDBC 드라이버 오류 발생
  - 문제 해결: Property 파일에서 JDBC 관련 옵션을 exclude 처리

- **Circular view path [chats] 에러**
  - 스프링이 객체를 뷰 이름으로 인식하여 순환 참조 발생
  - 문제 해결: ResponseEntity나 ModelAndView를 사용하여 명시적으로 처리

- **findAll() 메서드 구현 후 데이터 확인 문제**
  - DB가 chatservice라는 컬렉션에 데이터를 저장하고 있음을 확인
  - 문제 해결: Property 파일에 데이터베이스를 명시적으로 작성

## 2. JSON 데이터 HTTP 통신

### 기본 설정
1. Controller, entity, service 등 MVC 패턴 구성
2. room_id, content, createdname을 입력받는 간단한 HTML 구성

**참고:** 이 방식은 주기적인 데이터 요청으로 인해 서버 부하가 많이 가는 polling 방식이고,
실시간 통신 혹은 채팅이라 보기 어려운 점이 있음. Websocket을 통한 실시간 통신 연결이 필요하다.

## 3. Websocket 구현

### 설정 및 구현
1. WebSocketConfig 작성 및 송수신 주소, 엔드포인트 설정
2. WebSocketMessageHandler 작성 (Controller 디렉토리 안 위치)<br>
**참고:** SimpMessagingTemplate -> TextWebSocketHandler로 통신 프로토콜을 변경 / 간단한 프로젝트기 때문에 STMOP와 같은 고수준 통신 프로토콜이 필요없다고 판단. 추후 프로젝트가 고도화 될 경우 STMOP 통신을 고려해보아야함
3. Gson 라이브러리 사용하여 객체와 JSON 문자열 간 변환 쉽게 처리

### 문제 발생 및 해결
- **Postman 연결 404 에러**
  - 핸들러와 config 작성 후 Postman을 통해 Websocket 요청 시 지속적으로 404 에러 발생
  - 문제 해결: 우선 SocketJS 코드를 주석 처리 후 연결 확인



- **Gson 에러**
  - Gson이 LocalDatetime 타입 필드를 변환하지 못하는 문제
  - 문제 해결: Adapter를 추가 구현하여 Gson이 LocalDatetime을 가져오도록 변경

## 연결 방법
1. View에서 입력받은 createdName과 room_id를 session에 저장 후 Websocket session과 연결
2. HttpHandshakeInterceptor 사용하여 HTTP 세션과 Websocket 세션 연결

이상 1차 구현 완료

### 추가 구현 과제

- 채팅 내역을 DB에 저장 후 관리
- 코드 개선 및 세션 ID 결정
- 클라이언트단에서 메시지 형식 개선
- Docker 빌드 및 AWS에 배포
