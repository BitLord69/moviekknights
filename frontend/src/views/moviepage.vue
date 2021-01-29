<template>
<div class="movie-container p-grid nested-grid">
  <div class="p-lg-2">Filmer i db: {{movieCount}}</div>
    <div class="p-grid p-jc-center">
      <Paginator v-model:first="state.first" :rows="18" :totalRecords="movieCount"
        template="FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink" class="p-lg-7">
        <template #left>
          <Button type="button" icon="pi pi-refresh" @click="reset()"/>
        </template>
        <template #right>
          <Button type="button" icon="pi pi-search" />
        </template>
      </Paginator>

      <div id="movie-page" class="p-grid p-lg-8 p-jc-center">
          <Movie class="p-col-12 p-md-6 p-lg-1 p-m-2" :movie="movie" v-for="(movie, index) in state.pagMovies && state.pagMovies.slice(state.first, state.first+18)" :key="index"/>
      </div>
    </div>
  <div class="p-lg-2"></div>
</div>
</template>

<script>

import Movie from "@/components/Movie.vue";
import MovieHelper from "@/_helpers/MovieHelper";
import { reactive, onMounted } from 'vue';

export default {
  name: "Movies",
  components: { Movie },
  setup(){
    const { getMovies, getMovieCount, movies, movieCount, movieError } = MovieHelper();
    let state = reactive({
      first: 0,
      pagMovies: movies
    })

    onMounted(async () => {
      await getMovies();
      await getMovieCount();
    })

    return { state, movies, movieCount, movieError}
  }
}
</script>

<style scope>

</style>