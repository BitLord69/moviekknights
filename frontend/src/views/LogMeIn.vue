<template>
  <Button class="p-button-raised p-button-success" @click="performLogin">
    <i class="fab fa-google p-mr-2"></i>Logga in med Google</Button
  >
</template>

<script>
import UserHandler from "@/modules/UserHandler";

export default {
  name: "LogMeIn",

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
