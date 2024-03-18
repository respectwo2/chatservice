# Websocket 활용 Chatservice 구현

##  목표 구현 기능

- 실시간 메세지 전송
- 다수의 채팅방 기능
- 사용자 연결 상태 확인


##  기술 스택

- Springboot, **Websocket**, gradle, mongoDB




-----------------------------------------------

###기초 설정 순서
1. 프로젝트 생성 및 dependency 주입
2. docker-compose 사용을 위한 yaml 파일 생성 및 docker-compose up
3. 컨테이너 쉘 내부에서 DB에 접속해서 admin 계정을 통해 사용자와 DB를 생성<br>
	*(mongodb는 명시적으로 생성할 필요가 없이 데이터를 삽입하는 순간 생성되는듯)
	<br>==>> 지속적인 uri 에러 발생 해결 필요
	
**URI 에러 발생시 고려해볼만한 사항
1. property 파일 내의 uri 주소 설정. (현재는 로컬환경이기에 localhost로 설정)<br>
	-원래 양식은 사용자이름:패스워드@port:port/사용DB이름<br>
	현재는  psw:psw@localhost:27017/chatservice? 로 사용중
2. gradle 내에 의존성이 잘 주입되어 있는지
3. docker내에서 mongoDB 실행 여부 및 사용자 인증 과정 확인<br>
	3-1) mongoDB내에서 admin 계정으로 psw 계정을 생성하고, grant를 통해 권한을 부여했지만 chatservice 데이터베이스내에서 show users 명령어로 사용자가 보이지 않은 문제 발생<br>
	==> mongoDB에서는 admin DB에서 사용자를 생성하고 A DB의 권한을 부여했다면, admin DB에서만 사용자를 확인 가능 A DB내에서 사용자를 확인할 수 없더라도 권한이 이미 부여되고 사용 가능한 상태
	
	#4. JDBC 데이터 자동 연동 에러
		- JDBC 관련 의존성을 모두 주석처리하고, debug 모드를 통해 실행했으나
		여전히 JDBC 드라이버를 찾기 위한 에러가 발생한 것을 확인함.
		- 프로젝트를 생성할때 JDBC 관련 옵션을 선택해서 디펜던시에 주석처리를 해도
		Springboot가 자동으로 데이터를 연동하기 위해 시도하고 있다는 것을 디버깅을 통해 확인
		자동 구성(Auto-configration)
		- 해결) property 파일에 exclude를 통해 JDBC 관련 옵션들을 exclude로 처리
		==> 프로젝트를 생성할때 DB 구성에 대해 조금 더 신경써서 생성할 필요가 있을 것
		
		
		
		
*프로젝트의 기본 구성을 끝내고 mvc패턴을 구현해서 crud를 통해 DB연동 테스트를 하려고 했으나
jakarta.servlet.ServletException: Circular view path [chats] 
와 같은 에러가 발생함

문제 원인 )
    @PostMapping("/chats")
    public ChatCollections createChat(ChatCollections chatCollections) {
        return chatService.createChat(chatCollections);
    }
    이러한 코드를 통해 객체를 생성하고, 그대로 객체를 리턴했는데 
    스프링에서는 이를 뷰이름으로 인식하여 뷰를 찾으려고하는데 뷰가 없기 떄문에 계속해서 순환참조 현상이 발생함
    
    ==> 1. ResponseEntity 나 ModelAndView를 통해 직접 객체가 모델 데이터임을 명시해주는 방법
    	2. ResponseBody 어노테이션이나 Controller 대신 RestController를 사용함으로써
    	객체가 응답 본문으로 직접 사용되어야 함을 명시해줌 
    	
    	## Spring을 사용할때 반환 값을 명확하게 제어해야 한다!!
    	
    	
*postman을 통해 직접 json을 전송해서 보냈으나
chat_id 말고 다른 파라미터들은 null이 전송되는 에러가 발생함
==> 디버그를 통해 파라미터를 아예 못받아오고 있음. chat_id는 기본 id 생성자로 자동으로 추가되고 있는듯 함
==> postman으로 보내는 요청 본문(body)를 객체로 변환하기 위한 코드가 빠져있었음. 
@RequestBody 어노테이션을 컨트롤러에 추가해서 받아온 json 데이터를 객체로 반환시켜서 해결함



*findAll() 메서드를 추가로 구현해서 데이터를 잘 생성하고 정보를 반환하고 있는것을 확인
==> 하지만 컨테이너 내부 쉘에서는 데이터가 비어있는 것으로 확인
mongoDBinfo를 출력하는 클래스를 만들어서 현재 DB가 test(mongo의 기본 db)에서 chatservice라는 컬렉션을 생성하고 마구 집어넣고 있다는 것을 확인함
찾아보니 명시적인 DB를 property파일에 적어놓지 않아서 발생한 문제
==> 이를 해결하기 위해 property 파일에 데이터베이스를 명시적으로 작성해 놓도록 함