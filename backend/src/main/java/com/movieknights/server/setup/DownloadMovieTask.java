package com.movieknights.server.setup;

import com.movieknights.server.entities.Movie;
import com.movieknights.server.services.MovieService;
import com.movieknights.server.utils.TextUtil;

public class DownloadMovieTask implements Runnable {
  private long movieId;

  private MovieService movieService;

  public DownloadMovieTask(long id, MovieService ms) {
    movieId = id;
    movieService = ms;
  }

  @Override
  public void run() {
    Movie m = movieService.getMovieById(movieId);
    if (m != null) {
      System.out.printf("Movie '%s' (id: %s) is now in the database...\n", TextUtil.pimpString(m.getTitle(), TextUtil.LEVEL_INFO), m.getMovieId());
    }
  }
}
