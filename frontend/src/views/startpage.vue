<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png" />
    <button @click="performLogin">Login with google</button>
  </div>
</template>

<script>

export default {
  name: "Home",
  components: {},
  setup() {
    let gapi = window.gapi;
    const CLIENT_ID = "205088827578-u7c23dj0t8mrlt305c8vdnpjcufgsffb.apps.googleusercontent.com";
    
    
    let auth2;
    gapi.load("auth2", function () {
      auth2 = gapi.auth2.init({
        client_id: CLIENT_ID,
        scope: "https://www.googleapis.com/auth/calendar.events",
      });
    });

    function performLogin() {
      auth2.grantOfflineAccess().then(signInCallback);
    }

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
        console.log(result);
      } else {
        // There was an error.
      }
    }

    return { performLogin }
  },
};
</script>