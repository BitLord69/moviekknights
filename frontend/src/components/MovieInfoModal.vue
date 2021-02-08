<template>
  <div class="overlay">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <Button @click="addEventToCalendar(movie)">Skapa event</Button>
          <Button icon="pi pi-times" @click="$parent.state.showMovieInfo = false" />
        </div>
        <div class="modal-body" :style="{backgroundImage: `url(${movie.backdropPath != null ? movie.backdropPath : '/img/noimagebackdrop.png'})`}">
          <div class="poster">
            <img :src="movie.posterPath != null ? movie.posterPath : '/img/noimage.png'" />
          </div>
          <div class="info">
            <h3 class="p-m-0">{{movie.title}} ({{movie.releaseDate.slice(0, 4)}})</h3>
            <span v-if="movie.title.toLowerCase() != movie.originalTitle.toLowerCase()"><em>{{movie.originalTitle}}</em></span>
            <hr style="" />
            <span>Speltid: {{time()}}</span>
            <span>Genre(s): {{movie.genres.map(g => g.name ).join(', ')}}</span>
            <span>
              Regisserad av:
                <span v-for="(director, index) in movie.directors" :key="index">
                  <span class="crewLink" @click="$parent.state.showPersonInfo = true;
                  $parent.state.selectedPerson = director; $parent.state.movieListChosen = 'directing'">
                    {{director.name}}
                  </span>
                  <span v-if="index + 1 < movie.directors.length">, </span>
                </span>
            </span>
            <span>
              Kompositör(er):
                <span v-for="(composer, index) in movie.composers" :key="index">
                  <span class="crewLink" @click="$parent.state.showPersonInfo = true;
                  $parent.state.selectedPerson = composer; $parent.state.movieListChosen = 'composing'">
                    {{composer.name}} 
                  </span>
                  <span v-if="index + 1 < movie.composers.length">, </span>
                </span>
            </span>
          </div>
          <div class="overview">
            <div>
              <div v-if="movie.tagline.length > 0"><em>"{{movie.tagline}}"</em></div>
              <div v-if="state.overview.length > 3">{{state.overview}} <span class="showMoreText" @click="toggleShowText()">{{state.showMoreText}}</span></div>
            </div>
          </div>
          <div class="cast">
            <div>
              <Paginator v-model:first="state.first" :rows="5" :totalRecords="state.castCount"
              template="PrevPageLink NextPageLink">
                <template #left>
                  <span class="p-pl-2">Skådespelare:</span>
                </template>
              </Paginator>
            </div>
            <div style="display: flex">
              <div class="characterBody" v-for="(cast, index) in displayCast().slice(state.first, state.first+5)" :key="index" @click="$parent.state.showPersonInfo = true;
              $parent.state.selectedPerson = cast.person; $parent.state.movieListChosen = 'acting'">
                <img :src="cast.person.profileImgPath != null ? cast.person.profileImgPath : '/img/noimage.png'" />
                <span><strong>{{cast.person.name}}</strong></span>
                <span>{{cast.character}}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive } from 'vue';
import EventHelper from "@/modules/EventHelper"
export default {
  name: 'MovieInfoModal',
  props: {movie: Object, showMovieInfo: Boolean},
  setup(props){
    const { addEventToCalendar } = EventHelper();
    const state = reactive({
      showMore: false,
      showPersonInfo: false,
      selectedPerson: null,
      showMoreText: "[läs mer...]",
      overview: props.movie && props.movie.overview.slice(0, 150) + "...",
      first: 0,
      castCount: props.movie && props.movie.cast.length
    })

    function toggleShowText() {
      state.showMore = !state.showMore
      state.showMoreText = state.showMore ? "[läs mindre...]" : "[läs mer...]"
      state.overview = state.showMore ?props.movie && props.movie.overview : props.movie && props.movie.overview.slice(0, 150) + "..."
    }
    
    function displayCast() {
      let cast = props.movie.cast
      return cast.sort((a, b) => a.order - b.order)
    }

    function time() {
      if(props.movie.runTime % 60 == 0) {
        return props.movie.runTime / 60 + " h"
      }
      else if(props.movie.runTime < 60) {
        return props.movie.runTime + " min"
      }
      else {
        let h = parseInt(props.movie.runTime / 60)
        let min = parseInt(props.movie.runTime % 60)
        return h + " h " + min + " min"
      }
    }

    function displayPersonInfo(person) {
      state.showPersonInfo = !state.showPersonInfo;
      state.selectedPerson = person
    }
    
    return { state, time, displayCast, toggleShowText, addEventToCalendar, displayPersonInfo }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/_variables.scss";

.modal-header {
  justify-content: space-between;
}

.modal-body {
  height: 90%;
  grid-template-rows: 315px auto auto;
  grid-template-areas:
    "poster info"
    "overview overview"
    "cast cast";
  position: relative;
  background-size: cover;
  background-position: center center;
  background-attachment: fixed;
  background-repeat: no-repeat;
}

.modal-body::before {
    content: "";
    position: absolute;
    top: 0px;
    right: 0px;
    bottom: 0px;
    left: 0px;
    background-color: $bg-secondary;
    opacity: 0.9;
}

.overview {
  grid-area: overview;
  z-index: 2;
}
.cast {
  grid-area: cast;
  display: flex;
  z-index: 2;
  flex-direction: column;
  margin-top: 15px;
  .p-paginator {
    justify-content: flex-end !important;
    padding: 0 !important;
  }
  
  .characterBody {
    display: flex;
    flex-direction: column;
    margin-top: 15px;
    width: 20%;
    text-align: center !important;
    cursor: pointer;
    &:hover img{
      border: $border-hover;
      box-shadow: $boxshadow;
    }
  }
}

.characterBody img {
  align-self: center;
  object-fit: cover;
  object-position: 100% 50%;
  width: 90%;
  height: 130px;
  border-radius: 50%;
  border: $border-primary;
}

@media only screen and (max-width: 800px) {
  .characterBody img {
    height: 70px;
  }

  .modal-body {
    grid-template-rows: 255px auto auto auto;
    grid-template-areas:
      "poster poster"
      "info info"
      "overview overview"
      "cast cast";
  }
}
</style>