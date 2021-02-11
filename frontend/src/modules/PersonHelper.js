import { extFetch } from "@/modules/extFetch";
import { ref } from "vue";

const url = "/rest/person"
const moviesDirectedBy = ref(null);
const moviesComposedBy = ref(null);
const moviesActedIn = ref(null);
const personCount = ref(null);
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
        moviesActedIn.value = await extFetch(url + "/actor/" + id);
    } catch (err) {
        personError.value = err;
      return;
    }
  }

  async function getPersonCount(){
    try {
        personCount.value = await extFetch(url + "/count");
    } catch (err) {
        personError.value = err;
      return;
    }
  }

  return { getMoviesDirectedByPerson, getMoviesComposedByPerson, getMoviesPersonActedIn, getPersonCount, moviesDirectedBy, moviesComposedBy, moviesActedIn, personCount }
}