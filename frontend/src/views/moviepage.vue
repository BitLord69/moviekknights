<template>
<div class="movie-container p-grid nested-grid">
  <div class="p-lg-2">Filmer i db: {{movieCount}}</div>
  <div id="movie-page" class="p-grid p-lg-8 p-jc-center">
    <Paginator v-model:first="first" :rows="3" :totalRecords="movieCount"
      template="FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink">
      <template #left>
        <Button type="button" icon="pi pi-refresh" @click="reset()"/>
      </template>
      <template #right>
        <Button type="button" icon="pi pi-search" />
      </template>
    </Paginator>
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
    const { getMovies, getMovieCount, movies, movieCount, movieError } = MovieHelper();

    onMounted(async () => {
      await getMovies();
      await getMovieCount();
    })


    return { movies, movieCount, movieError}
  }
}
</script>

<style scope>

</style>