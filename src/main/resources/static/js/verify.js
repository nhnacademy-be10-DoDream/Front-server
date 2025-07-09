let isCodeSent = false;
let timerInterval = null;

function handleSendCode() {
    const userId = document.getElementById("userId")?.value;
    const button = document.getElementById("send-code-btn");

    if (!userId) {
        alert("유저 ID가 존재하지 않습니다.");
        return;
    }

    fetch(`/dormant/send?userId=${encodeURIComponent(userId)}`, {
        method: "POST"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("전송 실패");
            }
            return response.text();
        })
        .then(() => {
            alert("인증번호가 전송되었습니다.");
            startTimer();
            isCodeSent = true;
            button.textContent = "인증번호 재전송";
            startTimer(); // 타이머 재시작
        })
        .catch(error => {
            console.error(error);
            alert("인증번호 전송에 실패했습니다.");
        });
}

function startTimer() {
    clearInterval(timerInterval); // 기존 타이머 제거

    const display = document.getElementById("timer-display");
    let remainingTime = 5 * 60; // 5분

    function updateDisplay() {
        const minutes = Math.floor(remainingTime / 60);
        const seconds = remainingTime % 60;
        display.textContent = `⏱ 남은 시간: ${minutes}:${seconds.toString().padStart(2, '0')}`;
    }

    updateDisplay();

    timerInterval = setInterval(() => {
        remainingTime--;
        updateDisplay();

        if (remainingTime <= 0) {
            clearInterval(timerInterval);
            display.textContent = "⛔ 인증 시간이 만료되었습니다.";
        }
    }, 1000);
}

document.addEventListener('DOMContentLoaded', function () {
    if (verifySuccess) {
        alert(verifyMessage);
        window.location.href = '/auth/login-form';
    }
});

