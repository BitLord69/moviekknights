<template>
    <div class="overlay">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <Button class="close" @click="$parent.state.showMovieInfo = false">
              <span aria-hidden="true">&times;</span>
            </Button>
          </div>
          <div class="modal-body p-4 text-left">
              <div class="poster">
                <img :src="movie.posterPath" />
              </div>
              <div class="info">
                <span><strong>{{movie.title}} ({{movie.releaseDate.slice(0, 4)}})</strong></span>
                <span>{{movie.originalTitle}}</span>
                <hr style="" />
                <span>Speltid: {{time()}}</span>
              </div>
              <div class="overview">{{movie.overview}}</div>
              <div class="cast">CAST</div>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
export default {
  name: 'MovieInfoModal',
  props: {movie: Object, showMovieInfo: Boolean},
  setup(props){

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
    
    return {time}
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
  width: 80%;
  margin: 1.75rem auto;
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
  grid-template-rows: auto auto auto;
  grid-template-areas:
    "poster info"
    "overview overview"
    "cast cast";
  padding: 2%;
  text-align: left;
}

.poster {
      grid-area: poster;
      img {
        width: 210px;
        height:300px;
        box-shadow: $boxshadow;
      }
      
    }
    .info {
      grid-area: info;
      display: flex;
      flex-direction: column;
    }
    .overview {
      grid-area: overview;
    }
    .cast {
      grid-area: cast;
    }

hr {
  width: 100%;
  border-color: $bg-secondary;
}

</style>