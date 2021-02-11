<template>
  <div class="home">
    <img alt="Vue logo" style="width: 30%;" src="/img/movieknightslogov3.png" />
    <h1>Välkommen till Movie knights</h1>
    <h2>Med hjälp av Google Calendar och vår filmdatabas, kan du boka in filmkvällar direkt i din kalender.</h2>
    <h3>Antal filmer i vår databas: {{new Intl.NumberFormat().format(movieCount)}}</h3>
  </div>
</template>

<script>

import MovieHelper from "@/modules/MovieHelper";
import { onMounted } from 'vue';

export default {
  name: "Home",
  components: {},
  setup() {

    const { movieCount, getMovieCount } = MovieHelper();

    onMounted(async () => {
      await getMovieCount();
    })

    return { movieCount }
  },
};
</script>