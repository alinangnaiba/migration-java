package com.alyn.migration;
import com.alyn.migration.entities.AppConfiguration;
import com.alyn.migration.parse.CsvHandler;
import com.alyn.migration.process.Runner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class MigrationApplication {

    public static void main(String[] args) {

        try (InputStream in = new FileInputStream("config.properties")) {

            Properties prop = new Properties();

            if (in == null) {
                System.out.println("Unable to get config.properties. Tool will close");
                return;
            }

            prop.load(in);

            AppConfiguration config = new AppConfiguration();
            config.dbUrl = prop.getProperty("dbUrl");
            config.badFilePath = prop.getProperty("badFilePath");
            config.importFilePath = prop.getProperty("importFilePath");
            config.logFilePath = prop.getProperty("logFilePath");
            config.batchSize = Integer.parseInt(prop.getProperty("batchSize"));

            Runner runner = new Runner(config);
            runner.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
