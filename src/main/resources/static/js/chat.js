function sendMessage() {
    var roomId = document.getElementById("roomId").value;
    var content = document.getElementById("content").value;
    var createName = document.getElementById("createName").value;

    fetch('/chats', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            roomId: roomId,
            content: content,
            createName: createName
        })
    })
    .then(response => response.json())
    .then(data => {
        console.log('Success:', data);
        getMessages(); // 메시지 전송 후 모든 메시지 다시 불러오기
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}

function getMessages() {
    fetch('/chats')
    .then(response => response.json())
    .then(data => {
        var chatMessages = document.getElementById("chatMessages");
        chatMessages.innerHTML = ""; // 이전 메시지 지우기
        data.forEach(chat => {
            var messageElement = document.createElement("div");
            messageElement.textContent = chat.createName + ": " + chat.content;
            chatMessages.appendChild(messageElement);
        });
    });
}

// 페이지 로드 시 메시지 로드
getMessages();
