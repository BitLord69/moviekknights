export default function authHeader() {
  let user = localStorage.getItem('user');

  if (user) {
    let userUnpacked = JSON.parse(user);
    if (userUnpacked.accessToken) {
      return { Authorization: 'Bearer ' + userUnpacked.accessToken };
    }
  }
  return {};
} 