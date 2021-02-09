import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/startpage.vue";
import UserHandler from '@/modules/UserHandler'
const { isLoggedIn, isTokenValid, logout, showToast } = UserHandler();

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
    path: "/movies",
    name: "Movies",
    component: () =>
      import("../views/moviepage.vue"),
  },
  {
    path: "/calendar",
    name: "Calendar",
    component: () =>
      import("../views/calendarpage.vue"),
    beforeEnter: (to, from, next) => {
      let token = isTokenValid();
      if(isLoggedIn.value && token) {
        next();
      } else {
        if(!token){
          logout();
          showToast.value = true;
        }
        next("/");
      }
    }
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
