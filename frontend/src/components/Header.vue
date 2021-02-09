<template>
  <div id="nav">
    <Toast position="bottom-center" />
    <router-link to="/">Hem</router-link> |
    <router-link to="/movies">Filmer</router-link> |
    <router-link to="/about">Om tekniken</router-link> |
    <span v-if="isLoggedIn">
      <router-link to="/calendar">Kalendern</router-link> |
      <router-link to="/logmeout">Logga ut</router-link>
      </span>
      <span v-else>
      <router-link to="/logmein">Logga in</router-link>
      </span>
  </div>
</template>

<script>

import UserHandler from "@/modules/UserHandler";
import { defineComponent, watchEffect } from "vue";
import { useToast } from "primevue/usetoast";

export default defineComponent({
 async setup() {
  const toast = useToast();
  const { isLoggedIn, currentUser, startApp, showToast } = UserHandler();
  watchEffect(() => {
    if(showToast.value) {
       toast.add({severity:'warn', summary: 'Sessionen har upphört', detail:'Vänligen logga in igen', life: 3000});
       showToast.value = false;
    }
  })
  await startApp();
  return { isLoggedIn, currentUser }
  }
})
</script>
<style lang="scss" scoped>
@import "@/styles/_variables.scss";
#nav {
  padding: 10px 30px 30px 30px;
}

#nav a {
  font-weight: bold;
  color: $link-text;
  //color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: $link-active;
}
</style>