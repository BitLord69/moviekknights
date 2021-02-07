<template>
  <div class="event-form p-formgrid p-grid nested-grid p-jc-center" id="event">
    <div class="close-button" @click="showEventForm = !showEventForm">X</div>
    <div class="p-sm-12 p-grid">
      <div class="p-sm-12"><h2>Boka filmkväll</h2></div> 
    </div>
    <div class="p-sm-12">
      <Dropdown v-model="state.event" :options="eventsToBook" optionLabel="title" placeholder="Välj en film att boka" appendTo="body" />
    </div>
    <div class="p-grid p-jc-center" v-if="state.event">
      <div class="p-sm-12" v-if="state.event">
        Starttid: <PrimeCalendar v-model='state.event.start' dateFormat="dd/mm/yy" :showIcon='true' :showTime='true'  baseZIndex='15' />
      </div>    
      <div class="p-sm-12" v-if="state.event">
        Sluttid: {{getEndTime(state.event)}}
      </div>
      <Button label="Bekräfta evenemang"></Button>
    </div>

  </div>
</template>

<script>
import EventHelper from "@/modules/EventHelper"
import { reactive } from "vue";
export default {
  name: "Event",
  setup() {
    const state = reactive({
      event: null,
    })
    const { eventsToBook, showEventForm } = EventHelper();
    
    function getEndTime(event){
      return new Date(event.start.getTime() + event.duration*60000).toLocaleTimeString();
    }
    return { state, eventsToBook, showEventForm, getEndTime }
  }
}
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
    top: 20px;
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

  .p-dropdown{
    width: 100%;
  }

  .text-overflow{
    max-width: 100%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
</style>