import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/startpage.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/about",
    name: "About",
    component: () =>
      import("../views/aboutpage.vue"),
  },
  {

    path: "/logmein",
    name: "LogMeIn",
    component: () =>
      import("../views/LogMeIn.vue"),
  },
  {
    path: "/logmeout",
    name: "LogMeOut",
    component: () =>
      import("../views/LogMeOut.vue"),
  },
  {
    path: "/calendar",
    name: "Calendar",
    component: () =>
      import("../views/calendarpage.vue"),
  },
  {
    path: "/movies",
    name: "Movies",
    component: () =>
      import("../views/moviepage.vue"),

  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
