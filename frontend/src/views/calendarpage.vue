<template>
  <div>
    <Button @click="getCalendar" label="Get calendar"></Button>
    <div v-if="state.events.length > 0">
    <FullCalendar :events="state.events" :options="state.options"/>
    </div>
    <pre>
      {{state.events}}
      <hr/>
      {{events}}
    </pre>
  </div>
</template>

<script>
import { extFetch } from "@/modules/extFetch";
import { ref, reactive } from "vue";
import FullCalendar from 'primevue/fullcalendar';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';


export default {
  components: { FullCalendar },
  setup() {
    const result = ref(null);
    let state = reactive({
      events: [],
      options: {
                plugins:[dayGridPlugin, timeGridPlugin, interactionPlugin],
                initialDate: '2021-02-01',
                headerToolbar: {
                    left: 'prev,next',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                editable: true
            },
    })
    const events = [
				{
					id: 1,
					"title": "All Day Event",
					"start": "2021-02-01"
				},
				{
					"id": 2,
					"title": "Long Event",
					"start": "2021-02-07",
					"end": "2021-02-10"
				},
				{
					"id": 3,
					"title": "Repeating Event",
					"start": "2021-02-09T16:00:00"
				},
				{
					"id": 4,
					"title": "Repeating Event",
					"start": "2021-02-16T16:00:00"
				},
				{
					"id": 5,
					"title": "Conference",
					"start": "2021-02-11",
					"end": "2021-02-13"
				},
				{
					"id": 6,
					"title": "Meeting",
					"start": "2021-02-12T10:30:00",
					"end": "2021-02-12T12:30:00"
				}
			]
    async function getCalendar() {
      result.value = await extFetch("/rest/calendar/freebusy", "GET", undefined, true);
      console.log("Result: ", result.value);
      let index = 1;
      Object.entries(result.value.calendars).forEach( calendar => {
        calendar[1].busy.forEach( event => {
          console.log(event)
        let start = new Date(event.start.value).toLocaleString()
        let end = new Date(event.end.value).toLocaleString()
          state.events.push({ 
            "id": index++, 
            "title": "Busy", 
            "start": start,//.substring(0, start.length - 5), 
            "end": end//.substring(0, end.length - 5)
          })
        })
      });
      console.log("Events: ", state.events);
      console.log("Primevue events: ", events);
    }

    return { state, result, getCalendar, events };
  },
};
</script>

<style></style>
