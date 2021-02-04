import { reactive }  from "vue";
import { extFetch } from "../modules/extFetch";


const event = reactive({
  id: null,
  title: null,
  start: null,
  end: null,
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

    events.push(event);
  }

  async function createEvent() {
    await extFetch("rest/calendar/add", "POST", event, true)
  }

  return { event, events, addEventToCalendar, createEvent }
}