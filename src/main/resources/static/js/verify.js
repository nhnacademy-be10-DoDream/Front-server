let timerInterval = null;

function handleSendCode() {
    const userId = document.getElementById("userId")?.value;
    const button = document.getElementById("send-code-btn");

    if (!userId) {
        alert("유저 ID가 존재하지 않습니다.");
        return;
    }

    fetch(`/auth/dormant/send?userId=${encodeURIComponent(userId)}`, {
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
            // 타이머 종료 시점을 localStorage에 저장
            const expiresAt = Date.now() + 5 * 60 * 1000;
            localStorage.setItem("dormantTimerExpiresAt", expiresAt.toString());
            startTimer();
            button.textContent = "인증번호 재전송";
        })
        .catch(error => {
            console.error(error);
            alert("인증번호 전송에 실패했습니다.");
        });
}

function startTimer() {
    clearInterval(timerInterval); // 이전 타이머 제거
    const display = document.getElementById("timer-display");

    const expiresAt = parseInt(localStorage.getItem("dormantTimerExpiresAt"), 10);
    if (isNaN(expiresAt)) return;

    function updateDisplay() {
        const now = Date.now();
        const remainingTime = Math.max(0, Math.floor((expiresAt - now) / 1000));

        const minutes = Math.floor(remainingTime / 60);
        const seconds = remainingTime % 60;
        display.textContent = `⏱ 남은 시간: ${minutes}:${seconds.toString().padStart(2, '0')}`;

        if (remainingTime <= 0) {
            clearInterval(timerInterval);
            display.textContent = "⛔ 인증 시간이 만료되었습니다.";
            localStorage.removeItem("dormantTimerExpiresAt");
        }
    }

    updateDisplay();
    timerInterval = setInterval(updateDisplay, 1000);
}

document.addEventListener('DOMContentLoaded', function () {
    if (verifySuccess) {
        alert(verifyMessage);
        localStorage.removeItem("dormantTimerExpiresAt"); // 성공 시 타이머 종료
        window.location.href = '/auth/login-form';
    } else {
        startTimer(); // 실패든 뭐든 타이머 재시작 시도
    }
});

