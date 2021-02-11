import { reactive, ref }  from "vue";
import { extFetch } from "./extFetch";


const event = reactive({
  id: null,
  title: null,
  start: null,
  end: null,
  booked: false,
});

const showEventForm = ref(false);
const eventsToBook = reactive([]);
const events = reactive([]);

export default function EventHelper(){

  function addEventToCalendar(movie){
    let newDate = new Date();
    newDate.toISOString();

    event.id = events.length + 1; 
    event.title = movie.title;
    event.start = newDate;
    event.end = new Date(event.start.getTime() + movie.runTime * 60000);
    eventsToBook.push({...event, duration: movie.runTime});
    showEventForm.value = true; 
  }

  async function createEvent(newEvent) {
    let newDate = new Date(newEvent.end);
    newEvent.end = newDate;
    await extFetch("rest/calendar/add", "POST", newEvent, true)
    event.booked = true;
    let index = eventsToBook.indexOf(newEvent);
    eventsToBook.splice(index, 1);
  }

  async function removeEvent(eventToRemove) {
    await extFetch("rest/calendar/delete/" + eventToRemove, "DELETE", undefined, true)
  }



  return { event, events, eventsToBook, showEventForm, addEventToCalendar, createEvent, removeEvent }
}