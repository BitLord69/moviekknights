<template>
  <div class="overlay">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <Button icon="pi pi-times" @click="$parent.state.showPersonInfo = false" />
        </div>
        <div class="modal-body">
          <div class="poster">
            <img :src="person.profileImgPath != null ? person.profileImgPath : '/img/noimage.png'" />
          </div>
          <div class="info">
            <h3 class="p-m-0">{{person.name}}</h3>
            <hr />
            <span v-if="person.dob">Född: {{person.dob.slice(0, 10)}} <span v-if="!person.dod">({{moment(person.dob).locale('sv').fromNow(true)}})</span></span>
            <span v-if="person.dod">Död: {{person.dod.slice(0, 10)}}</span>
            <span v-if="person.imdbId">IMDb: <a :href="'https://www.imdb.com/name/' + person.imdbId" target="_blank">Klicka här</a></span>
            <span v-if="person.homepage">Hemsida: <a :href="person.homepage" target="_blank">Klicka här</a></span>
          </div>
          <div v-if="state.biography.length > 3" class="biography">
            <div>
              <div>{{state.biography}} <span class="showMoreText" @click="toggleShowText()">{{state.showMoreText}}</span></div>
            </div>
          </div>
          <div class="filmography">
						<div class="p-d-flex p-jc-evenly p-mb-2">
							<Button :class="{active: movieListChosen == 'acting'}" @click="$parent.state.movieListChosen = 'acting'">Skådespelat</Button>
							<Button :class="{active: movieListChosen == 'directing'}" @click="$parent.state.movieListChosen = 'directing';
               chooseMovieListToView('directing')">Regisserat</Button>
							<Button :class="{active: movieListChosen == 'composing'}" @click="$parent.state.movieListChosen = 'composing';
               chooseMovieListToView('composing')">Komponerat musiken till</Button>
						</div>
            <hr />
						<div class="list" v-if="movieListChosen != 'acting'">
							<div v-for="(movie, index) in state.filmographyDC" :key="index">
								{{movie.title}} <span v-if="movie.releaseDate != null">({{movie.releaseDate.slice(0, 4)}})</span>
							</div>
						</div>
						<div class="list" v-if="movieListChosen == 'acting'">
							<div v-for="(movie, index) in state.filmographyA" :key="index">
								{{movie.title}} <span v-if="movie.date != null">({{movie.date.slice(0, 4)}})</span> - {{movie.character}}
							</div>
						</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, onMounted, watchEffect } from 'vue';
import moment from 'moment';
import PersonHelper from '@/modules/PersonHelper';
export default {
  name: 'PersonInfoModal',
  props: {person: Object, movieListChosen: String, movies: Array},
  setup(props){
    const { getMoviesDirectedByPerson, getMoviesComposedByPerson, getMoviesPersonActedIn, moviesDirectedBy, moviesComposedBy, moviesActedIn } = PersonHelper();
    const state = reactive({
      showMore: false,
      showMoreText: "[läs mer...]",
			biography: props.person && props.person.biography.slice(0, 150) + "...",
			filmographyA: [],
			filmographyDC: [],
		})

    onMounted(async () => {
      await getMoviesDirectedByPerson(props.person.id);
      await getMoviesComposedByPerson(props.person.id);
      await getMoviesPersonActedIn(props.person.id);
    });

    watchEffect(async () => {
      if (moviesDirectedBy.value && moviesComposedBy.value) {
			if(state.movieListChosen == "directing") state.filmographyDC = moviesDirectedBy.value;
			else if(state.movieListChosen == "composing") state.filmographyDC = moviesComposedBy.value;
      }
      
      if (moviesActedIn.value && moviesActedIn.value.length > 0) getMoviesStarringPerson();
    })

		
    chooseMovieListToView(props.movieListChosen)

    function toggleShowText() {
      state.showMore = !state.showMore
      state.showMoreText = state.showMore ? "[läs mindre...]" : "[läs mer...]"
      state.biography = state.showMore ?props.person && props.person.biography : props.person && props.person.biography.slice(0, 150) + "..."
    }

    function chooseMovieListToView(list) {
			state.movieListChosen = list;
			if(list == "directing") state.filmographyDC = moviesDirectedBy.value;
			else if(list == "composing") state.filmographyDC = moviesComposedBy.value;
		}
		
		function getMoviesStarringPerson() {
      console.log("moviesActedIn: ", moviesActedIn.value);
      let temp = moviesActedIn.value;
      console.log("temp: ", temp);
			temp.forEach(m => {
        console.log("film: ", m);
        m.cast.forEach(p => {
					if(p.person.id == props.person.id) {
						state.filmographyA.push({title: m.title, date: m.releaseDate, character: p.character})
					}
					
        });
			});
			//state.filmographyA = state.filmographyA.sort((a, b) => b.date - a.date)
    }

    return { state, moment, toggleShowText, chooseMovieListToView }
  }
}
</script>

<style lang="scss" scoped>
@import "@/styles/_variables.scss";
.overlay {
  z-index: 3;
}

.modal-dialog {
  z-index: 4 !important;
}

.modal-header {
  justify-content:flex-end;
}

.modal-body {
  grid-template-rows: 315px auto minmax(50px, 390px);
  grid-template-areas:
    "poster info"
    "biography biography"
    "filmography filmography";
  background-color: $bg-secondary;
}

.poster {
  z-index: 4 !important;
}

.info {
  z-index: 4 !important;
}

.biography {
  grid-area: overview;
  z-index: 4;
}

.filmography {
  grid-area: filmography;
  display: flex;
  z-index: 4;
  flex-direction: column;
  margin-top: 15px;
  .list {
    overflow-y: auto;
  }
}

.active {
  box-shadow: $boxshadow;
}

@media only screen and (max-width: 800px) {
  .modal-body {
    grid-template-rows: 255px auto auto minmax(50px, 190px);
    grid-template-areas:
      "poster poster"
      "info info"
      "biography biography"
      "filmography filmography";
  }
}
</style>