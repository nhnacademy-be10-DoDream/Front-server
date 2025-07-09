

import { initEmailValidation, initPasswordValidation, initUserIdValidation } from './validation.js';

(function() {
    'use strict';

    let emailChecked = false;
    let userIdChecked = false;
    let passwordValidator = null;

    function init() {
        // 검증 초기화
        initUserIdValidation('.signup-form', ok => {
            userIdChecked = ok;
        });

        initEmailValidation('.signup-form', ok => {
            emailChecked = ok;
        });

        passwordValidator = initPasswordValidation('.signup-form');

        const form = document.querySelector('.signup-form');
        if (form) {
            form.addEventListener('submit', (e) => {

                if (!userIdChecked) {
                    e.preventDefault();
                    alert('아이디 중복확인을 먼저 진행해주세요.');
                    return;
                }

                if (!emailChecked) {
                    e.preventDefault();
                    alert('이메일 중복확인을 먼저 진행해주세요.');
                    return;
                }

                if (!passwordValidator) {
                    e.preventDefault();
                    alert('비밀번호 검증기가 초기화되지 않았습니다.');
                    return;
                }

                const passwordValid = passwordValidator.isValid();

                if (!passwordValid) {
                    e.preventDefault();
                    alert('비밀번호가 일치하지 않습니다.');
                }
            });
        } else {
            console.error('signup-form Not Found');
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
