<template>
<div class="movie-container p-grid nested-grid">
  <div class="p-lg-2"></div>
  <div id="movie-page" class="p-grid p-lg-8 p-jc-center">
    <Movie class="p-col-12 p-md-6 p-lg-1 p-m-2" :movie="movie" v-for="(movie, index) in movies" :key="index"/>
  </div>
  <div class="p-lg-2"></div>
</div>
</template>

<script>

import Movie from "@/components/Movie.vue";
import MovieHelper from "@/_helpers/MovieHelper";
import { onMounted } from 'vue';

export default {
  name: "Movies",
  components: { Movie },
  setup(){
    const { getMovies, movies, movieError } = MovieHelper();

    onMounted(() => {
      getMovies();
    })


    return { movies, movieError}
  }
}
</script>

<style scope>

</style>