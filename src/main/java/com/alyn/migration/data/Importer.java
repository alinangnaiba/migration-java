package com.alyn.migration.data;

import com.alyn.migration.contract.ConnectionObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Importer {
    private ConnectionObject sqliteConnectionObject;
    private Connection connection;

    public Importer(ConnectionObject obj) {
        sqliteConnectionObject = obj;
    }

    public int saveByBatch(List<String[]> data, int batchSize)  {
        int totalRecords = 0;
        int batchCtr = 0;
        System.out.println("Saving records...");
        List<String[]> batchData = new ArrayList<String[]>();
        try {
            connection = sqliteConnectionObject.getConnection();
            for (int i = 0; i < data.size(); i++) {
                batchData.add(data.get(i));
                if (batchData.size() == batchSize || i == data.size() - 1) {
                    System.out.println("Saving batch " + ++batchCtr);
                    totalRecords += this.save(connection, batchData);
                    System.out.println(totalRecords + "/" + data.size() + " total records saved.");
                    batchData = new ArrayList<>();
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalRecords;
    }

    public int save(Connection connection, List<String[]> data) {
        String sql = "INSERT INTO import(a,b,c,d,e,f,g,h,i,j) VALUES(?,?,?,?,?,?,?,?,?,?)";
        int[] result = null;
        try {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(sql);

            for (int i = 0; i < data.size(); i++) {
                String[] d = data.get(i);
                statement.setString(1, d[0]);
                statement.setString(2, d[1]);
                statement.setString(3, d[2]);
                statement.setString(4, d[3]);
                statement.setString(5, d[4]);
                statement.setString(6, d[5]);
                statement.setDouble(7, Double.parseDouble(d[6].substring(1)));
                statement.setBoolean(8, Boolean.parseBoolean(d[7]));
                statement.setBoolean(9, Boolean.parseBoolean(d[8]));
                statement.setString(10, d[9]);
                statement.addBatch();
            }
            long start = System.currentTimeMillis();
            System.out.println("Inserting records to db...");
            result = statement.executeBatch();
            connection.commit();
            statement.clearBatch();
            long end = System.currentTimeMillis();
            statement.close();

            long elapsed = end - start;
            System.out.println("Took " + elapsed + " ms to save to db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result.length;
    }
}
