let ws;

function connect(room_id,createdName) {
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
    if (event.key === 'Enter') {
        sendMessage();
    }
});

document.getElementById('enterChatRoomForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const room_id = document.getElementById('room_id').value;
    const createdName = document.getElementById('createdName').value;
    connect(room_id,createdName);
});
