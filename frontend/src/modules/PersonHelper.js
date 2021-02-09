import { extFetch } from "@/modules/extFetch";
import { ref } from "vue";

const url = "/rest/person"
const moviesDirectedBy = ref(null);
const moviesComposedBy = ref(null);
const moviesActedIn = ref(null);
const personError = ref(null);

export default function PersonHelper(){

  async function getMoviesDirectedByPerson(id){
    try {
        moviesDirectedBy.value = await extFetch(url + "/director/" + id);
    } catch (err) {
        personError.value = err;
      return;
    }
  }

  async function getMoviesComposedByPerson(id){
    try {
        moviesComposedBy.value = await extFetch(url + "/composer/" + id);
    } catch (err) {
        personError.value = err;
      return;
    }
  }

  async function getMoviesPersonActedIn(id){
    try {
        moviesActedIn.value = await extFetch(url + "/composer/" + id);
    } catch (err) {
        personError.value = err;
      return;
    }
  }

  return { getMoviesDirectedByPerson, getMoviesComposedByPerson, getMoviesPersonActedIn, moviesDirectedBy, moviesComposedBy, moviesActedIn }
}