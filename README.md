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
