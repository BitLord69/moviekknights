import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import VueGapi from "vue-gapi";

//Primevue
import PrimeVue from "primevue/config";

import "primeflex/primeflex.css";
import "primeicons/primeicons.css";
import "primevue/resources/primevue.min.css";

const app = createApp(App);
app.use(PrimeVue)
app.use(VueGapi, {
  apiKey: 'AIzaSyCmRdlZoUVPDn5XSskvgSzquBvKfHCccWw',
  clientId: '205088827578-u7c23dj0t8mrlt305c8vdnpjcufgsffb.apps.googleusercontent.com',
  discoveryDocs: ['https://sheets.googleapis.com/$discovery/rest?version=v4'],
  scope: 'https://www.googleapis.com/auth/calendar'
})
app.use(router).mount('#app')
