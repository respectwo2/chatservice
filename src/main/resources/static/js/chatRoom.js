let ws;

function connect(room_id,createdName) {
    const sockJsUrl = `http://localhost:8080/chatRoom/sockjs?room_id=${room_id}&createdName=${createdName}`;
    
    ws = new SockJS(sockJsUrl);

    ws.onopen = function() {
        console.log('Connection to room', room_id, 'opened');
        // 채팅창 표시
        document.getElementById('chat').style.display = 'block';
    };

    ws.onmessage = function(event) {
        let message = JSON.parse(event.data);
        displayMessage(message);
    };

    ws.onclose = function() {
        console.log('Connection closed');
    };

    ws.onerror = function(error) {
        console.error('WebSocket error:', error);
    };
}

function displayMessage(message) {
    const messageDiv = document.createElement('div');
    messageDiv.textContent = message.content;
    document.getElementById('messages').appendChild(messageDiv);
}

function sendMessage() {
    let messageContent = document.getElementById('messageInput').value;
    if (messageContent && ws) {
        // 임시로 여기에 메시지를 출력하는 코드를 추가합니다.
        console.log("전송된 메시지:", messageContent);
        ws.send(JSON.stringify({ content: messageContent }));
        document.getElementById('messageInput').value = '';
    }
}


document.getElementById('sendButton').addEventListener('click', function() {
    sendMessage();
});

document.getElementById('messageInput').addEventListener('keypress', function(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
});

document.getElementById('enterChatRoomForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const room_id = document.getElementById('room_id').value;
    const createdName = document.getElementById('createdName').value;
    // 채팅 연결
    connect(room_id,createdName);
});
