<template>
  <div class="movies">
    <div>Antal filmer i db: {{movieCount}}</div>
    
    <Paginator v-model:first="state.first" :rows="18" :totalRecords="movieCount" class="paginator"
      template="FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
      currentPageReportTemplate="({currentPage} av {totalPages})">
      <template #left>
        &nbsp;
        <!-- <Button type="button" icon="pi pi-refresh" @click="reset()"/> -->
      </template>
      <template #right>
        <AutoComplete v-model="state.searchTerm" :suggestions="state.filteredMovies" @complete="searchMovie($event)" field="title" />
      </template>
    </Paginator>
    
    <div id="movie-page">
        <Movie :movie="movie" v-for="(movie, index) in state.pagMovies && state.pagMovies.slice(state.first, state.first+18)" :key="index" @click="displayMovieInfo(movie)"/>
    </div>
        <MovieInfoModal v-if="state.showMovieInfo" :movie="state.selectedMovie"/>
        <PersonInfoModal v-if="state.showPersonInfo" :person="state.selectedPerson" :movies="movies" :movieListChosen="state.movieListChosen" />
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
    const { getMovies, getMovieCount, movies, movieCount, movieError } = MovieHelper();
    let state = reactive({
      first: 0,
      pagMovies: movies,
      showMovieInfo: false,
      selectedMovie: null,
      searchTerm: '',
      filteredMovies: movies.value,
      selectedPerson: null,
      movieListChosen: "",
    })

    watchEffect(() => {
      if (typeof state.searchTerm === 'object') {
        state.selectedMovie = state.searchTerm;
        state.showMovieInfo = true;
      }
    })

    onMounted(async () => {
      await getMovieCount();
      await getMovies();
    })

    function reset() {
      state.first = 0;
    }

    function displayMovieInfo(movie) {
      state.showMovieInfo = !state.showMovieInfo;
      state.selectedMovie = movie
    }

    function searchMovie(event) {
      if (!event.query.trim().length) {
        state.filteredMovies = movies.value;
      }
      else {
        state.filteredMovies = movies.value.filter((movie) => {
            return movie.title.toLowerCase().startsWith(event.query.toLowerCase()) || movie.originalTitle.toLowerCase().startsWith(event.query.toLowerCase());
        });
      }
    }

    return { state, movies, movieCount, movieError, displayMovieInfo, reset, searchMovie}
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
</style>