var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/statistic', function (cpu) {
            showGreeting(cpu.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showGreeting(result) {
    var statusRow = document.getElementById('status-row');
    if (result === "CPU loaded") {
        statusRow.innerHTML="<td>" + result + "</td>" + "<td>" + "<img src='http://localhost:8080/imgError'>" + "</td>";
    } else {
        statusRow.innerHTML = "<td>" + result + "</td>" + "<td>" + "<img src='http://localhost:8080/img'>" + "</td>";
    }
}

// Использовался для тестирования программы
function sendName() {
    stompClient.send("/api/cpu", {}, "test");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
});