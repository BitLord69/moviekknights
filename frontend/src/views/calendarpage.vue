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
import {useRouter} from 'vue-router'
import Calendar from "@/components/Calendar"
import UserHandler from '@/modules/UserHandler'

export default {
  components: { Calendar },
  setup() {
    const router = useRouter();
    const {isLoggedIn} = UserHandler();
    
    if(!isLoggedIn.value) {
      router.push("/")
    }

    return { }
  },
};
</script>

<style lang="scss" scope>
  .calendar-container {
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    justify-content: center;
  }
</style>
