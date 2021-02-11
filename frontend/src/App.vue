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

a, .crewLink {
  color: $text-secondary;
  text-decoration-line: none;
  cursor: pointer;
  &:hover {
    text-decoration-line: underline;
  }
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

.overlay {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
}

.modal-dialog {
  width: 40%;
  margin: 2% auto;
  height: 96%;
  z-index: 2;
}

.modal-content {
  width: 100%;
  background-color: $bg-secondary;
  border: $border-primary;
  border-radius: $border-radius;
}

.modal-header {
  display: flex;
  padding: 1rem;
  border-bottom: $border-primary;
}

.modal-body {
  display: grid;
  grid-template-columns: 225px auto;
  padding: 2%;
  text-align: left;
}

.poster {
  grid-area: poster;
  z-index: 2;
  img {
    width: 210px;
    height:300px;
    box-shadow: $boxshadow;
  }
}

.info {
  grid-area: info;
  z-index: 2;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

hr {
  width: 100%;
  border-color: $text-secondary;
}

.showMoreText {
  cursor: pointer;
  color: $text-secondary;
}

.p-inputswitch-slider {
  background: $bg-primary;

}

.p-inputswitch.p-focus .p-inputswitch-slider{
  box-shadow: 0 0 0 1px $text-secondary !important;
  border-radius: 30px;
}

.p-inputswitch.p-inputswitch-checked .p-inputswitch-slider {
    background: $bg-tertiary !important;
}

.p-inputswitch.p-inputswitch-checked .p-inputswitch-slider:before {
    background: $bg-secondary !important;
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

@media only screen and (max-width: 1600px) {
  .modal-dialog {
    width: 45%;
  }
}

@media only screen and (max-width: 1440px) {
  .modal-dialog {
    width: 53%;
  }
}

@media only screen and (max-width: 1200px) {
  .modal-dialog {
    width: 62%;
  }
}

@media only screen and (max-width: 1024px) {
  .modal-dialog {
    width: 75%;
  }
}

@media only screen and (max-height: 800px) {
  .modal-dialog {
    margin: 1% auto;
  }

  .modal-body {
    grid-template-columns: 175px auto;
  }
  
  .poster img {
    width: 160px;
    height:240px;
  }

  .modal-header {
    padding: .5rem;
  }
}

@media only screen and (max-width: 800px) {
  .modal-body {
    grid-template-columns: 175px auto;
  }
  .modal-dialog {
    width: 94%;
  }

  .poster {
    display: flex;
    justify-content: center;
  }

  .poster img {
    width: 160px;
    height:240px;
  }

  .modal-header {
    padding: .5rem;
  }
}
</style>

/**
<link rel="preconnect" href="https://fonts.gstatic.com">
<link href="https://fonts.googleapis.com/css2?family=Libre+Franklin:wght@300;700&family=Sarala&display=swap" rel="stylesheet"> 

font-family: 'Libre Franklin', sans-serif;
font-family: 'Sarala', sans-serif;

*/
