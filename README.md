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


---------------------------------------------------------------------------------

- **Greeting 메세지 보내기**
- 채팅방에 새로운 세션이 접속했을때 접속을 알리는 그리팅 메세지를 보내고자함.
	이 부분을 핸들러메서드 내에서 처리해야하는데, 서버 내에서 메세지를 처리하는 방식과 동일하게
	textMessage라는 객체를 생성해서 그안에 텍스트를 담아 입장과 동시에 메세지를 보내주려고 했는데
	올바른 형태가 아니라 보낼수 없는 오류가 발생함
	- 문제해결 : 채팅방 내의 메세지와 그리팅 메세지는 다르게 처리해야함.<br>
	채팅방 내의 메세지는 이미 클라이언트 단에서
	JSON으로 파싱해서 처리하는데, 내가 보내는 그리팅 메세지는 일반 메세지 형태이기 떄문에 채팅방에 나타나지 않음.
	이를 해결하기 위해 두가지 방법이 있는데,
	첫번째로는 핸들러 메서드 내에서 그리팅 메세지를 JSON으로 파싱하는 새로운 메서드를 작성해서 보내는 방법 or
	두번째로는 클라이언트내에서 처리하는건데, 서버보다는 클라이언트측에서 처리하는게 좋다고 판단해서 클라이언트에서 처리하도록 하자
	
	
- **mongodb id 값 설정하기**
- 채팅 내역을 mongodb내에 저장하는 과정에서 비어있는 chat_id를 임의의 값으로 설정할 것인가 or autoincrement를 통해 자동으로 증가시킬것인가에 대한 이슈가 있었다. 만약 사용하고자 하면 자동 증가 시퀀스 클래스를 별도로 생성해서 이를 호출하는 방식으로 사용할 수 있겠으나, mongodb는 특성상 기본 식별자 id를 제공하고 있기 때문에 이를 활용하는 방향으로 코드를 수정하도록 한다.


- **채팅 데이터 저장하기**
- 채팅 내역을 저장함에 있어서 클라이언트 측 코드에서 작성자 이름을 포함해서 내보내는 문제가 있었다. 서버와 클라이언트 양측에서 모두 수정이 가능한 부분이지만
조건문을 포함하고 있기 때문에 유지보수측에 있어서 서버 측에 코드를 추가하는게 편하다고 판단해 조건문을 걸어 content를 저장하는 코드를 추가한다.



------------------------------------------

### 남은 과제

- Docker 를 통해 어플리케이션 관리 및 AWS 배포 시도