import authHeader from './auth-header';

export async function extFetch(url, method, body, getFromProtectedPart = false) {
  if (url.startsWith('http://localhost/%27')) {
    console.warn("Do not fetch http://localhost:5001/rest/etc, just write: '/rest/etc'")
  }

  let headers = { 'content-type': 'application/json' };
  if (getFromProtectedPart) {
    let h2 = authHeader();
    headers = { ...headers, ...h2 };
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
}