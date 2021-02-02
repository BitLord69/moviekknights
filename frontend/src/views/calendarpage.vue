<template>
  <div>
    <Button @click="getCalendar" label="Get calendar"></Button>
    {{result}}
  </div>
</template>

<script>
import { extFetch } from "@/modules/extFetch";
import { ref } from "vue";


export default {
  setup() {
    const result = ref(null);

    async function getCalendar() {
      result.value = await extFetch("/rest/calendar/freebusy", "GET", undefined, true);
    }

    return { result, getCalendar};
  },
};
</script>

<style></style>
