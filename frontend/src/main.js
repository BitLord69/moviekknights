import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import VueGapi from "vue-gapi";
import ToastService from 'primevue/toastservice';

//Primevue
import PrimeVue from "primevue/config";
import Paginator from 'primevue/paginator';
import Button from 'primevue/button';
import Toast from 'primevue/toast';
import Dropdown from 'primevue/dropdown';
import Calendar from 'primevue/calendar';
import Dialog from 'primevue/dialog';
import InputSwitch from "primevue/inputswitch";

import "primeflex/primeflex.css";
import "primeicons/primeicons.css";
import "primevue/resources/primevue.min.css";
import 'primevue/resources/themes/arya-blue/theme.css';

const app = createApp(App);
app.use(VueGapi, {
  apiKey: 'AIzaSyCmRdlZoUVPDn5XSskvgSzquBvKfHCccWw',
  clientId: '205088827578-u7c23dj0t8mrlt305c8vdnpjcufgsffb.apps.googleusercontent.com',
  discoveryDocs: ['https://sheets.googleapis.com/$discovery/rest?version=v4'],
  scope: 'https://www.googleapis.com/auth/calendar'
})
app.use(PrimeVue, { ripple: true, locale: {
  dayNamesMin: ["sö", "må", "ti", "on", "to", "fr", "lö"],
  monthNames: ["januari", "februari", "mars", "april", "maj", "juni", "juli", "augusti", "september", "oktober", "november", "december"],
  firstDayOfWeek: 1,
} });
app.use(router);
app.use(ToastService);
app.component("Paginator", Paginator);
app.component("Button", Button);
app.component("Toast", Toast);
app.component("Dropdown", Dropdown);
app.component("PrimeCalendar", Calendar);
app.component("Dialog", Dialog);
app.component("InputSwitch", InputSwitch);

app.mount('#app')

