CREATE TABLE users(
  id			VARCHAR(36) NOT NULL PRIMARY KEY,
  name			VARCHAR(100) NULL,
  email			VARCHAR(100) NULL,
  password		VARCHAR(20) NULL,
  signature_width	SMALLINT NULL,
  signature_height	SMALLINT NULL,
  signature		LONGTEXT NULL
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
  stage_1		LONGTEXT NOT NULL,
  stage_1_signature	LONGTEXT NULL,
  stage_2_author_id	VARCHAR(36) NULL REFERENCES users(id),
  stage_2_author_name	VARCHAR(100) NULL,
  stage_2_author_email	VARCHAR(100) NULL,
  stage_2		LONGTEXT NULL,
  stage_2_signature	LONGTEXT NULL,
  stage_3_author_id	VARCHAR(36) NULL REFERENCES users(id),
  stage_3_author_name	VARCHAR(100) NULL,
  stage_3_author_email	VARCHAR(100) NULL,
  stage_3		LONGTEXT NULL,
  stage_3_signature	LONGTEXT NULL,
  stage_4_author_id	VARCHAR(36) NULL REFERENCES users(id),
  stage_4_author_name	VARCHAR(100) NULL,
  stage_4_author_email	VARCHAR(100) NULL,
  stage_4		LONGTEXT NULL,
  stage_4_signature	LONGTEXT NULL,
  
  INDEX incomplete_drawings(completed, locked, stage),
  INDEX awaiting_drawing(distinguished_id),
  UNIQUE INDEX friendly_ids(friendly_id)
);

CREATE TABLE gallery(
  id			VARCHAR(36) NOT NULL PRIMARY KEY,
  friendly_id		INTEGER NOT NULL,
  width			SMALLINT NOT NULL,
  height		SMALLINT NOT NULL,
  stage			TINYINT NOT NULL,
  stage_1_author_id	VARCHAR(36) NOT NULL REFERENCES users(id),
  stage_1_author_name	VARCHAR(100) NULL,
  stage_2_author_id	VARCHAR(36) NOT NULL REFERENCES users(id),
  stage_2_author_name	VARCHAR(100) NULL,
  stage_3_author_id	VARCHAR(36) NOT NULL REFERENCES users(id),
  stage_3_author_name	VARCHAR(100) NULL,
  stage_4_author_id	VARCHAR(36) NOT NULL REFERENCES users(id),
  stage_4_author_name	VARCHAR(100) NULL,
  thumbnail_filename	VARCHAR(20) NULL,
  anim_swf_filename	VARCHAR(20) NULL,
  pdf_filename		VARCHAR(20) NULL
);