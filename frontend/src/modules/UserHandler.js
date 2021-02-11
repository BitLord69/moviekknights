import { ref } from "vue";
import { extFetch } from "./extFetch";
import { useRouter } from "vue-router";
import authHeader from './auth-header';

const loginError = ref(null);
const userError = ref(null);
const isLoggedIn = ref(null);
const currentUser = ref(null);
const auth2 = ref(null);
const googleApi = ref(null);
const showToast = ref(false);

export default function UserHandler() {

  const router = useRouter();
  
  async function signInCallback(authResult) {
    if (authResult["code"]) {
      let result = await fetch("api/auth/storeauthcode", {
        method: "POST",
        headers: {
          "Content-Type": "application/octet-stream; charset=utf-8",
          "X-Requested-With": "XMLHttpRequest",
        },
        body: authResult["code"],
      });

      let result2 = await result.json();
      
      if (result2.accessToken) {
        localStorage.setItem("user", JSON.stringify(result2));
      }
      loginError.value = null;
      isLoggedIn.value = true;
      currentUser.value = result2;
      router.push("/");
    } else {
      isLoggedIn.value = false;
      currentUser.value = null;
      loginError.value = "Bad username and/or password!";
    }
  } 

  async function refreshToken() {
     
      let headers = { 'content-type': 'application/json' };
      let h2 = authHeader();
      headers = { ...headers, ...h2 };
      
      try {
        let response = await fetch("/api/auth/refreshtoken", {
          headers: headers,
        });
        let result = await response.json();
      
        if (result.accessToken) {
          localStorage.setItem("user", JSON.stringify(result));
        }
        
      } catch (e) {
        console.log("extFetch catch....", e)
      }
  }

  function logout() {
    localStorage.removeItem("user");
    isLoggedIn.value = false;
    currentUser.value = null;
  }

  function loadGoogle() {
    googleApi.value = window.gapi;
    const CLIENT_ID =
      "205088827578-u7c23dj0t8mrlt305c8vdnpjcufgsffb.apps.googleusercontent.com";

    googleApi.value.load("auth2", function() {
     auth2.value = googleApi.value.auth2.init({
        client_id: CLIENT_ID,
        scope: "https://www.googleapis.com/auth/calendar.events",
      });
    });
  }

  async function startApp() {
    loadGoogle();
    let result;

    try {
      result = await extFetch("/api/auth/whoami", "GET", undefined, true);
      if (result.error) {
        isLoggedIn.value = false;
        userError.value = result.error;
        return;
      }
      isLoggedIn.value = true;
      currentUser.value = result;
    } catch (e) {
      userError.value = e;
      isLoggedIn.value = false;
      return;
    }
  }

  function parseJwt(token){
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }

  function isTokenValid(){
    let user = localStorage.getItem('user');
    let token;
    if (user) {
      let userUnpacked = JSON.parse(user);
      if (userUnpacked.accessToken) {
        token = parseJwt(userUnpacked.accessToken)
        let time = Date.now()/1000;
        if(token.exp - time > 0){
          return true;
        }
      }
    }
    return false;
  }

  return {
    auth2,
    userError,
    showToast,
    loginError,
    isLoggedIn,
    currentUser,
    logout,
    parseJwt,
    startApp,
    refreshToken,
    isTokenValid,
    signInCallback,
  };
}
