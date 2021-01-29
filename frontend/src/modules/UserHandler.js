import { ref } from "vue";
import { extFetch } from "./extFetch";
import { useRouter } from "vue-router";

const loginError = ref(null);
const userError = ref(null);
const isLoggedIn = ref(null);
const currentUser = ref(null);
const auth2 = ref(null);
const googleApi = ref(null);

export default function UserHandler() {

  const router = useRouter();
  
  async function signInCallback(authResult) {
    console.log("authResult", authResult);

    if (authResult["code"]) {
      // Hide the sign-in button now that the user is authorized
      // Send the code to the server
      let result = await fetch("api/auth/storeauthcode", {
        method: "POST",
        headers: {
          "Content-Type": "application/octet-stream; charset=utf-8",
          "X-Requested-With": "XMLHttpRequest",
        },
        body: authResult["code"],
      });
      console.log("signInCallback, result:", result);

      let result2 = await result.json();
      console.log("result2:", result2);
      if (result2.accessToken) {
        console.log("Storing ", result2, " in local storage!!!");
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
      console.log("Wrong username/password");
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
      console.log("whoami result:", result);
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

  return {
    isLoggedIn,
    loginError,
    currentUser,
    signInCallback,
    userError,
    startApp,
    logout,
    auth2,
  };
}
