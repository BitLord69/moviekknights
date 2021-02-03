import { reactive }  from "vue";


const event = reactive({
  movie: null,
  startDateTime: null,
  endDateTime: null,
});

const events = reactive([]);

export default function EventHelper(){

  function createEvent(movie){
    event.movie = movie;
    console.log("Event in EventHelper:", event);
  }

  return { event, events, createEvent}
}