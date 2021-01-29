import { extFetch } from "@/modules/extFetch";
import { ref } from "vue";

const url = "/rest/movies"
const movies = ref(null);
const movieCount = ref(null);
const movieError = ref(null);

export default function MovieHelper(){

  async function getMovies(){
    try {
      movies.value = await extFetch(url + "/");
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

  return { getMovies, getMovieCount, movies, movieCount, movieError }
}