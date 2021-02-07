import { reactive }  from "vue";
import { extFetch } from "./extFetch";


const event = reactive({
  id: null,
  title: null,
  start: null,
  end: null,
  booked: false,
});

const events = reactive([]);

export default function EventHelper(){

  function addEventToCalendar(movie){
    let newDate = new Date();
    newDate.toISOString();

    event.id = events.length + 1; 
    event.title = movie.title;
    event.start = newDate;
    event.end = new Date(event.start.getTime() + movie.runTime * 60000);
  }

  async function createEvent() {
    await extFetch("rest/calendar/add", "POST", event, true)
    event.booked = true;
  }

  return { event, events, addEventToCalendar, createEvent }
}