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
  head_author_id	VARCHAR(36) NOT NULL REFERENCES users(id),
  head_author_email	VARCHAR(100) NULL,
  head			TEXT NOT NULL,
  body_author_id	VARCHAR(36) NULL REFERENCES users(id),
  body_author_email	VARCHAR(100) NULL,
  body			TEXT NULL,
  legs_author_id	VARCHAR(36) NULL REFERENCES users(id),
  legs_author_email	VARCHAR(100) NULL,
  legs			TEXT NULL,
  feet_author_id	VARCHAR(36) NULL REFERENCES users(id),
  feet_author_email	VARCHAR(100) NULL,
  feet			TEXT NULL,
  
  INDEX incomplete_drawings(completed, locked),
  INDEX awaiting_drawing(distinguished_id)
);