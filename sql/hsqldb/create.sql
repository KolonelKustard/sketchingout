CREATE TABLE users(
  id		VARCHAR(80) NOT NULL PRIMARY KEY,
  name		VARCHAR(100),
  email		VARCHAR(100),
  signature	BINARY
);

CREATE TABLE drawings(
  id		VARCHAR(80) NOT NULL PRIMARY KEY,
  completed	BIT NOT NULL DEFAULT false,
  lock		DATETIME NOT NULL,
  head		BINARY,
  body		BINARY,
  legs		BINARY,
  feet		BINARY
);
CREATE INDEX incomplete_drawings ON drawings(completed, lock, head, body, legs, feet);