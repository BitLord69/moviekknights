<template>
  <div>
    <Button @click="getCalendar" label="Get calendar"></Button>
    <Button @click="createEvent" label="Create event"></Button>
    <div class="calendar-container">
      <div class="calendar" v-if="events.length > 0">
        <FullCalendar :events="events" :options="state.options" />
      </div>
    </div>
  </div>
</template>

<script>
import { extFetch } from "@/modules/extFetch";
import { ref, reactive } from "vue";
import FullCalendar from "primevue/fullcalendar";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import EventHelper from "@/_helpers/EventHelper";

export default {
  components: { FullCalendar },
  setup() {
    const { events, event, createEvent } = EventHelper();
    const result = ref(null);
    let state = reactive({
      options: {
        plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
        initialDate: "2021-02-01",
        headerToolbar: {
          left: "prev,next",
          center: "title",
          right: "dayGridMonth,timeGridWeek,timeGridDay",
        },
        editable: true,
        handleWindowResize: true,
        contentHeight: "auto",
        height: "auto",
        eventOverlap: false,
        eventDrop: (e) => {
          event.start = e.event.start;
          event.end = e.event.end;
        },
      },
    });

    async function getCalendar() {
      result.value = await extFetch(
        "/rest/calendar/freebusy",
        "GET",
        undefined,
        true
      );
      Object.entries(result.value.calendars).forEach((calendar) => {
        calendar[1].busy.forEach((event) => {
          let start = new Date(event.start.value).toLocaleString();
          let end = new Date(event.end.value).toLocaleString();
          events.push({
            id: events.length + 1,
            title: "Busy",
            start: start,
            end: end,
            editable: false,
          });
        });
      });
    }

    return { state, result, events, getCalendar, createEvent };
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
