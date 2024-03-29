<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>메세지 앱</title>    
</head>
<body>
    <h2>채팅</h2>
    <div id="chatMessages"></div>
    <input type="text" id="roomId" placeholder="Room ID">
    <input type="text" id="content" placeholder="메시지 입력">
    <input type="text" id="createName" placeholder="이름">
    <button onclick="sendMessage()">메시지 보내기</button>

    <script src="chat.js"></script>
</body>
</html>