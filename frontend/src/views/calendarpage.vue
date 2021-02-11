<template>
  <div class="calendar-container">
    <Suspense>
      <template #default>
        <Calendar />
      </template>
      <template #fallback>
        <h1>Laddar kalendern</h1>
      </template>
    </Suspense>
  </div>
</template>

<script>

import Calendar from "@/components/Calendar"

export default {
  components: { Calendar },
  
};
</script>

<style lang="scss" scope>
  @import "@/styles/_variables.scss";
  .calendar-container {
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    justify-content: center;
  }

  .event-container{
    position: absolute;
    width: 25%;
    z-index: 10;
    top: 50px;
    left: 50px;
  }

  .showEventForm{
    position: absolute;
    color: $text-secondary;
    font-size: 2rem;
    width: 100px;
    height: 50px;
    cursor: pointer;
    border: $border-primary;
    background-color: $bg-secondary;
    border-radius: $border-radius;
    &:hover {
      border: $border-hover;
    }
  }
</style>
