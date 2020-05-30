# migration-java
Demo code for migrating data from CSV file to a database (SQLite)

### Set up environment checklist
- [x] Create directories where you would want the app to read the file and to write the output files
- [x] Edit `config.properties` and specify the directories, url, and filename.

   * `dbUrl` - database URL
   * `logFilePath` - the directory where the log file will be written. e.g (C:\\logs\\)
   * `importFilePath` - the directory where the import file is located. e.g (C:\\import\\)
   * `badFilePath` - the directory where the record that does not match the column count will be written to e.g (C:\\badfiles\\)
   * `batchSize` - the number of records you'd like to send per batch. Must be greater than zero.
- [x] Java 8. This is written in Java 8.

### Overview
The application will scan the directory for .csv files. If no files are found it will print to the console that no files are found, otherwise, it will iterate through each file and process it. It expects the CSV files has 10 "columns". A record in each file that has an empty column will be added to the bad data object that will be written in a filename-bad.csv file. Each file will have its corresponding "-bad.csv" file. The 7th column is expected to be a money value and will be parsed as double before sending the record to the DB. The app will also check if the value is parse-able to double, if not, it will consider it as bad data and be added to the bad data object. After "sanitizing" the data, we now have two objects, `goodData` and `badData`. `badData` object will be written to <filename>-bad.csv by another thread and the `goodData` object will be processed by batch. The batch size is specified by the user. After inserting the record to the DB, the app will write the statistics (record received, successful, failed) to the log file name <filename>.log, also corresponds to the import files. 

My assumptions are: 
  1.) that there will be no connection issue in inserting the records to the DB so the records will be inserted 100% of the time.
  2) all the directories are already created are specified correctly in the `.properties` file before running.
  3) no errors needed to be logged in a log file.
  4) there is one or more import file/s in the directory specified for import scan.
