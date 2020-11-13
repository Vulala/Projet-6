USE paymybuddy;

BEGIN;
CREATE TABLE bank_account (
                id INT AUTO_INCREMENT NOT NULL,
                IBAN VARCHAR(255),
                description VARCHAR(255),
                PRIMARY KEY (id)
);
COMMIT;

BEGIN;
CREATE TABLE transaction (
                id INT AUTO_INCREMENT NOT NULL,
                user_email VARCHAR(128) NOT NULL,
                amount INT NOT NULL,
                date DATE,
                description VARCHAR(255),
                user_email_receiver VARCHAR(128) NOT NULL,
                PRIMARY KEY (id)
);
COMMIT;

BEGIN;
CREATE TABLE user (
                id INT AUTO_INCREMENT NOT NULL,
                email VARCHAR(128) NOT NULL,
                first_name VARCHAR(32),
                last_name VARCHAR(32),
                money_available INT NOT NULL,
                password VARCHAR(255) NOT NULL,
                PRIMARY KEY (id)
);
COMMIT;

BEGIN;
CREATE TABLE user_transaction (
                transaction_id INT NOT NULL,
                user_id INT NOT NULL,
                PRIMARY KEY (transaction_id, user_id)
);
COMMIT;

BEGIN;
CREATE TABLE user_bank_account (
                user_id INT NOT NULL,
                bank_account_id INT NOT NULL,
                PRIMARY KEY (user_id, bank_account_id)
);
COMMIT;

BEGIN;
ALTER TABLE user_bank_account ADD CONSTRAINT bank_account_user_bank_account_fk
FOREIGN KEY (bank_account_id)
REFERENCES bank_account (id)
ON DELETE RESTRICT
ON UPDATE RESTRICT;
COMMIT;

BEGIN;
ALTER TABLE user_transaction ADD CONSTRAINT transaction_user_transaction_fk
FOREIGN KEY (transaction_id)
REFERENCES transaction (id)
ON DELETE RESTRICT
ON UPDATE RESTRICT;
COMMIT;

BEGIN;
ALTER TABLE user_bank_account ADD CONSTRAINT user_user_bank_account_fk
FOREIGN KEY (user_id)
REFERENCES user (id)
ON DELETE RESTRICT
ON UPDATE RESTRICT;
COMMIT;

BEGIN;
ALTER TABLE user_transaction ADD CONSTRAINT user_user_transaction_fk
FOREIGN KEY (user_id)
REFERENCES user (id)
ON DELETE RESTRICT
ON UPDATE RESTRICT;
COMMIT;


-- Data used for testing purpose --

BEGIN;
INSERT INTO paymybuddy.user
(email,
first_name,
last_name,
money_available,
password)
VALUES
('emailTest',
'firstNameTest',
'lastNameTest',
'10',
'$2y$10$S6MsFs7iQk0yfKyTAVC51ONE2nD8i0ppkf.0dAmrFh2lByTZX69nO');
COMMIT;

BEGIN;
INSERT INTO paymybuddy.bank_account
(iban,
description)
VALUES
('ibanTest',
'descriptionTest');
COMMIT;

BEGIN;
INSERT INTO paymybuddy.user
(email,
first_name,
last_name,
money_available,
password)
VALUES
('emailTest2',
'firstNameTest2',
'lastNameTest2',
'0',
'$2y$10$hZFWG.lYw7Zr8jSi0JeP0OCrWaCgWeAWJwR/5.d3FudKXOBAfqjZm');
COMMIT;

BEGIN;
INSERT INTO paymybuddy.transaction
(user_email,
amount,
description,
user_email_receiver)
VALUES
('emailTest',
10,
'descriptionTest',
'userEmailReceiverTest');
COMMIT;