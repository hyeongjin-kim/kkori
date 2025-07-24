function loginProvider(socialLoginRequestPath: string) {
  fetch(`${process.env.REACT_APP_API_URL}${socialLoginRequestPath}`);
}

export default loginProvider;
