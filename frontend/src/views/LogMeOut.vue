
<script>

import UserHandler from "@/modules/UserHandler";
import { useRouter } from "vue-router";

export default {

setup() {

  const router = useRouter();
  const { logout } = UserHandler();
  logout();
  router.push('/');
}

}
</script>

<style>

</style>