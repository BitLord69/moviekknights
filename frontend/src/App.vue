<template>
  <Suspense>
    <Header />
  </Suspense>
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
  overflow-x: hidden;
  width: 100vw;
  height: 98vh;
}
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: $text-primary;
  height:100%;
  font-family: "Sarala", sans-serif;
  transition: border .2s;
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

.p-paginator{
  background-color: $bg-secondary !important;
  font-family: "Sarala", sans-serif !important;
}
.p-button{
  color: $text-secondary !important;
  font-family: inherit !important;
  border: $border-primary !important;
  background-color: $bg-secondary !important;
  &:hover{
    border: $border-hover !important;
  }
  &:focus{
    box-shadow: $boxshadow !important;
  }
  .p-button-icon{
    color: $text-secondary;
  }
}

button{
  margin: 0 2px !important;
  color: $text-secondary !important;
  font-family: inherit !important;
  border: $border-primary !important;
  background-color: $bg-secondary !important;
  &:hover{
    border: $border-hover !important;
  }
  &:focus{
    box-shadow: $boxshadow !important;
  }
  .p-button-icon{
    color: $text-secondary;
  }
}
</style>

/**
<link rel="preconnect" href="https://fonts.gstatic.com">
<link href="https://fonts.googleapis.com/css2?family=Libre+Franklin:wght@300;700&family=Sarala&display=swap" rel="stylesheet"> 

font-family: 'Libre Franklin', sans-serif;
font-family: 'Sarala', sans-serif;

*/
