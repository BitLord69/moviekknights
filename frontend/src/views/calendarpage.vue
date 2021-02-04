<template>
  <div>
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
import Calendar from '@/components/Calendar'
import {useRouter} from 'vue-router'
import UserHandler from '@/modules/UserHandler'
export default {
  components: { Calendar },
    setup() {
      const router = useRouter();
      const {isLoggedIn} = UserHandler();

      if(!isLoggedIn.value) {
        router.push("/")
      }

      return {}
    }
};
</script>

<style></style>
