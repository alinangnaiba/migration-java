package com.alyn.migration.parse;

import com.alyn.migration.contract.ImportFileHandler;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvHandler implements ImportFileHandler {
    private String path;

    public CsvHandler(String path) {
        this.path = path;
    }

    public List<String[]> read() {
        List<String[]> data = new ArrayList<String[]>();
        CSVReader reader = null;

        try{
            reader = new CSVReader(new FileReader(path));
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (isHeader(line)) {
                    continue;
                }

                data.add(line);
            }
        } catch (IOException e) {
            return null;
        } catch (CsvValidationException e) {
            e.printStackTrace();
            return null;
        }

        return data;
    }

    public String getFileName() {
        File file = new File(this.path);

        return file.getName();
    }

    private boolean isHeader(String[] line) {
        if (line[0].equals("A") && line[1].equals("B") && line[2].equals("C") && line[3].equals("D") &&
                line[4].equals("E") && line[5].equals("F") && line[6].equals("G") && line[7].equals("H") &&
                line[8].equals("I") && line[9].equals("J")) {
            return true;
        }

        return false;
    }
}
