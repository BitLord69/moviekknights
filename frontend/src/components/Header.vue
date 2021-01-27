<template>
  <div id="nav">
    <router-link to="/">Home</router-link> |
    <router-link to="/about">About</router-link>
  </div>
</template>

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