<template>
  <div v-if="events && events.length > 0" class="calendar">
		<h3>Visa privat kalender</h3>
		<InputSwitch v-model="isPrivate" class="p-mb-4" @click="onClick"/>
		<FullCalendar :events="events" :options="state.options" :key="refreshKey"/>
  </div>
	<div v-else><h1>Här var det tomt...</h1></div>
</template>

<script>
import { extFetch } from "@/modules/extFetch";
import { ref, reactive, watchEffect } from "vue";
import FullCalendar from 'primevue/fullcalendar';
import InputSwitch from 'primevue/inputswitch';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import svLocale from '@fullcalendar/core/locales/sv'
import UserHandler from '@/modules/UserHandler';
import EventHelper from '@/modules/EventHelper';

export default {
components: { FullCalendar, InputSwitch },
  async setup() {
		const {isLoggedIn} = UserHandler();
		const { event, events, createEvent } = EventHelper();

		const result = ref(null);
		const refreshKey = ref(0);
		const isPrivate = ref(false);
	
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
				slotMinTime: "00:00:00",
				firstDay: 1,
				locale: svLocale,
      },
		})
		
		watchEffect(async () => {
			if (isPrivate.value) {
				await getPersonal();
			} else {
				await getFreeBusy();
			}
			refreshKey.value++;
		});

		await getFreeBusy();

		async function getFreeBusy() {
			result.value = await extFetch("/rest/calendar/freebusy", "GET", undefined, true);
			events.length = 0;
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

		async function getPersonal() {
			result.value = await extFetch("/rest/calendar/personal", "GET", undefined, true);
			events.length = 0;
			// privateEvents.length = 0;
			result.value.forEach(event => {
				if (event.status !== 'cancelled') {
					let start = new Date(JSON.parse(JSON.stringify(event.start)).dateTime.value).toLocaleString();				
					let end = new Date(JSON.parse(JSON.stringify(event.end)).dateTime.value).toLocaleString();
					events.push({
						id: event.id,
						title: event.summary,
						start: start,
						end: end,
						editable: false,
					});
				}
			});
		}

		if(event.id){
			events.push(event);
			event.id = null;
		}
		
		function onClick() {

		}

    return { state, result, isLoggedIn, createEvent, events, event, isPrivate, onClick, refreshKey };
  }
}
</script>

<style lang="scss" scoped>
	.calendar {
		display: grid;
		grid-column: 3/11;
	}
</style>