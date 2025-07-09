const API_URLS = {
    userId: '/api/user/check-user-id',
    email: '/api/user/check-email'
};

const getMessages = () => {
    const container = document.getElementById('validationMessages');

    const getMessage = (selector) => {
        const element = container.querySelector(selector);
        return element ? element.textContent.trim() : null;
    };

    return {
        invalidUserId: getMessage('.msg-invalid-userid') || 'Please enter 4-20 characters using only letters, numbers, and underscores.',
        duplicateUserId: getMessage('.msg-duplicate-userid') || 'This ID is already in use.',
        availableUserId: getMessage('.msg-available-userid') || 'This ID is available.',
        invalidEmail: getMessage('.msg-invalid-email') || 'Please enter a valid email format.',
        duplicateEmail: getMessage('.msg-duplicate-email') || 'This email is already in use.',
        availableEmail: getMessage('.msg-available-email') || 'This email is available.',
        passwordMismatch: getMessage('.msg-password-mismatch') || 'Passwords do not match.',
        networkError: getMessage('.msg-network-error') || 'Network error'
    };
};

const MESSAGES = getMessages();

function createDuplicateValidator(config) {
    const container = document.querySelector(config.containerSel);
    if (!container) return;

    const input = container.querySelector(config.inputSel);
    const feedback = container.querySelector(config.feedbackSel);
    const button = container.querySelector(config.buttonSel);

    if (!input || !feedback || !button) return;

    let isValid = false;
    let currentRequest = null; // 중복 요청 방지

    const show = (msg, ok) => {
        feedback.textContent = msg;
        feedback.className = ok ? 'text-success' : 'text-danger';

        input.classList.remove('is-valid', 'is-invalid');
        input.classList.add(ok ? 'is-valid' : 'is-invalid');

        isValid = ok;
        config.onResult(ok);
    };

    const reset = () => {
        if (isValid) {
            isValid = false;
            feedback.textContent = '';
            feedback.className = '';
            input.classList.remove('is-valid', 'is-invalid');
            config.onResult(false);
        }
    };

    button.onclick = async () => {
        const value = input.value.trim();

        if (!config.validateFormat(value)) {
            show(config.messages.invalid, false);
            return;
        }

        if (currentRequest) {
            currentRequest.abort();
        }

        button.disabled = true;
        button.textContent = '확인중...';

        try {
            const controller = new AbortController();
            currentRequest = controller;

            const res = await fetch(config.buildApiUrl(value), {
                signal: controller.signal
            });

            if (!res.ok) throw new Error(`HTTP ${res.status}`);

            const data = await res.json();
            const isDuplicate = data.duplicate === true;

            show(isDuplicate ? config.messages.duplicate : config.messages.available, !isDuplicate);
        } catch (error) {
            if (error.name !== 'AbortError') {
                show(config.messages.networkError, false);
            }
        } finally {
            button.disabled = false;
            button.textContent = '중복확인';
            currentRequest = null;
        }
    };

    input.oninput = reset;
}


// 아이디 검증
export function initUserIdValidation(containerSel, onResult) {
    createDuplicateValidator({
        containerSel,
        inputSel: '[data-userid-input]',
        feedbackSel: '[data-userid-feedback]',
        buttonSel: '[data-userid-check]',
        validateFormat: (value) => /^[a-zA-Z0-9_]{4,20}$/.test(value),
        buildApiUrl: (value) => `${API_URLS.userId}?user-id=${value}`,
        messages: {
            invalid: MESSAGES.invalidUserId,
            duplicate: MESSAGES.duplicateUserId,
            available: MESSAGES.availableUserId
        },
        onResult
    });
}

// 이메일 검증
export function initEmailValidation(containerSel, onResult) {
    createDuplicateValidator({
        containerSel,
        inputSel: '[data-email-input]',
        feedbackSel: '[data-email-feedback]',
        buttonSel: '[data-email-check]',
        validateFormat: (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
        buildApiUrl: (value) => `${API_URLS.email}?email=${value}`,
        messages: {
            invalid: MESSAGES.invalidEmail,
            duplicate: MESSAGES.duplicateEmail,
            available: MESSAGES.availableEmail
        },
        onResult
    });
}

// 비밀번호 검증
export function initPasswordValidation(containerSel) {
    const container = document.querySelector(containerSel);
    if (!container) return null;

    const form = container.tagName === 'FORM' ? container : container.querySelector('form');
    const newPwd = container.querySelector('[data-new-password]');
    const confirmPwd = container.querySelector('[data-confirm-password]');
    const feedback = container.querySelector('[data-password-feedback]');

    if (!form || !newPwd || !confirmPwd) {
        return null;
    }

    let isPasswordValid = false;

    const showMessage = (msg, isValid) => {
        if (feedback) {
            feedback.textContent = msg;
            feedback.className = isValid ? 'text-success' : 'text-danger';
        }
    };

    const clearStyles = () => {
        confirmPwd.classList.remove('is-valid-green', 'is-invalid');
        if (feedback) {
            feedback.textContent = '';
            feedback.className = '';
        }
    };

    const setValid = () => {
        confirmPwd.classList.add('is-valid-green');
        confirmPwd.classList.remove('is-invalid');
        feedback.textContent='';
    };

    const setInvalid = () => {
        confirmPwd.classList.add('is-invalid');
        confirmPwd.classList.remove('is-valid-green');
        showMessage(MESSAGES.passwordMismatch, false);
    };

    const checkPasswords = () => {
        const newPassword = newPwd.value;
        const confirmPassword = confirmPwd.value;

        if (!newPassword || !confirmPassword) {
            clearStyles();
            isPasswordValid = false;
            return false;
        }

        if (newPassword === confirmPassword) {
            setValid();
            isPasswordValid = true;
            return true;
        } else {
            setInvalid();
            isPasswordValid = false;
            return false;
        }
    };

    confirmPwd.addEventListener('input', checkPasswords);
    newPwd.addEventListener('input', () => {
        if (confirmPwd.value) {
            checkPasswords();
        } else {
            clearStyles();
        }
    });

    form.addEventListener('submit', (e) => {
        if (!checkPasswords()) {
            e.preventDefault();
            alert('비밀번호가 일치하지 않습니다.');
            confirmPwd.focus();
            return false;
        }
    });

    return {
        isValid: () => isPasswordValid
    };
}
