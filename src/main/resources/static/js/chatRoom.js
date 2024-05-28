let ws;
var isConnected = false; // 연결 상태를 관리하기 위한 변수

function connect(room_id, createdName) {
    const sockJsUrl = `http://localhost:8080/chatRoom/sockjs?room_id=${room_id}&createdName=${createdName}`;

    ws = new SockJS(sockJsUrl);

    ws.onopen = function() {
        console.log('Connection to room', room_id, 'opened');
        document.getElementById('chat').style.display = 'block';
        
        const greetingMessage = {
            content: `${createdName}님이 방에 입장하셨습니다.`,
            room_id: room_id,
            createdName: createdName
        };
        ws.send(JSON.stringify(greetingMessage));

        document.getElementById('enterOrLeaveButton').innerText = '퇴장하기'; // 버튼 텍스트 변경
        isConnected = true; 
        console.log(createdName)
    };

    ws.onmessage = function(event) {
        let message = JSON.parse(event.data);
        displayMessage(message);
    };

    ws.onclose = function() {
        console.log('Connection closed');
        console.log()
        document.getElementById('chat').style.display = 'none';
        document.getElementById('enterOrLeaveButton').innerText = '입장하기'; // 버튼 텍스트 변경
        isConnected = false; // 연결 상태를 거짓으로 변경
    };

    ws.onerror = function(error) {
        console.error('WebSocket error:', error);
    };
}

function disconnect(room_id, createdName) {

    const leaveMessage = {
        content: `${createdName}님이 방을 떠났습니다.`,
        room_id: room_id,
        createdName: createdName
    };
    ws.send(JSON.stringify(leaveMessage));

    ws.close();

    document.getElementById('enterOrLeaveButton').textContent = '입장하기';
    
    //채팅내역을 지우는 코드
    document.getElementById('messages').innerHTML = '';

    
}

function displayMessage(message) {
    const messageDiv = document.createElement('div');
    messageDiv.textContent = message.content;
    document.getElementById('messages').appendChild(messageDiv);
}

function sendMessage() {
    let messageContent = document.getElementById('messageInput').value;
    let name = document.getElementById('createdName').value;
    let room_id = document.getElementById('room_id').value;
    if (messageContent && ws) {
        let message = {
            content: `${name}: ${messageContent}`,
            room_id: room_id, 
            createdName: name 
        };
        ws.send(JSON.stringify(message)); 
        document.getElementById('messageInput').value = '';
    }	
}

document.getElementById('sendButton').addEventListener('click', function() {
    sendMessage();
});

document.getElementById('messageInput').addEventListener('keypress', function(event) {
    if (event.key === '입장하기') {
        sendMessage();
    }
});

document.getElementById('enterChatRoomForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const room_id = document.getElementById('room_id').value;
    const createdName = document.getElementById('createdName').value;
    const enterOrLeaveButton = document.getElementById('enterOrLeaveButton'); 
    if (enterOrLeaveButton.textContent === '입장하기') {
        connect(room_id, createdName);
    } else {
        disconnect(room_id, createdName);
    }
});

document.getElementById('enterOrLeaveButton').addEventListener('click', function() {
    const room_id = document.getElementById('room_id').value;
    const createdName = document.getElementById('createdName').value;
    // 연결 상태에 따라 입장 또는 퇴장 처리
    if (!isConnected) {
        connect(room_id, createdName);
    } else {
        disconnect(room_id, createdName);
    }
});


