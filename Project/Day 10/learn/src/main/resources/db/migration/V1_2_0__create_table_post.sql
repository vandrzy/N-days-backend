CREATE TABLE posts(
   id VARCHAR(150) NOT NULL,
   short_code VARCHAR(150) NOT NULL UNIQUE,
   title VARCHAR(255) NOT NULL,
   description TEXT,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NULL,
   created_by VARCHAR(100),
   updated_by VARCHAR(100)
);

ALTER TABLE posts ADD PRIMARY KEY (id);