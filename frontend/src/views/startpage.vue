<template>
  <div class="home">
    <img alt="Vue logo" src="/img/movieknightslogov3.png" />
    <h1>Välkommen till Movie knights</h1>
    <h2>Med hjälp av Google Calendar och vår filmdatabas, kan du boka in filmkvällar direkt i din kalender.</h2>
    <h3>Antal filmer i vår databas: {{new Intl.NumberFormat().format(movieCount)}}</h3>
    <h3>Antal filmarbetare i vår databas: {{new Intl.NumberFormat().format(personCount)}}</h3>
  </div>
</template>

<script>

import MovieHelper from "@/modules/MovieHelper";
import PersonHelper from "@/modules/PersonHelper";
import { onMounted } from 'vue';

export default {
  name: "Home",
  components: {},
  setup() {

    const { movieCount, getMovieCount } = MovieHelper();
    const { personCount, getPersonCount } = PersonHelper();

    onMounted(async () => {
      await getMovieCount();
      await getPersonCount();
    })

    return { movieCount, personCount }
  },
};
</script>
<style scoped>
.home img {
  width: 30%;
}

@media only screen and (max-width: 1040px) {
  .home img {
    width: 40%;
  }
}

@media only screen and (max-width: 700px) {
  .home img {
    width: 60%;
  }
}
</style>