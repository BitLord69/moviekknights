import authHeader from './auth-header';
import UserHandler from './UserHandler';

export async function extFetch(url, method, body, getFromProtectedPart = false) {
  const { refreshToken } = UserHandler();
  if (url.startsWith('http://localhost/%27')) {
    console.warn("Do not fetch http://localhost:5050/rest/etc, just write: '/rest/etc'")
  }

  let headers = { 'content-type': 'application/json' };
  if (getFromProtectedPart) {
    let h2 = authHeader();
    headers = { ...headers, ...h2 };
  }

  let user = localStorage.getItem('user');
  let token;
  if (user) {
    let userUnpacked = JSON.parse(user);
    if (userUnpacked.accessToken) {
      token = parseJwt(userUnpacked.accessToken)
      let time = Date.now()/1000;
      if(token.exp - time < 300){
        await refreshToken();
      }
    }
  }
  try {
    let result = await fetch(url, {
      method,
      body: body ? JSON.stringify(body) : undefined,
      headers: headers,
    });

    if (result.ok) {
      if (result.headers.get('Content-Type') === 'text/plain;charset=UTF-8') {
        return result.text();
      }
      return result.json()
    }
    return false
  } catch (e) {
    console.log("extFetch catch....")
    return false;
  }

  function parseJwt(token){
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }
}