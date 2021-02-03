<template>
  <div>
    <Button @click="getCalendar" label="Get calendar"></Button>
    <div class="calendar-container">
      <div class="calendar" v-if="events.length > 0">
        <FullCalendar :events="events" :options="state.options"/>
      </div>
    </div>
  </div>
</template>

<script>
import { extFetch } from "@/modules/extFetch";
import { ref, reactive } from "vue";
import FullCalendar from 'primevue/fullcalendar';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import EventHelper from "@/_helpers/EventHelper"

export default {
  components: { FullCalendar },
  setup() {
    const { events } = EventHelper();
    const result = ref(null);
    let state = reactive({
      options: {
                plugins:[dayGridPlugin, timeGridPlugin, interactionPlugin],
                initialDate: '2021-02-01',
                headerToolbar: {
                    left: 'prev,next',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                editable: true,
                handleWindowResize: true,
                contentHeight: 'auto',
                height: 'auto',
                eventOverlap: false,
            },
    })

    async function getCalendar() {
      result.value = await extFetch("/rest/calendar/freebusy", "GET", undefined, true);
      let index = 1;
      Object.entries(result.value.calendars).forEach( calendar => {
        calendar[1].busy.forEach( event => {
        let start = new Date(event.start.value).toLocaleString()
        let end = new Date(event.end.value).toLocaleString()
          events.push({ 
            "id": index++, 
            "title": "Busy", 
            "start": start,
            "end": end
          })
        })
      });
    }

    return { state, result, events, getCalendar };
  },
};
</script>

<style lang="scss" scoped>

.calendar-container {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  justify-content: center;
  .calendar {
    display: grid;
    grid-column: 3/11;
  }
}
</style>
