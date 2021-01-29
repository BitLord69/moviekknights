<template>
  <Button @click="performLogin">Login with Google</Button>
</template>

<script>
import UserHandler from "@/modules/UserHandler";

export default {
  setup() {
    const { signInCallback, auth2 } = UserHandler();

    function performLogin() {
      auth2.value.grantOfflineAccess().then(signInCallback);
    }

    return { performLogin };
  },
};
</script>

<style></style>
