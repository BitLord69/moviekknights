package com.movieknights.server.setup;


import com.movieknights.server.entities.DBSetting;
import com.movieknights.server.entities.LastLineDTO;
import com.movieknights.server.repos.DBSettingRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

@NoArgsConstructor
@AllArgsConstructor
@Component
public class DBSetup {

    private final String fileUrlStart = "http://files.tmdb.org/p/exports/movie_ids_";
    private final String fileUrlEnd = ".json.gz";
    private LastLineDTO lineDTO;
    private long numberOfLines = 0;

    @Autowired
    private DBSettingRepo dbSettingRepo;

    @PostConstruct
    public void run() {
        checkDB();
    }

    private void checkDB() {
        LocalDate localDate = LocalDate.now().minusDays(1);
        String text = localDate.toString();
        Optional<DBSetting> dbSetting = dbSettingRepo.findById(localDate);
        if (dbSetting.isPresent()) {
            return;
        }
        getFileFromUrl();
        dbSettingRepo.save(new DBSetting(localDate, numberOfLines, lineDTO.getId()));
    }

    private void getFileFromUrl() {
        LocalDate localDate = LocalDate.now().minusDays(1);
        String newMonth;
        String newDay;
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();

        if (month < 10) {
            newMonth = "0" + month;
        }
        else {
            newMonth = "" + month;
        }
        if (day < 10) {
            newDay = "0" + day;
        }
        else {
            newDay = "" + day;
        }
        String fileUrlDate = newMonth + "_" + newDay + "_" + year;
        InputStream in = null;
        try {
            in = new URL(fileUrlStart + fileUrlDate + fileUrlEnd).openStream();
            Files.copy(in, Paths.get("src/dbfiles", fileUrlDate + fileUrlEnd), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        unZipFile("src/dbfiles/" + fileUrlDate + fileUrlEnd);
    }

    private void unZipFile(String filePath) {
        Path path = Paths.get(filePath);
        Path target = Paths.get(filePath.substring(0, filePath.length() - 3));
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(path.toFile()));
            Files.copy(gzipInputStream, target, StandardCopyOption.REPLACE_EXISTING);
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
}
