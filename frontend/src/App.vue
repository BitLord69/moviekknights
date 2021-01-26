<template>
  <Header />
  <div class="rutaEtt">
    <h1>MOVIE KNIGHTS</h1>
    <h2>Whalecum to Mknights!</h2>
    <h3>Hoppsan kerstin!</h3>
  </div>
  <router-view />
</template>


<script>
import Header from "@/components/Header.vue";
export default {
  components: { Header },
};
</script>

<style lang="scss">
@import "@/styles/_variables.scss";
body {
  background-color: $bg-primary;
  font-size: 16px;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: $text-primary;
}

h1 {
  font-family: "Libre Franklin", sans-serif;
  font-weight: 700;
}

h2 {
  font-family: "Libre Franklin", sans-serif;
  font-weight: 300;
  color: $text-secondary;
}

h3 {
  font-family: "Sarala", sans-serif;
  color: $text-primary;
}

//Test
.rutaEtt {
  width: 400px;
  background-color: $bg-secondary;
  border: $border-primary;
  border-radius: $border-radius;
}
</style>

/**
<link rel="preconnect" href="https://fonts.gstatic.com">
<link href="https://fonts.googleapis.com/css2?family=Libre+Franklin:wght@300;700&family=Sarala&display=swap" rel="stylesheet"> 

font-family: 'Libre Franklin', sans-serif;
font-family: 'Sarala', sans-serif;

*/
