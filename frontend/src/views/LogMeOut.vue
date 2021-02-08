<template>
  <div></div>
</template>
<script>

import UserHandler from "@/modules/UserHandler";
import { useRouter } from "vue-router";

export default {
  name: "LogMeOut",
  setup() {
    const router = useRouter();
    const { logout } = UserHandler();
    logout();
    router.push('/');
  }
}
</script>

<style>

</style>