<template>
  <div v-if="isLoggedIn && events && events.length > 0" class="calendar">
		<FullCalendar :events="events" :options="state.options"/>
  </div>
	<div v-else><h1>Här var det tomt...</h1></div>
</template>

<script>
import { extFetch } from "@/modules/extFetch";
import { ref, reactive } from "vue";
import FullCalendar from 'primevue/fullcalendar';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import svLocale from '@fullcalendar/core/locales/sv'
import UserHandler from '@/modules/UserHandler';
import EventHelper from '@/modules/EventHelper';

export default {
components: { FullCalendar },
  async setup() {
		const { event, events, createEvent } = EventHelper();
		const {isLoggedIn} = UserHandler();
		const result = ref(null);
		
    let state = reactive({
			updateCalendar: 0,
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
				contentHeight: "auto",
				height: "auto",
				eventOverlap: false,
				eventDrop: (e) => {
					event.start = e.event.start;
					event.end = e.event.end;
				},
				slotMinTime: "08:00:00",
				firstDay: 1,
				locale: svLocale,
      },
		})
		
		if(!isLoggedIn.value) return
		events.length = 0;
		result.value = await extFetch("/rest/calendar/freebusy", "GET", undefined, true);
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
		
		if(event.id){
			events.push(event);
			event.id = null;
		}
		

    return { state, result, isLoggedIn, createEvent, events, event };
  }
}
</script>

<style lang="scss" scoped>
	.calendar {
		display: grid;
		grid-column: 3/11;
	}
</style>