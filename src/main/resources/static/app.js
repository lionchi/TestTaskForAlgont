let stompClient = null;
const urlWithMaxValue = 'http://localhost:8080/img/max';
const urlWithValue = 'http://localhost:8080/img/';

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
    let socket = new SockJS('/ws');
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
    let statusRow = document.getElementById('status-row');
    if (result === "CPU loaded") {
        statusRow.innerHTML = "<td style='background-color: red'>" + result + "</td>" + "<td>" + "<img id='image'>" + "</td>";
        let querySelector = document.querySelector("image");
        querySelector.setAttribute("src", urlWithMaxValue);
    } else {
        let url = urlWithValue + result.substr(0, 1);
        statusRow.innerHTML = "<td></td>" + "<td>" + "<img id='image'>" + "</td>";
        let querySelector = document.querySelector("image");
        querySelector.setAttribute("src", url);
    }
}

//region Использовался для тестирования программы
function sendName() {
    stompClient.send("/api/cpu", {}, "test");
}
//endregion

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