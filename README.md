# Websocket 활용 Chatservice 구현

## 목표 구현 기능

- 실시간 메세지 전송
- 다수의 채팅방 기능
- 사용자 연결 상태 확인

## 기술 스택

- Springboot, **Websocket**, gradle, mongoDB

---

### 1. 기초 설정

1. 프로젝트 생성 및 dependency 주입
2. docker-compose 사용을 위한 yaml 파일 생성 및 `docker-compose up`
3. 컨테이너 쉘 내부에서 DB에 접속해서 admin 계정을 통해 사용자와 DB를 생성
   *(mongodb는 명시적으로 생성할 필요가 없이 데이터를 삽입하는 순간 생성되는듯)*

**문제 발생 및 해결**

- *uri 에러 문제*:
    - 보통의 형식은 `사용자이름:패스워드@port:port/DB명` 으로 작성하지만, 현재 설정은 로컬환경이기 때문에 `psw:psw@localhost:27017/chatservice?`로 명시함.
    - 하지만 여전한 에러 발생. docker 내에서 mongoDB가 제대로 실행되고 있지 않을 가능성을 염두해두고 확인해 봤지만 잘 실행되고 있음.
    - 해결) docker 쉘 내에서 `psw` 유저를 다시 생성하고, 권한을 부여함으로써 문제 해결


- *JDBC 데이터 자동 연동 에러*:
    - 스프링을 실행했을 경우 JDBC 데이터 연동 에러가 발생함.
      이에 JDBC 디펜던시를 모두 주석처리하고, 실행했으나 여전히 JDBC 드라이버를 찾기 위한 에러가 발생함
    - Auto-configration(자동 구성)을 통해 Springboot가 자동으로 데이터를 연동하기 위해 계속해서 시도하고 있음을
      디버그 모드를 통해 확인함.
    - 해결) property 파일에 exclude를 통해 JDBC 관련 옵션들을 exclude로 처리
      ==> 프로젝트 생성 시 JDBC 관련 옵션을 체킹했기 때문에 일어난 문제임으로 주의할 필요 있음


- *Circular view path [chats]* 에러 발생 문제:
    - 스프링에서는 객체를 뷰이름으로 인식하여 뷰를 찾으려고 하는데 뷰가 없기 때문에 계속해서 순환참조 현상이 발생함
    - 해결)
        1. ResponseEntity나 ModelAndView를 통해 직접 객체가 모델 데이터임을 명시
        2. ResponseBody 어노테이션이나 Controller 대신 RestController를 사용하여 객체가 응답 본문으로 직접 사용되어야 함을 명시


- *findAll() 메서드를 추가로 구현 후 데이터 확인 문제*:
    - 현재 DB가 test(mongo의 기본 db)에서 chatservice라는 컬렉션을 생성하고 마구 집어넣고 있다는 것을 확인함
    - 명시적인 DB를 property 파일에 작성하지 않아서 발생한 문제
    - 해결) property 파일에 데이터베이스를 명시적으로 작성


### 2. JSON 데이터 HTTP 통신을 통해 데이터 보내기

1. 기본적인 controller, entity, service 등 MVC 패턴을 구성함
2. room_id, content, createdname을 입력받는 간단한 html 구성
	*기본 id를 chat_id로 설정하고 autoincrement 하는 것이 필요
		- mySQL 같은 DB일 경우에는 generatedValue 어노테이션으로 사용 가능하나
		mongoDB에서는 작동하지 않기에 별도의 컬렉션 생성을 통해 관리해야함. 
		하지만 본 프로젝트에서는 필요하지 않다고 판단하기에 해당 작업은 수행하지 않기로 결정

** 이 방식은 전통적인 HTTP 통신을 사용한 메세지 전달방식이고, 상태 정보를 유지하기 어렵다는 단점이 존재함.
클라이언트가 주기적으로 새로운 데이터를 요청하기 떄문에 서버에 부하가 많이 가는 방식이다.
실시간 통신처럼 보일 수 있지만, 정확하게는 polling 방식이라고 할 수 있고,
실제 서버를 운영하기 위해서는 long polling으로 서버 부하를 줄일 수 있겠지만, 이는 실시간 통신이라고 보기 어렵다
그러므로 websocket를 통한 통신연결을 구현해야한다.

		
		
		
		
### 3. Websocket 구현

