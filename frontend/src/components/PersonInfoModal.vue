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
          <div class="overview">
            <div>
              <div>{{state.biography}} <span class="showMoreText" @click="toggleShowText()">{{state.showMoreText}}</span></div>
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
  props: {person: Object, showPersonInfo: Boolean},
  setup(props){
    const state = reactive({
      showMore: false,
      showMoreText: "[läs mer...]",
      biography: props.person && props.person.biography.slice(0, 150) + "...",
    })

    function toggleShowText() {
      state.showMore = !state.showMore
      state.showMoreText = state.showMore ? "[läs mindre...]" : "[läs mer...]"
      state.biography = state.showMore ?props.person && props.person.biography : props.person && props.person.biography.slice(0, 150) + "..."
    }
    

    return { state, moment, toggleShowText }
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