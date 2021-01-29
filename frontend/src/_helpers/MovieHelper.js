import { extFetch } from "./extFetch";
import { ref } from "vue";


const movies = ref(null);
const movieError = ref(null);

export default function MovieHelper(){

  async function getMovies(){
    try {
      movies.value = await extFetch("/rest/movies/");
    } catch (err) {
      movieError.value = err;
      return;
    }
  }


  return { getMovies, movies, movieError }
}