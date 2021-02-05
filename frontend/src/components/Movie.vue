<template>
  <div class="movie" :style="{backgroundImage: `url(${movie.posterPath != null ? movie.posterPath : '/img/noimage.png'})`}">
    <div class="movie-info p-py-2">
      <span class="p-mx-2">{{movie.movieId}} {{movie.title}}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "Movie",
  props: {movie: Object},
  setup(props){

    return { props }
  }

}
</script>

<style lang="scss" scope>
@import "@/styles/_variables.scss";
  .movie{
    width: 100%;
    height: 250px;
    background-size: 100% 100%;
    position: relative;
    box-shadow: $boxshadow;
    transition: transform 0.2s;
    &:hover {
      transform: scale(1.05);
    }
  }

  .movie-info{
    position: absolute;
    width: 100%;
    bottom: 0;
    left: 0;
    color: $text-primary;
    background: rgba($bg-secondary, 0.9);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
</style>