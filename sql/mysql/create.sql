CREATE TABLE users(
  id		VARCHAR(36) NOT NULL PRIMARY KEY,
  name		VARCHAR(100) NULL,
  email		VARCHAR(100) NULL,
  password	VARCHAR(20) NULL,
  signature	TEXT NULL
);

CREATE TABLE drawings(
  id			VARCHAR(36) NOT NULL PRIMARY KEY,
  completed		CHAR(1) NOT NULL,
  locked		DATETIME NOT NULL,
  distinguished_id	VARCHAR(36) NULL,
  stage			INTEGER NOT NULL,
  stage_1_author_id	VARCHAR(36) NOT NULL REFERENCES users(id),
  stage_1_author_name	VARCHAR(100) NULL,
  stage_1_author_email	VARCHAR(100) NULL,
  stage_1		TEXT NOT NULL,
  stage_1_signature	TEXT NULL,
  stage_2_author_id	VARCHAR(36) NULL REFERENCES users(id),
  stage_2_author_name	VARCHAR(100) NULL,
  stage_2_author_email	VARCHAR(100) NULL,
  stage_2		TEXT NULL,
  stage_2_signature	TEXT NULL,
  stage_3_author_id	VARCHAR(36) NULL REFERENCES users(id),
  stage_3_author_name	VARCHAR(100) NULL,
  stage_3_author_email	VARCHAR(100) NULL,
  stage_3		TEXT NULL,
  stage_3_signature	TEXT NULL,
  stage_4_author_id	VARCHAR(36) NULL REFERENCES users(id),
  stage_4_author_name	VARCHAR(100) NULL,
  stage_4_author_email	VARCHAR(100) NULL,
  stage_4		TEXT NULL,
  stage_4_signature	TEXT NULL,
  
  INDEX incomplete_drawings(completed, locked, stage),
  INDEX awaiting_drawing(distinguished_id)
);