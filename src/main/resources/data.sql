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
   user_email VARCHAR (64) NOT NULL,
   amount DECIMAL NOT NULL,
   date DATE,
   description VARCHAR (128),
   user_email_receiver VARCHAR (64) NOT NULL,
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
   email_buddy VARCHAR (64) DEFAULT NULL,
   PRIMARY KEY (id)
);
COMMIT;

BEGIN;
CREATE TABLE buddy
(
   email_buddy VARCHAR (64) NOT NULL,
   first_name VARCHAR (32),
   last_name VARCHAR (32),
   description VARCHAR (128),
   PRIMARY KEY (email_buddy)
);
COMMIT;

BEGIN;
CREATE TABLE user_buddy
(
   user_id INTEGER NOT NULL,
   buddy_email_buddy VARCHAR (64) NOT NULL,
   PRIMARY KEY
   (
      user_id,
      buddy_email_buddy
   )
);
COMMIT;

-- -------| FOREIGN KEYS |-------

BEGIN;
ALTER TABLE user_buddy ADD CONSTRAINT user_buddy_user_id_fk FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE user_buddy ADD CONSTRAINT user_buddy_email_buddy_fk FOREIGN KEY (buddy_email_buddy) REFERENCES buddy (email_buddy);
COMMIT;

BEGIN;
ALTER TABLE bank_account ADD COLUMN user INT;
ALTER TABLE bank_account ADD CONSTRAINT bank_account_user_id_fk FOREIGN KEY (user) REFERENCES user (id);
COMMIT;

BEGIN;
ALTER TABLE transaction ADD COLUMN user INT;
ALTER TABLE transaction ADD CONSTRAINT transaction_user_id_fk FOREIGN KEY (user) REFERENCES user (id);
COMMIT;

BEGIN;
ALTER TABLE user ADD COLUMN bank_account INT;
ALTER TABLE user ADD CONSTRAINT user_bank_account_id_fk FOREIGN KEY (bank_account) REFERENCES bank_account (id);

ALTER TABLE user ADD COLUMN transaction_id INT;
ALTER TABLE user ADD CONSTRAINT user_transaction_id_fk FOREIGN KEY (transaction_id) REFERENCES transaction (id);
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
INSERT INTO paymybuddy.buddy
(
   email_buddy,
   first_name,
   last_name,
   description
)
VALUES
(
   'emailTest2',
   'firstNameTest',
   'lastNameTest',
   'descriptionTest'
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
   user_email,
   amount,
   description,
   user_email_receiver
)
VALUES
(
   'emailTest',
   '10.0',
   'descriptionTest',
   'userEmailReceiverTest'
);
COMMIT;