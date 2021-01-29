import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import VueGapi from "vue-gapi";
import PrimeVue from 'primevue/config';
import 'primevue/resources/themes/saga-blue/theme.css';       
import 'primevue/resources/primevue.min.css';                 
import 'primeicons/primeicons.css';                           
import 'primeflex/primeflex.css';

import Button from "primevue/button";

const app = createApp(App);
app.use(VueGapi, {
  apiKey: 'AIzaSyCmRdlZoUVPDn5XSskvgSzquBvKfHCccWw',
  clientId: '205088827578-u7c23dj0t8mrlt305c8vdnpjcufgsffb.apps.googleusercontent.com',
  discoveryDocs: ['https://sheets.googleapis.com/$discovery/rest?version=v4'],
  scope: 'https://www.googleapis.com/auth/calendar'
})
app.use(PrimeVue, { ripple: true });
app.use(router);
app.component("Button", Button);

app.mount('#app')

