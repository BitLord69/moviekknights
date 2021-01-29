<template>
  <Button class="p-button-raised p-button-success" @click="performLogin">
    <i class="fab fa-google p-mr-2"></i>Login with Google</Button
  >
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

<style scoped></style>
