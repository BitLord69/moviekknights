<template>
  <div class="movies">

    <Paginator :rows="18" :totalRecords="movieCount" @page="onPage($event)" class="paginator"
      template="FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
      currentPageReportTemplate="({currentPage} av {totalPages})">
      <template #left>
        <InputSwitch v-model="state.hideAdult" />&nbsp;Censurera vuxenfilmer
      </template>
      <template #right>
        <AutoComplete v-model="state.searchTerm" :suggestions="state.filteredMovies" @complete="searchMovie($event)" field="title" />
      </template>
    </Paginator>
    
    <div id="movie-page">
        <Movie :movie="movie" v-for="(movie, index) in state.pagMovies" :key="index" @click="displayMovieInfo(movie)"/>
    </div>
        <MovieInfoModal v-if="state.showMovieInfo" :movie="state.selectedMovie"/>
        <PersonInfoModal v-if="state.showPersonInfo" :person="state.selectedPerson" :movies="state.pagMovies" :movieListChosen="state.movieListChosen" />
  </div>
</template>

<script>
import { reactive, onMounted, watchEffect } from 'vue';
import Movie from "@/components/Movie.vue";
import MovieInfoModal from "@/components/MovieInfoModal.vue";
import MovieHelper from "@/modules/MovieHelper";
import AutoComplete from 'primevue/autocomplete';
import PersonInfoModal from '@/components/PersonInfoModal';

export default {
  name: "Movies",
  components: { Movie, MovieInfoModal, AutoComplete, PersonInfoModal },
  setup(){
    const { getMovieCount, movieCount, movieError, getMoviesByPagination,
     getMoviesBySearch, moviesByPagination, moviesBySearch } = MovieHelper();
    let state = reactive({
      first: 0,
      moviesOnPage: 18,
      pagMovies: moviesByPagination,
      showMovieInfo: false,
      selectedMovie: null,
      searchTerm: '',
      filteredMovies: moviesByPagination.value,
      selectedPerson: null,
      movieListChosen: "",
      hideAdult: true,
    })

    watchEffect(async () => {
      if (typeof state.searchTerm === 'object') {
        state.selectedMovie = state.searchTerm;
        state.showMovieInfo = true;
      }
    })

    onMounted(async () => {
      await getMovieCount();
      await getMoviesByPagination(0);
    })

    function reset() {
      state.first = 0;
    }

    async function onPage(event) {
      await getMoviesByPagination(event.page);
      state.pagMovies = moviesByPagination.value;
    }

    function displayMovieInfo(movie) {
      state.showMovieInfo = !state.showMovieInfo;
      state.selectedMovie = movie
    }

   async function searchMovie(event) {
      console.log("event", event);
      if (!event.query.trim().length) {
        state.filteredMovies = moviesByPagination.value;
      }
      else {
        await getMoviesBySearch(event.query);
        state.filteredMovies = moviesBySearch.value;
      }
    }

    return { state, movieCount, movieError, displayMovieInfo, reset, searchMovie, onPage}
  }
}
</script>

<style lang="scss" scope>
@import "@/styles/_variables.scss";
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
    .p-paginator-left-content {
      width:33%;
      margin-right: 0;
      display: flex;
      justify-content: flex-start;
    }
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

  .p-inputtext {
    background-color: $bg-primary;
    border: $border-primary !important;

    &:enabled:focus {
      box-shadow: $boxshadow;
    }

    &:hover {
      border: $border-hover !important;
    }
  }

  .p-paginator {
    justify-content: space-between;
  }

  .p-ink-active {
    border: $border-hover !important;
  }

  .p-autocomplete-panel {
    background-color: $bg-primary;
  }

  .p-paginator-right-content {
    width:33%;
    display: flex;
    justify-content: flex-end;
    margin-left: 0 !important;
  }

@media only screen and (max-width: 1250px) {
  .paginator {
    grid-column: 1/12;
  }

  #movie-page{
    grid-column: 1/12;
  }
}

@media only screen and (max-width: 900px) {
  .movies {
    grid-template-rows: 125px 1fr 1fr 1fr;
  }

  .paginator {
    .p-paginator-left-content {
      width:100%;
      justify-content: center;
    }
  }

  #movie-page{
    grid-column: 1/12;
  }

  .p-paginator-right-content {
    width:100%;
    justify-content: center;
  }
}
</style>