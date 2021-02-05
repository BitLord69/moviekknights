<template>
  <div class="overlay">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <Button>Skapa event</Button>
          <Button icon="pi pi-times" @click="$parent.state.showPersonInfo = false" />
        </div>
        <div class="modal-body">
          <div class="poster">
            <img :src="person.profileImgPath != null ? person.profileImgPath : '/img/noimage.png'" />
          </div>
          <div class="info">
            <h3 class="p-m-0">{{person.name}}</h3>
            <hr style="" />
            <span v-if="person.dob">Född: {{person.dob.slice(0, 10)}} <span v-if="!person.dod">({{moment(person.dob).locale('sv').fromNow(true)}})</span></span>
            <span v-if="person.dod">Död: {{person.dod.slice(0, 10)}}</span>
          </div>
          <div v-if="state.biography.length > 3" class="overview">
            <div>
              <div>{{state.biography}} <span class="showMoreText" @click="toggleShowText()">{{state.showMoreText}}</span></div>
            </div>
          </div>
          <div class="cast">
						<div class="p-d-flex p-jc-evenly p-mb-2">
							<span @click="chooseMovieListToView('acting')">Skådespelat</span> | 
							<span @click="chooseMovieListToView('directing')">Regisserat</span> | 
							<span @click="chooseMovieListToView('composing')">Komponerat musiken till</span>
						</div>
						<div v-if="state.movieListChosen != 'acting'">
							<div v-for="(movie, index) in state.filmographyDC" :key="index">
								{{movie.title}} ({{movie.date}})
							</div>
						</div>
						<div v-if="state.movieListChosen == 'acting'">
							<div v-for="(movie, index) in state.filmographyA" :key="index">
								{{movie.title}} ({{movie.date}}) - {{movie.character}}
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
import moment from 'moment';
export default {
  name: 'PersonInfoModal',
  props: {person: Object, showPersonInfo: Boolean, movies: Array},
  setup(props){
    const state = reactive({
      showMore: false,
      showMoreText: "[läs mer...]",
			biography: props.person && props.person.biography.slice(0, 150) + "...",
			filmographyA: [],
			filmographyD: [],
			filmographyC: [],
			filmographyDC: [],
			movieListChosen: "acting"
		})
		
		getMoviesStarringPerson();
		getMoviesDirectedByPerson();
		getMoviesComposedByPerson();

		function chooseMovieListToView(list) {
			state.movieListChosen = list;
			if(list == "directing") state.filmographyDC = state.filmographyD;
			else if(list == "composing") state.filmographyDC = state.filmographyC;
		}

    function toggleShowText() {
      state.showMore = !state.showMore
      state.showMoreText = state.showMore ? "[läs mindre...]" : "[läs mer...]"
      state.biography = state.showMore ?props.person && props.person.biography : props.person && props.person.biography.slice(0, 150) + "..."
    }

    function getMoviesDirectedByPerson() {
			props.movies.forEach(m => {
        m.directors.forEach(p => {
					if(p.id == props.person.id) {
						state.filmographyD.push({title: m.title, date: m.releaseDate.slice(0, 4)})
					}
					
        });
			});
			state.filmographyD = state.filmographyD.sort((a, b) => b.date - a.date)
		}
		
		function getMoviesComposedByPerson() {
			props.movies.forEach(m => {
        m.composers.forEach(p => {
					if(p.id == props.person.id) {
						state.filmographyC.push({title: m.title, date: m.releaseDate.slice(0, 4)})
					}
					
        });
			});
			state.filmographyC = state.filmographyC.sort((a, b) => b.date - a.date)
		}
		
		function getMoviesStarringPerson() {
			props.movies.forEach(m => {
        m.cast.forEach(p => {
					if(p.person.id == props.person.id) {
						state.filmographyA.push({title: m.title, date: m.releaseDate.slice(0, 4), character: p.character})
					}
					
        });
			});
			state.filmographyA = state.filmographyA.sort((a, b) => b.date - a.date)
    }

    return { state, moment, toggleShowText, chooseMovieListToView }
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
  z-index: 3;
}

.modal-dialog {
  width: 40%;
  margin: 10px auto;
  height: 90%;
  z-index: 4;
}

.modal-content {
  width: 100%;
  background-color: $bg-secondary;
  border: $border-primary;
  border-radius: $border-radius;
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
  grid-template-rows: 315px 0px 390px;
  grid-template-areas:
    "poster info"
    "overview overview"
    "cast cast";
  padding: 2%;
  text-align: left;
  background-color: $bg-secondary;
  overflow-y: auto;
}

.poster {
  grid-area: poster;
  z-index: 4;
  img {
    width: 210px;
    height:300px;
    box-shadow: $boxshadow;
  }
}
.info {
  grid-area: info;
  z-index: 4;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.overview {
  grid-area: overview;
  z-index: 4;
}
.cast {
  grid-area: cast;
  display: flex;
  z-index: 4;
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