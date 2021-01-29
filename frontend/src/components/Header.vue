<template>
  <div id="nav">
    <router-link to="/">Home</router-link> |
    <router-link to="/about">About</router-link> |
    <router-link to="/movies">Movies</router-link> |
    <span v-if="isLoggedIn">
      <router-link to="/logmeout"> Log out </router-link>
      </span>
      <span v-else>
      <router-link to="/logmein"> Log in </router-link>
      </span>
  </div>
</template>

<script>

import UserHandler from "@/modules/UserHandler";

export default {
  
 async setup() {
  
  const { isLoggedIn, currentUser, startApp } = UserHandler();

  await startApp();
  

  return { isLoggedIn, currentUser }
  }

}
</script>
<style lang="scss" scoped>
@import "@/styles/_variables.scss";
#nav {
  padding: 30px;
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