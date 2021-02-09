import { extFetch } from "@/modules/extFetch";
import { ref } from "vue";

const url = "/rest/movies"
const movies = ref(null);
const moviesByPagination = ref(null);
const movieCount = ref(null);
const movieError = ref(null);
const moviesBySearch = ref(null);

export default function MovieHelper(){

  async function getMovies(){
    try {
      movies.value = await extFetch(url + "/db");
    } catch (err) {
      movieError.value = err;
      return;
    }
  }

  async function getMoviesByPagination(page){
    try {
      moviesByPagination.value = await extFetch(url + "/page/" + page);
    } catch (err) {
      movieError.value = err;
      return;
    }
  }

  async function getMovieCount(){
    try {
      movieCount.value = await extFetch(url + "/count")
    } catch (err) {
      movieError.value = err;
      return;
    }
  }

  async function getMoviesBySearch(searchTerm){
    try {
      moviesBySearch.value = await extFetch(url + "/search/" + searchTerm);
    } catch (err) {
      movieError.value = err;
      return;
    }
  }

  return { getMovies, getMovieCount, getMoviesByPagination, getMoviesBySearch,
     movies, movieCount, moviesByPagination, movieError, moviesBySearch }
}