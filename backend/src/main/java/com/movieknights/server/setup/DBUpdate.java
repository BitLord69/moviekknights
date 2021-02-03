package com.movieknights.server.setup;

import com.google.common.io.Files;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.*;
import java.nio.file.Path;

@AllArgsConstructor
@NoArgsConstructor
public class DBUpdate implements Runnable {
  private Path inputFile;

  @Override
  public void run() {
    updateDbWithFile();
  }

  private void updateDbWithFile() {
    InputStream is = null;
    try {
      File file = new File(String.valueOf(inputFile));
        is = new FileInputStream(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
