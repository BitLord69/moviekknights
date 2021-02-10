<template>
  <div v-if="events && events.length > 0" class="calendar">
		<div class="p-grid p-jc-center p-ai-center">
			<h3 class="p-m-0 p-col-3">Visa privat kalender: </h3>
			<InputSwitch class="p-col-1" v-model="isPrivate"/>
		</div>
		<FullCalendar :events="events" :options="state.options" :key="refreshKey" />
		<Dialog header="Radera evenemang?" :visible="state.showRemoveDialog" :style="{width: '350px'}" :modal="true">
        <div class="confirmation-content">
          <i class="pi pi-exclamation-triangle p-mr-3" style="font-size: 2rem" />
          <span>Vill du radera valt evenemang?</span>
        </div>
    <template #footer>
        <Button label="Nej" icon="pi pi-times" @click="state.showRemoveDialog = !state.showRemoveDialog;" class="p-button-text"/>
        <Button label="Ja" icon="pi pi-check" @click="doRemoveEvent"
         class="p-button-text" autofocus />
    </template>
      </Dialog>
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
		const { event, events, createEvent, removeEvent } = EventHelper();

		const result = ref(null);
		const refreshKey = ref(0);
		const isPrivate = ref(false);

    let state = reactive({
			showRemoveDialog: false,
			eventToRemove: null,
			updateCalendar: 0,
      options: {
				plugins:[dayGridPlugin, timeGridPlugin, interactionPlugin],
				initialDate: '2021-02-01',
				headerToolbar: {
					left: 'prev,next,today',
					center: 'title',
					right: 'dayGridMonth,timeGridWeek,timeGridDay',
				},
				editable: false,
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
				eventClick: (e) => {
					if(isPrivate.value){
						state.eventToRemove = e.event._def.publicId;
						state.showRemoveDialog = true;
						console.log(e.event._def);
					}
				},
				datesSet: (e) => {
					console.log("Tjosan hejsan, e:", e);
				}
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

		function ourClicker() {
			console.log('Tjosan hejsan!!!');
		}

		async function getFreeBusy() {
			result.value = await extFetch("/rest/calendar/freebusy", "GET", undefined, true);
			events.length = 0;
			state.options.editable = false;
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
			state.options.editable = true;
			result.value.forEach(event => {
				if (event.status !== 'cancelled') {
					let start = new Date(JSON.parse(JSON.stringify(event.start)).dateTime.value).toLocaleString();				
					let end = new Date(JSON.parse(JSON.stringify(event.end)).dateTime.value).toLocaleString();
					events.push({
						id: event.id,
						title: event.summary,
						start: start,
						end: end,
						editable: true,
						classNames:'text-class',
					});
				}
			});
		}

		if(event.id){
			events.push(event);
			event.id = null;
		}
		
	async function doRemoveEvent() {
		removeEvent(state.eventToRemove);
		state.showRemoveDialog = !state.showRemoveDialog;
		await getPersonal();
		refreshKey.value++;
	}

    return { state, result, isLoggedIn, createEvent, events, event, isPrivate, refreshKey, doRemoveEvent, ourClicker };
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/_variables.scss";
	
	.calendar {
		display: grid;
		grid-column: 3/11;
	}

	.p-inputswitch-slider {
		background: $bg-primary;
	}

	::v-deep(.fc-header-toolbar){
		.fc-toolbar-chunk:first-child {
			width:33%;
			margin-right: 0 !important;
		}

		.fc-toolbar-chunk:last-child {
			width:33%;
			display: flex;
			justify-content: flex-end;
			margin-left: 0 !important;
		}
	}

	::v-deep(.fc-event), ::v-deep(.fc-event-main) {
		color: $text-secondary !important;
		border: $border-primary !important;
		background-color: $bg-secondary !important;
	}

	::v-deep(.fc-daygrid-day.fc-day-today) {
		background-color: lighten($bg-secondary, 5%);
	}
</style>