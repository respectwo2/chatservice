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