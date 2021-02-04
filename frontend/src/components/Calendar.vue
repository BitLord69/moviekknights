<template>
  <div v-if="isLoggedIn && state.events.length > 0">
		<FullCalendar :events="state.events" :options="state.options"/>
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
import UserHandler from '@/modules/UserHandler'
export default {
components: { FullCalendar },
  async setup() {
		const {isLoggedIn} = UserHandler();
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
		
		if(!isLoggedIn.value) return

		result.value = await extFetch("/rest/calendar/freebusy", "GET", undefined, true);
		let index = 1;
		Object.entries(result.value.calendars).forEach( calendar => {
			calendar[1].busy.forEach( event => {
			let start = new Date(event.start.value).toLocaleString()
			let end = new Date(event.end.value).toLocaleString()
				state.events.push({ 
					"id": index++, 
					"title": "Busy", 
					"start": start,
					"end": end
				})
			})
		});

    return { state, result, isLoggedIn };
  }
}
</script>

<style>

</style>