CREATE TABLE users(
  id			VARCHAR(36) NOT NULL PRIMARY KEY,
  name			VARCHAR(100) NULL,
  email			VARCHAR(100) NULL,
  password		VARCHAR(20) NULL,
  signature_width	SMALLINT NULL,
  signature_height	SMALLINT NULL,
  signature		TEXT NULL
);

CREATE TABLE drawings(
  id			VARCHAR(36) NOT NULL PRIMARY KEY,
  friendly_id		INTEGER NOT NULL AUTO_INCREMENT,
  completed		CHAR(1) NOT NULL,
  locked		DATETIME NOT NULL,
  distinguished_id	VARCHAR(36) NULL,
  version		SMALLINT NOT NULL,
  width			SMALLINT NOT NULL,
  height		SMALLINT NOT NULL,
  stage			TINYINT NOT NULL,
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
  INDEX awaiting_drawing(distinguished_id),
  UNIQUE INDEX complete_drawings(friendly_id)
);

CREATE TABLE emails(
  id		INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
  from_name	VARCHAR(40) NOT NULL,
  from_email	VARCHAR(40) NOT NULL,
  subject	VARCHAR(150) NOT NULL,
  body		VARCHAR(255) NOT NULL
);