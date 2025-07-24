const NAVIGATION_BAR_LINKS = Object.freeze({
  home: { path: '/', text: '홈' },
  myPage: { path: '/my-page', text: '마이페이지' },
  interviewQuestions: {
    path: '/interview-questions',
    text: '면접 질문',
  },
});

const SOCIAL_LOGIN_REQUEST_PATHS = Object.freeze({
  kakao: { path: '/oauth2/authorization/kakao', text: '카카오로 로그인' },
  google: { path: '/oauth2/authorization/google', text: '구글로 로그인' },
});

export { NAVIGATION_BAR_LINKS, SOCIAL_LOGIN_REQUEST_PATHS };
