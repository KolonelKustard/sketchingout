DELETE FROM emails;

-- Keywork substitution:
--
-- :name:	Individuals name
-- :email:	Individuals email
--
INSERT INTO emails(
  from_name,
  from_email,
  subject,
  body
) VALUES (
  'Bob Geldof',
  'bob@worldpeas.com',
  'Hi :name:, I\'m Bob.  Your picture is dandy.',
  'Hi :name:,\n\nI, Bob Geldof, send you your picture.\n\nYours sincerely,\n\nBob.'
);