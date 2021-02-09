package com.movieknights.server.setup;

import com.google.gson.Gson;
import com.movieknights.server.entities.DBSetting;
import com.movieknights.server.entities.LastLineDTO;
import com.movieknights.server.repos.DBSettingRepo;
import com.movieknights.server.services.MovieService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Component
public class DBUpdate implements Runnable {
  private final String fileUrlStart = "http://files.tmdb.org/p/exports/movie_ids_";
  private final String fileUrlEnd = ".json.gz";
  private final String PATH = "src/dbfiles/";
  private LastLineDTO lineDTO;
  private long numberOfLines = 0;

  private DBSettingRepo dbSettingRepo;
  private MovieService movieService;

  public DBUpdate(DBSettingRepo dbRepo, MovieService ms) {
    movieService = ms;
    dbSettingRepo = dbRepo;
  }

  @Override
  public void run() {
    System.out.printf("In DBUpdate.run, starting checks...\n");
    long lastId = checkDB();
    System.out.printf("After checkDB, lastID: %s\n", lastId);
    try {
      downloadMovies(lastId);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void downloadMovies(long lastId) throws InterruptedException {
    ExecutorService pool = Executors.newFixedThreadPool(10);

    for (long id = 1l; id < lastId; id++) { //Your ArrayList
      pool.execute(new DownloadMovieTask(id, movieService));
    }

    System.out.printf("Wohoooo - All movies added to the download que!!!\n");

    pool.shutdown();
    pool.awaitTermination(600, TimeUnit.SECONDS);
  }

  private long checkDB() {
    LocalDate localDate = LocalDate.now().minusDays(1);
    Optional<DBSetting> dbSetting = dbSettingRepo.findById(localDate);
    if (dbSetting.isPresent()) {
      return dbSetting.get().getLastIndex();
    }
    getFileFromUrl();
    dbSettingRepo.save(new DBSetting(localDate, numberOfLines, lineDTO.getId()));
    return lineDTO.getId();
  }

  private void getFileFromUrl() {
    LocalDate localDate = LocalDate.now().minusDays(1);
    String newMonth;
    String newDay;
    int year = localDate.getYear();
    int month = localDate.getMonthValue();
    int day = localDate.getDayOfMonth();

    newMonth = month < 10 ? "0" + month : "" + month;
    newDay = day < 10 ? "0" + day : "" + day;

    String fileUrlDate = newMonth + "_" + newDay + "_" + year;
    InputStream in = null;
    try {
      Path p = Paths.get(PATH);
      java.nio.file.Files.createDirectories(p);

      in = new URL(fileUrlStart + fileUrlDate + fileUrlEnd).openStream();
      java.nio.file.Files.copy(in, Paths.get(PATH, fileUrlDate + fileUrlEnd), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }
    unZipFile("src/dbfiles/" + fileUrlDate + fileUrlEnd);
  }

  private void unZipFile(String filePath){
    Path path = Paths.get(filePath);
    Path target = Paths.get(filePath.substring(0, filePath.length() - 3));
    GZIPInputStream gzipInputStream = null;
    try {
      gzipInputStream = new GZIPInputStream(new FileInputStream(path.toFile()));
      java.nio.file.Files.copy(gzipInputStream, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        assert gzipInputStream != null;
        gzipInputStream.close();
      } catch (IOException e){
        e.printStackTrace();
      }
    }

    //Deletes .gz file after unzipping it.
    try{
      Files.delete(path);
    } catch (IOException e) {
      e.printStackTrace();
    }

    countLinesInFile(target);
  }

  private void countLinesInFile(Path target) {
    File file = new File(String.valueOf(target));
    StringBuilder builder = new StringBuilder();
    RandomAccessFile randomAccessFile = null;
    try {
      randomAccessFile = new RandomAccessFile(file, "r");
      long fileLength = file.length() - 2;
      randomAccessFile.seek(fileLength);
      for(long pointer = fileLength; pointer >= 0; pointer--){
        randomAccessFile.seek(pointer);
        char c;
        c = (char)randomAccessFile.read();
        if(c == '\n'){
          break;
        }
        builder.append(c);
      }
      builder.reverse();
      String lastLine = builder.toString();
      Gson gson = new Gson();
      lineDTO = gson.fromJson(lastLine, LastLineDTO.class);
      fastAfCount(target);
    } catch (IOException e) {
      e.printStackTrace();
    } finally{
      if(randomAccessFile != null){
        try {
          randomAccessFile.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void fastAfCount(Path fileName) {

    try (InputStream is = new BufferedInputStream(new FileInputStream(String.valueOf(fileName)))) {
      byte[] c = new byte[1024];
      int count = 0;
      int readChars = 0;
      boolean endsWithoutNewLine = false;
      while ((readChars = is.read(c)) != -1) {
        for (int i = 0; i < readChars; ++i) {
          if (c[i] == '\n')
            ++count;
        }
        endsWithoutNewLine = (c[readChars - 1] != '\n');
      }
      if (endsWithoutNewLine) {
        ++count;
      }
      numberOfLines = count;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

//  private void updateDbWithFile() {
//    InputStream is = null;
//    try {
//      File file = new File(String.valueOf(inputFile));
//        is = new FileInputStream(file);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
}
