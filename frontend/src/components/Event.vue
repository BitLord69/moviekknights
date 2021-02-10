<template>
  <div class="event-form p-formgrid p-grid nested-grid p-jc-center" id="event">
    <div class="close-button" @click="showEventForm = !showEventForm">X</div>
    <div class="p-sm-12 p-grid">
      <div class="p-sm-12"><h2>Boka filmkväll</h2></div>
    </div>
    <div class="p-sm-12">
      <Dropdown
        v-model="state.event"
        :options="eventsToBook"
        optionLabel="title"
        placeholder="Välj en film att boka"
        appendTo="body"
      />
    </div>
    <Dialog
      header="Dubbelbokning"
      :visible="state.displayConfirmation"
      :style="{ width: '350px' }"
      :modal="true"
    >
      <div class="confirmation-content">
        <i class="pi pi-exclamation-triangle p-mr-3" style="font-size: 2rem" />
        <span>Du håller på att dubbelboka. Vill du fortsätta?</span>
      </div>
      <template #footer>
        <Button
          label="Nej"
          icon="pi pi-times"
          @click="toggleConfirmation()"
          class="p-button-text"
        />
        <Button
          label="Ja"
          icon="pi pi-check"
          @click="
            createEvent(state.event);
            toggleConfirmation();
            showEventForm = !showEventForm;
          "
          class="p-button-text"
          autofocus
        />
      </template>
    </Dialog>
    <div class="p-grid p-jc-center" v-if="state.event">
      <div class="p-sm-12" v-if="state.event">
        Starttid:
        <PrimeCalendar
          v-model="state.event.start"
          dateFormat="dd/mm/yy"
          :showIcon="true"
          :showTime="true"
          :baseZIndex=15
        />
      </div>
      <div class="p-sm-12" v-if="state.event">
        Sluttid: {{ getEndTime(state.event) }}
      </div>
      <Button @click="checkBusy" label="Bekräfta evenemang"></Button>
    </div>
  </div>
</template>

<script>
import EventHelper from "@/modules/EventHelper";
import moment from "moment";
import { reactive } from "vue";
export default {
  name: "Event",
  setup() {
    const state = reactive({
      event: null,
      displayConfirmation: false,
    });
    const { eventsToBook, showEventForm, events, createEvent } = EventHelper();

    function getEndTime(event) {
      state.event.end = new Date(
        event.start.getTime() + event.duration * 60000
      ).toLocaleString();
      return new Date(
        event.start.getTime() + event.duration * 60000
      ).toLocaleTimeString();
    }

    function checkBusy() {
      let isBusy = false;
      let start = moment(state.event.start);
      let end = moment(state.event.end);
      events.forEach((e) => {
        let eStart = moment(e.start);
        let eEnd = moment(e.end);
        if (
          start.isBetween(eStart, eEnd) ||
          end.isBetween(eStart, eEnd) ||
          eStart.isBetween(start, end) ||
          eEnd.isBetween(start, end)
        ) {
          isBusy = true;
        }
      });

      if (isBusy) toggleConfirmation();
      else {
        createEvent(state.event);
        showEventForm.value = !showEventForm.value;
      }
    }

    function toggleConfirmation() {
      state.displayConfirmation = !state.displayConfirmation;
    }

    return {
      state,
      eventsToBook,
      showEventForm,
      getEndTime,
      checkBusy,
      toggleConfirmation,
      createEvent,
    };
  },
};
</script>

<style lang="scss" scope>
@import "@/styles/_variables.scss";
.event-form {
  position: relative;
  border: $border-primary;
  border-radius: $border-radius;
  background-color: $bg-primary;
  padding: 10px;
}

.close-button {
  position: absolute;
  font-size: 1.2rem;
  width: 2rem;
  height: 2rem;
  top: 35px;
  right: 25px;
  cursor: pointer;
  color: $text-secondary;
  border: $border-primary;
  border-radius: $border-radius;
  background-color: $bg-secondary;
  &:hover {
    border: $border-hover;
  }
}

.p-dropdown {
  width: 100%;
}

.text-overflow {
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
