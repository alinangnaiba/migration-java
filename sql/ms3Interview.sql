--
-- File generated with SQLiteStudio v3.2.1 on Sat May 30 20:09:58 2020
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: IMPORT
CREATE TABLE import (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, a VARCHAR (250), b VARCHAR (250), c VARCHAR (250), d VARCHAR (250), e VARCHAR (250), f VARCHAR (250), g DECIMAL, h BOOLEAN, i BOOLEAN, j VARCHAR (250));

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
