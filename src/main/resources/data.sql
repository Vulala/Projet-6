BEGIN;
CREATE SCHEMA IF NOT EXISTS paymybuddy;
USE paymybuddy;
COMMIT;
BEGIN;
CREATE TABLE bank_account
(
   id INT AUTO_INCREMENT NOT NULL,
   IBAN VARCHAR (34),
   description VARCHAR (128),
   PRIMARY KEY (id)
);
COMMIT;
BEGIN;
CREATE TABLE transaction
(
   id INT AUTO_INCREMENT NOT NULL,
   amount DECIMAL NOT NULL,
   date DATE,
   description VARCHAR (128),
   PRIMARY KEY (id)
);
COMMIT;
BEGIN;
CREATE TABLE user
(
   id INT AUTO_INCREMENT NOT NULL,
   email VARCHAR (64) NOT NULL,
   first_name VARCHAR (32),
   last_name VARCHAR (32),
   money_available DECIMAL NOT NULL,
   password VARCHAR (255) NOT NULL,
   PRIMARY KEY (id)
);
COMMIT;
BEGIN;
CREATE TABLE user_friends
(
   user_id INT,
   friends_id INT
);
COMMIT;
-- -------| FOREIGN KEYS |-------
BEGIN;
ALTER TABLE bank_account ADD COLUMN user INT;
ALTER TABLE bank_account ADD CONSTRAINT bank_account_user_id_fk FOREIGN KEY (user) REFERENCES user (id);
COMMIT;
BEGIN;
ALTER TABLE transaction ADD COLUMN user_sender INT;
ALTER TABLE transaction ADD CONSTRAINT transaction_user_sender_id_fk FOREIGN KEY (user_sender) REFERENCES user (id);
COMMIT;
BEGIN;
ALTER TABLE transaction ADD COLUMN user_receiver INT;
ALTER TABLE transaction ADD CONSTRAINT transaction_user_receiver_id_fk FOREIGN KEY (user_receiver) REFERENCES user (id);
COMMIT;
BEGIN;
ALTER TABLE user ADD COLUMN bank_account INT;
ALTER TABLE user ADD CONSTRAINT user_bank_account_id_fk FOREIGN KEY (bank_account) REFERENCES bank_account (id);
ALTER TABLE user ADD COLUMN transaction_id INT;
ALTER TABLE user ADD CONSTRAINT user_transaction_id_fk FOREIGN KEY (transaction_id) REFERENCES transaction (id);
COMMIT;
BEGIN;
ALTER TABLE user_friends ADD CONSTRAINT user_friends_user_id_fk FOREIGN KEY (user_id) REFERENCES user (id);
ALTER TABLE user_friends ADD CONSTRAINT user_friends_friends_id_fk FOREIGN KEY (friends_id) REFERENCES user (id);
COMMIT;
-- -------| Data used for testing purpose |-------
BEGIN;
INSERT INTO paymybuddy.user
(
   email,
   first_name,
   last_name,
   money_available,
   password
)
VALUES
(
   'emailTest',
   'firstNameTest',
   'lastNameTest',
   '30.0',
   '$2y$10$S6MsFs7iQk0yfKyTAVC51ONE2nD8i0ppkf.0dAmrFh2lByTZX69nO'
);
COMMIT;
BEGIN;
INSERT INTO paymybuddy.user
(
   email,
   first_name,
   last_name,
   money_available,
   password
)
VALUES
(
   'emailTest2',
   'firstNameTest2',
   'lastNameTest2',
   '0.0',
   '$2y$10$hZFWG.lYw7Zr8jSi0JeP0OCrWaCgWeAWJwR/5.d3FudKXOBAfqjZm'
);
COMMIT;
BEGIN;
INSERT INTO paymybuddy.bank_account
(
   iban,
   description
)
VALUES
(
   'ibanTest',
   'descriptionTest'
);
COMMIT;
BEGIN;
INSERT INTO paymybuddy.transaction
(
   amount,
   description
)
VALUES
(
   '10.0',
   'descriptionTest'
);
COMMIT;