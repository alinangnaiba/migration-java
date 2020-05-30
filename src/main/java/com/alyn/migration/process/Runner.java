package com.alyn.migration.process;

import com.alyn.migration.contract.ImportFileHandler;
import com.alyn.migration.data.SqliteConnectionObject;
import com.alyn.migration.data.Importer;
import com.alyn.migration.entities.AppConfiguration;
import com.alyn.migration.entities.FilteredData;
import com.alyn.migration.entities.Statistic;
import com.alyn.migration.parse.CsvHandler;
import com.alyn.migration.parse.DataFilter;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Runner {
    private AppConfiguration config;
    private int totalSaved;
    private int totalFailed;
    public Runner(AppConfiguration config) {
        this.config = config;
        totalFailed = 0;
        totalSaved = 0;
    }

    public void run() {
        long start = System.currentTimeMillis();
        List<String> files = this.getAllFiles();
        if (files.size() < 1) {
            System.out.println("No CSV file found in " + config.importFilePath);
            return;
        }
        files.forEach(f -> {
            processFile(f);
        });
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Import done. \nTook " + elapsed + " ms");
    }

    public List<String> getAllFiles() {
        List<String> result = null;
        try (Stream<Path> walk = Files.walk(Paths.get(config.importFilePath))){
            result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".csv")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void processFile(String fileFullPath) {
        long start = System.currentTimeMillis();
        CsvHandler handler = new CsvHandler(fileFullPath);
        List<String[]> data = this.readFile(handler);
        String filename = handler.getFileName();
        FilteredData filteredData = this.filterData(data);
        Thread writerThread = new Thread(() -> {
            this.writeDataToCsv(filteredData.badData, filename);
        });
        writerThread.start();

        this.insertRecordsToDb(filteredData.goodData, config.batchSize);
        totalFailed += filteredData.badData.size();
        Statistic stat = new Statistic();
        stat.totalFailed = totalFailed;
        stat.totalSuccessful = totalSaved;
        stat.totalReceived = data.size();
        writeToLog(stat, filename);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println(filename + " done. \nTook " + elapsed + " ms");
    }

    private void writeToLog(Statistic stat, String filename) {
        String regex = "\\.";
        String[] splitStr = filename.split(regex);
        String logFilename = config.logFilePath + splitStr[0] + ".log";
        try {
            PrintWriter writer = new PrintWriter(logFilename);
            writer.println(stat.totalReceived + " records received.");
            writer.println(stat.totalSuccessful + " records successful.");
            writer.println(stat.totalFailed + " records failed.");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private List<String[]> readFile(ImportFileHandler handler) {
        String filename = handler.getFileName();
        System.out.println("Read file " + filename + " started.");
        long readStart = System.currentTimeMillis();
        List<String[]> data = handler.read();
        if (data == null) {
            return null;
        }
        System.out.println("Read file " + filename + " finished.");
        long readEnd = System.currentTimeMillis();
        long readElapsed = readEnd - readStart;
        System.out.println("Reading file took " + readElapsed + " ms");

        return data;
    }

    private void writeDataToCsv(List<String[]> data, String filename) {
        String regex = "\\.";
        String[] str = filename.split(regex);
        String badFilename = str[0] + "-bad." + str[1];
        String fn = config.badFilePath + badFilename;
        System.out.println("Writing records to " + fn);
        long start = System.currentTimeMillis();
        try (FileOutputStream fos = new FileOutputStream(fn);
             OutputStreamWriter osw = new OutputStreamWriter(fos,
                     StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {

            writer.writeAll(data, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("Write done. \nTook " + elapsed + " ms");
    }

    private void insertRecordsToDb(List<String[]> data, int batchSize) {
        SqliteConnectionObject sqliteConnectionObject = new SqliteConnectionObject(config.dbUrl);
        Importer importer = new Importer(sqliteConnectionObject);
        totalSaved = importer.saveByBatch(data, batchSize);
        totalFailed = data.size() - totalSaved;
    }

    private FilteredData filterData(List<String[]> data) {
        FilteredData records = new FilteredData();
        DataFilter filter = new DataFilter(data);
        filter.sanitize();
        records.goodData = filter.getGoodData();
        records.badData = filter.getBadData();

        return records;
    }
}
