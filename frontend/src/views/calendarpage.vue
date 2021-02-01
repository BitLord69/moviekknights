<template>
    <iframe :src="'https://calendar.google.com/calendar/embed?src=' + email + '&ctz=Europe%2FStockholm'" style="border: 0" width="90%" height="90%" frameborder="0" scrolling="no"></iframe>
</template>

<script>
//import UserHandler from '@/modules/UserHandler';
export default {
    name: "Calendar",
    setup() {
        //const {currentUser} = UserHandler();
        let email = JSON.parse(window.localStorage.getItem("user")).username.replace("@", "%40");

        return {email}
    }

}
</script>

<style lang="scss" scoped>
</style>