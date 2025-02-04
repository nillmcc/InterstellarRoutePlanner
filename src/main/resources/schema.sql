DROP TABLE if EXISTS gate;
CREATE TABLE gate (
    id VARCHAR(3) PRIMARY KEY,
    name VARCHAR(20),
    connections JSON
);
