export async function redirectToSocialLogin(socialLoginRequestPath: string) {
  window.location.href = process.env.BASE_URL + '/' + socialLoginRequestPath;
}
