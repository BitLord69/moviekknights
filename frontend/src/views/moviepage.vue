<template>
  <div class="movies">
    <div>Antal filmer i db: {{movieCount}}</div>
    
    <Paginator v-model:first="state.first" :rows="18" :totalRecords="movieCount" class="paginator"
      template="FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
      currentPageReportTemplate="({currentPage} av {totalPages})">
      <template #left>
        <Button type="button" icon="pi pi-refresh" @click="reset()"/>
      </template>
      <template #right>
        <Button type="button" icon="pi pi-search"/>
      </template>
    </Paginator>
    
    <div id="movie-page">
        <Movie :movie="movie" v-for="(movie, index) in state.pagMovies && state.pagMovies.slice(state.first, state.first+18)" :key="index" @click="displayMovieInfo(movie)"/>
    </div>
        <MovieInfoModal v-if="state.showMovieInfo" :movie="state.selectedMovie"/>
        <PersonInfoModal v-if="state.showPersonInfo" :person="state.selectedPerson"/>
    
  </div>

  
</template>

<script>

import Movie from "@/components/Movie.vue";
import MovieInfoModal from "@/components/MovieInfoModal.vue";
import PersonInfoModal from '@/components/PersonInfoModal';
import MovieHelper from "@/_helpers/MovieHelper";
import { reactive, onMounted } from 'vue';

export default {
  name: "Movies",
  components: { Movie, MovieInfoModal, PersonInfoModal },
  setup(){
    const { getMovies, getMovieCount, movies, movieCount, movieError } = MovieHelper();
    let state = reactive({
      first: 0,
      pagMovies: movies,
      showMovieInfo: false,
      showPersonInfo: false,
      selectedMovie: null,
      selectedPerson: null
    })

    onMounted(async () => {
      await getMovies();
      await getMovieCount();
    })

    function reset() {
      state.first = 0;
    }

    function displayMovieInfo(movie) {
      state.showMovieInfo = !state.showMovieInfo;
      state.selectedMovie = movie
    }

    return { state, movies, movieCount, movieError, displayMovieInfo, reset}
  }
}
</script>

<style lang="scss" scope>
  .movies {
    display: grid;
    width: 100%;
    grid-template-rows: 65px 1fr 1fr 1fr;
    grid-template-columns: repeat(12, 1fr);
    row-gap: 12px;
    column-gap: 13px;
  }

  .paginator {
    grid-row: 1/2;
    grid-column: 3/11;
  }

  #movie-page{
    grid-row: 2/5;
    grid-column: 3/11;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(175px, 175px));
    row-gap: 12px;
    column-gap: 13px;
    justify-content: center;
  }
</style>