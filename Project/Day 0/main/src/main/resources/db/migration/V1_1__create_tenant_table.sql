DROP TABLE IF EXISTS tenants;


CREATE TABLE tenants(
    id VARCHAR(100) NOT NULL,
    username VARCHAR(50),
    password VARCHAR(255)
);

ALTER TABLE tenants ADD PRIMARY KEY (id);