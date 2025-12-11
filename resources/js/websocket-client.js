let stompClient = null;
let connected = false;

function log(message, type = 'info') {
    const logsDiv = document.getElementById('logs');
    const timestamp = new Date().toLocaleTimeString();
    const logClass = type === 'error' ? 'log-error' :
                   type === 'success' ? 'log-success' : 'log-info';
    logsDiv.innerHTML += `<div class="log-entry ${logClass}">[${timestamp}] ${message}</div>`;
    logsDiv.scrollTop = logsDiv.scrollHeight;
}

function updateUI(isConnected) {
    connected = isConnected;
    const indicator = document.getElementById('statusIndicator');
    const connectBtn = document.getElementById('connectBtn');
    const disconnectBtn = document.getElementById('disconnectBtn');

    if (isConnected) {
        indicator.classList.add('connected');
        connectBtn.style.display = 'none';
        disconnectBtn.style.display = 'block';
    } else {
        indicator.classList.remove('connected');
        connectBtn.style.display = 'block';
        disconnectBtn.style.display = 'none';
    }
}

function connect() {
    const serverUrl = document.getElementById('serverUrl').value;
    const userId = document.getElementById('userId').value;

    if (!userId) {
        alert('Пожалуйста, введите User ID');
        return;
    }

    log(`Попытка подключения к ${serverUrl} с userId: ${userId}`, 'info');

    // Создаем SockJS соединение с параметром userId
    const socket = new SockJS(`${serverUrl}?userId=${userId}`);
    stompClient = Stomp.over(socket);

    // Отключаем debug логи (можно включить для отладки)
    stompClient.debug = (str) => {
        console.log(str);
    };

    stompClient.connect({},
        function(frame) {
            log('✓ Успешное подключение к WebSocket', 'success');
            log(`Подписка на /user/${userId}/queue/notifications`, 'info');
            updateUI(true);

            // Подписываемся на персональную очередь
            stompClient.subscribe(`/user/queue/notifications`, function(message) {
                log('✓ Получено уведомление!', 'success');
                const notification = JSON.parse(message.body);
                displayNotification(notification);
            });

            log('✓ Готов к приему уведомлений', 'success');
        },
        function(error) {
            log(`✗ Ошибка подключения: ${error}`, 'error');
            updateUI(false);
            console.error('Connection error:', error);
        }
    );
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
        log('Отключено от сервера', 'info');
    }
    updateUI(false);
}

function displayNotification(notification) {
    const notificationsDiv = document.getElementById('notifications');

    // Удаляем empty state
    const emptyState = notificationsDiv.querySelector('.empty-state');
    if (emptyState) {
        emptyState.remove();
    }

    const notificationElement = document.createElement('div');
    notificationElement.className = 'notification-item';

    const timestamp = new Date(notification.timestamp);
    const formattedTime = timestamp.toLocaleString('ru-RU');

    notificationElement.innerHTML = `
        <span class="notification-type type-${notification.type}">${notification.type}</span>
        <div class="notification-message">${notification.message}</div>
        <div class="notification-time">📅 ${formattedTime}</div>
    `;

    notificationsDiv.insertBefore(notificationElement, notificationsDiv.firstChild);
}

// Отключаемся при закрытии страницы
window.addEventListener('beforeunload', function() {
    if (connected) {
        disconnect();
    }
});

log('Приложение готово к работе', 'success');