1. WebSocketConfig를 작성해 송수신 주소와 엔드포인트 설정
2. 기존의 HTTP통신의 ChatController와 구분하기 위해 WebSocketMessageHandler 작성
	- 이 클래스는 서버 간 웹소켓을 담당하기때문에 controller 디렉토리 안에 위치
	SimpMessagingTemplate 클래스를 사용해서 구현
	==>
	TextWebSocketHandler로 변경
	이유)1.단순성
		2.구현 용이성
		3.성능
		4.자유도
	단점)프로젝트가 고도화되거나, 메세지 브로드캐스팅, 멀티채널 통신 등의 고급기능이 필요해질 경우
	STOMP같은 고수준의 프로토콜 통신을 사용해볼 것을 고려해야함.
	
	
3. 객체와 Json 문자열간의 변환을 쉽게 하기 위해 Gson 라이브러리 사용
	WebSocketHandler에서 사용
	**고려해볼만한 부분
		-config에서 실무에서는 특정 도메인만을 허용해야함.
		특정사용자에게 메세지를 보내거나 고도화된 서비스를 사용하게 될 시 STMOP 통신을 고려해야함
		
		
- *postman에서 연결 404 에러 발생*:
    - 핸들러와 config를 작성한 후 postman을 통해 websocket 요청을 보냈는데 지속적으로
    404 에러가 뜨는 현상이 발생함
    property 에서 debug 모드를 활성화 한후 로깅한 결과
    SocketJS 에러인 것으로 확인함
    withSocketJS코드는 웹소켓을 지원하지 않는 브라우저에서도 접속할 수 있게 해주는 코드인데
    왜 에러가 발생하는지 찾지 못해 일단 해당 코드를 주석처리한 후 제대로 연결되는 것을 확인함
	
	
- *Gson 에러 발생*:
	- Chat entity에 LocalDatetime 타입으로 설정해놓은 필드를 Gson이 변환해서 가져오지 못하는 현상 발생
	Adapter를 작성해 LocalDatetime을 Gson이 가져오게 변경함
	
	
4. **어떻게 view와 websocket을 구현한 채팅방을 연결할 것인가??**
	- 1. view에서 입력받은 createdName과 room_id를 session에 저장하고
	이걸 websocket의 session과 연결한다?? (구현의 핵심 과제)
	HttpHandshakeInterceptor 를 사용 ( HTTP 세션과 Websocket 세션 연결 )
	
4-1) 선택 가능한 옵션
	1) URL를 통한 식별 방식
	2) DB또는 메모리를 기반으로 해 서버내에서 처리하는 방식
	3) JWT 토큰 또는 세션을 이용한 관리 방식
	

5. chatRoom 에서의 채팅을 json 형태의 메세지로 변환하고,
	세션을 통해 createdName과 room_id를 받아서 웹소켓으로 연결
	
		5-1)경로 변수를 사용하기 위해서 연결된 websocket 세션에서 경로 변수를 추출하는 방법이 필요.
		websocketsession에 저장된 URI 정보를 사용해서 경로 변수를 파싱하는 getRoomIDfromPath 메서드를 작성해서 사용
		
		
6. postman을 통해 경로에 따른 웹소켓 연결이 잘 작동하는걸 확인했지만,
URI로 직접 검색했을때는 Can "Upgrade" only to "WebSocket" 라는 오류가 발생하는걸 확인함
	검색 결과 주요 원인은로
	6-1) 클라이언트측에서 웹소켓 연결을 시도할때 upgrade 헤더를 포함하지 않았기 떄문 (이쪽이 유력)
	6-2) 프록시서버나 로드밸런스 쪽의 문제
	

** 클라이언트 접근 방식 vs 서버 사이드 접근 방식 **	
	
	- *클라이언트 접근 방식 : 동적인 사용자 경험 제공, 서버 부하 감소, 개발 분리* 채택
	- 서버 사이드 접근 방식 : 검색 엔진 최적화 유리, 초기 로딩 속도 에 유리한 측면

	
**ConcurrentHashMap**
	- 웹소켓 통신을 위해 세션단위로 관리하기 위해 HashMap을 사용하려 했으나, 뤼튼과 예제에서 ConcurrentHashMap을 사용하는걸 보고
	이를 채택하기로 결정. HashMap보다 멀티스레드 환경에서 동시성을 보장하기 더 적합한 자료구조로 볼 수 있음
	
	
	
	
### 추가 구현 과제

- 채팅 내역 DB에 저장후 관리

- 코드 개선하기 (세션의 ID를 어떤것으로 할건지 추가적으로 고민 필요)

- 클라이언트단에서 메세지 형식 개선 (작성자, 작성시간)

- docker 빌드 작업 & AWS 배포 시도