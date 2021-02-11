<template>
  <div class="calendar">
    <div class="p-grid p-jc-center p-ai-center">
      <h3 class="p-m-0 p-col-3">Visa privat kalender:</h3>
      <InputSwitch class="p-col-1" v-model="isPrivate" />
    </div>
    <teleport to="#app">
      <div class="event-container" v-if="showEventForm">
        <Suspense>
          <template #default>
            <Event />
          </template>
          <template #fallback>
            <h1>Laddar evenemang</h1>
          </template>
        </Suspense>
      </div>
    </teleport>
    <FullCalendar
      :events="events"
      :options="state.options"
      :key="refreshKey"
      :custom-buttons="
        eventsToBook.length > 0 ? state.options.customButtons : {}
      "
    />
    <Dialog
      header="Radera evenemang?"
      :visible="state.showRemoveDialog"
      :style="{ width: '350px' }"
      :modal="true"
    >
      <div class="confirmation-content">
        <i class="pi pi-exclamation-triangle p-mr-3" style="font-size: 2rem" />
        <span>Vill du radera valt evenemang?</span>
      </div>
      <template #footer>
        <Button
          label="Nej"
          icon="pi pi-times"
          @click="state.showRemoveDialog = !state.showRemoveDialog"
          class="p-button-text"
        />
        <Button
          label="Ja"
          icon="pi pi-check"
          @click="doRemoveEvent"
          class="p-button-text"
          autofocus
        />
      </template>
    </Dialog>
  </div>
</template>

<script>
import { extFetch } from "@/modules/extFetch";
import { ref, reactive, watch, onMounted } from "vue";
import FullCalendar from "primevue/fullcalendar";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import svLocale from "@fullcalendar/core/locales/sv";
import Event from "@/components/Event";
import UserHandler from "@/modules/UserHandler";
import EventHelper from "@/modules/EventHelper";
import moment from "moment";

export default {
  components: { FullCalendar, Event },
  async setup() {
    const { isLoggedIn } = UserHandler();
    const {
      event,
      events,
      eventsToBook,
      createEvent,
      removeEvent,
      showEventForm,
    } = EventHelper();

    const result = ref(null);
    const refreshKey = ref(0);
    const isPrivate = ref(false);
    let dateSpan = reactive({
      startOfMonth: null,
      endOfMonth: null,
    });

    const defaultView = ref("dayGridMonth");

    let state = reactive({
      showRemoveDialog: false,
      eventToRemove: null,
      calendar: null,
      refresh: false,
      options: {
        plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
        initialDate: moment()
          .startOf("month")
          .format("yyyy-MM-DD"),
        initialView: defaultView.value,
        headerToolbar: {
          left:
            eventsToBook.length > 0
              ? "prev,next,today,bookEventButton"
              : "prev,next,today",
          center: "title",
          right: "dayGridMonth,timeGridWeek,timeGridDay",
        },
        editable: false,
        height: "auto",
        contentHeight: "auto",
        handleWindowResize: true,
        eventOverlap: false,
        eventDrop: (e) => {
          event.start = e.event.start;
          event.end = e.event.end;
        },
        slotMinTime: "00:00:00",
        firstDay: 1,
        locale: svLocale,
        showNonCurrentDates: false,
        fixedWeekCount: false,
        eventClick: (e) => {
          if (isPrivate.value) {
            state.eventToRemove = e.event._def.publicId;
            state.showRemoveDialog = true;
          }
        },
        datesSet: async (e) => {
          dateSpan.startOfMonth = e.startStr;
          dateSpan.endOfMonth = e.endStr;
          defaultView.value = e.view.type;
        },
        customButtons: {
          bookEventButton: {
            text: "Boka!",
            click: function() {
              if (eventsToBook.length == 0) return;
              showEventForm.value = true;
            },
          },
        },
      },
    });

    watch(isPrivate, async () => {
      if (isPrivate.value) {
        await getPersonal();
      } else {
        await getFreeBusy();
      }
      refreshKey.value++;
    });

    watch(dateSpan, async (cur) => {
      if (isPrivate.value) {
        await getPersonal();
      } else {
        await getFreeBusy();
      }

      refreshKey.value++;
      state.options.initialDate = cur.startOfMonth;
      state.options.initialView = defaultView.value;
    });

    onMounted(() => {
      refreshKey.value++;
    });

    async function getFreeBusy() {
      if (dateSpan.startOfMonth) {
        result.value = await extFetch(
          "/rest/calendar/freebusy/" +
            dateSpan.startOfMonth +
            "/" +
            dateSpan.endOfMonth,
          "GET",
          undefined,
          true
        );
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
    }

    async function getPersonal() {
      result.value = await extFetch(
        "/rest/calendar/personal/" +
          dateSpan.startOfMonth +
          "/" +
          dateSpan.endOfMonth,
        "GET",
        undefined,
        true
      );
      events.length = 0;
      state.options.editable = true;
      result.value.forEach((event) => {
        if (event.status !== "cancelled") {
          let start = new Date(
            JSON.parse(JSON.stringify(event.start)).dateTime.value
          ).toLocaleString();
          let end = new Date(
            JSON.parse(JSON.stringify(event.end)).dateTime.value
          ).toLocaleString();
          events.push({
            id: event.id,
            title: event.summary,
            start: start,
            end: end,
            editable: true,
            classNames: "text-class",
          });
        }
      });
    }

    if (event.id) {
      events.push(event);
      event.id = null;
    }

    async function doRemoveEvent() {
      removeEvent(state.eventToRemove);
      state.showRemoveDialog = !state.showRemoveDialog;
      await getPersonal();
      refreshKey.value++;
    }

    return {
      state,
      result,
      isLoggedIn,
      createEvent,
      events,
      eventsToBook,
      event,
      isPrivate,
      refreshKey,
      doRemoveEvent,
      showEventForm,
    };
  },
};
</script>

<style lang="scss" scoped>
@import "@/styles/_variables.scss";

.calendar {
  display: grid;
  grid-column: 3/11;
}

::v-deep(.fc-header-toolbar) {
  .fc-toolbar-chunk:first-child {
    width: 33%;
    margin-right: 0 !important;
  }

  .fc-toolbar-chunk:last-child {
    width: 33%;
    display: flex;
    justify-content: flex-end;
    margin-left: 0 !important;
  }
}

::v-deep(.fc-event),
::v-deep(.fc-event-main) {
  color: $text-secondary !important;
  border: $border-primary !important;
  background-color: $bg-secondary !important;
}

::v-deep(.fc-daygrid-day.fc-day-today) {
  background-color: lighten($bg-secondary, 5%);
}
</style>
