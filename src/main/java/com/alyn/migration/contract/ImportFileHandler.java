package com.alyn.migration.contract;

import java.util.List;

public interface ImportFileHandler {
    String getFileName();
    List<String[]> read();
}
