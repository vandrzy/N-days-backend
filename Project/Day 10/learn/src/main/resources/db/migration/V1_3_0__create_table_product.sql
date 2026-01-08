CREATE TABLE products(
   id VARCHAR(150) NOT NULL,
   short_code VARCHAR(150) NOT NULL UNIQUE,
   title VARCHAR(255) NOT NULL,
   photo_id VARCHAR(255),
   photo_url VARCHAR(255),
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NULL,
   created_by VARCHAR(100),
   updated_by VARCHAR(100)
);