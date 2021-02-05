<template>
  <div class="overlay">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <Button>Skapa event</Button>
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
            <span>Regisserad av: {{movie.directors.map(d => d.name ).join(', ')}}</span>
            <span>Kompositör(er): {{movie.composers.map(c => c.name ).join(', ')}}</span>
            <span>Skådespelare: {{displayCast()}}</span>
          </div>
          <div class="overview">
            <div>
              <div v-if="movie.tagline.length > 0"><em>"{{movie.tagline}}"</em></div>
              <div>{{state.overview}} <span class="showMoreText" @click="toggleShowText()">{{state.showMoreText}}</span></div>
            </div>
          </div>
          <div class="cast">
            <div>
              <Paginator v-model:first="state.first" :rows="5" :totalRecords="state.castCount" class="paginator"
              template="PrevPageLink NextPageLink">
                <template #left>
                  <span class="p-pl-2">Skådespelare:</span>
                </template>
              </Paginator>
            </div>
            <div style="display: flex">
              <div class="characterBody" v-for="(cast, index) in displayCastTest().slice(state.first, state.first+5)" :key="index">
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
export default {
  name: 'MovieInfoModal',
  props: {movie: Object, showMovieInfo: Boolean},
  setup(props){
    const state = reactive({
      showMore: false,
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
    
    function displayCastTest() {
      let cast = props.movie.cast
      return cast.sort((a, b) => a.order - b.order)
    }

    function displayCast() {
      let cast = props.movie.cast
      return cast.sort((a, b) => a.order - b.order).slice(0, 5).map(c => c.person.name ).join(', ')
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
    
    return { state, time, displayCast, toggleShowText, displayCastTest }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/_variables.scss";
.overlay {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
}

.modal-dialog {
  width: 40%;
  margin: 10px auto;
  height: 90%;
}

.modal-content {
  width: 100%;
  pointer-events: auto;
  background-color: $bg-secondary;
  border: $border-primary;
  border-radius: $border-radius;
  outline: 0;
}

.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 1rem;
  border-bottom: $border-primary;
}

.modal-body {
  display: grid;
  grid-template-columns: 225px auto;
  grid-template-rows: 315px auto auto;
  grid-template-areas:
    "poster info"
    "overview overview"
    "cast cast";
  padding: 2%;
  text-align: left;
  position: relative;
  background-size: cover;
  background-position: center center;
  background-attachment: fixed;
  background-repeat: no-repeat;
  overflow-y: auto;
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

.poster {
  grid-area: poster;
  z-index: 1;
  img {
    width: 210px;
    height:300px;
    box-shadow: $boxshadow;
  }
}
.info {
  grid-area: info;
  z-index: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.overview {
  grid-area: overview;
  z-index: 1;
}
.cast {
  grid-area: cast;
  display: flex;
  z-index: 1;
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
  }
}
.characterBody img {
  align-self: center;
  object-fit: cover;
  object-position: 100% 50%;
  width: 90%;
  height: 130px;
  border-radius: 50%;
  border: $border-primary
}

hr {
  width: 100%;
  border-color: $text-secondary;
}

.showMoreText {
  cursor: pointer;
  color: $text-secondary;
}

</style>