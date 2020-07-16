START TRANSACTION;
CREATE SCHEMA IF NOT EXISTS `paymybuddy` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `paymybuddy` ;
COMMIT;

-- -----------------------------------------------------
-- Table `paymybuddy`.`bank_account`
-- -----------------------------------------------------
START TRANSACTION;
CREATE TABLE IF NOT EXISTS `paymybuddy`.`bank_account` (
  `iban` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`iban`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;
COMMIT;

-- -----------------------------------------------------
-- Table `paymybuddy`.`transaction`
-- -----------------------------------------------------
START TRANSACTION;
CREATE TABLE IF NOT EXISTS `paymybuddy`.`transaction` (
  `user_email` VARCHAR(128) NOT NULL,
  `amount` INT NOT NULL,
  `date` DATE NULL DEFAULT NULL,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `user_email_receiver` VARCHAR(128) NULL DEFAULT NULL,
  PRIMARY KEY (`user_email`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;
COMMIT;

-- -----------------------------------------------------
-- Table `paymybuddy`.`user`
-- -----------------------------------------------------
START TRANSACTION;
CREATE TABLE IF NOT EXISTS `paymybuddy`.`user` (
  `email` VARCHAR(128) NOT NULL,
  `first_name` VARCHAR(32) NULL DEFAULT NULL,
  `last_name` VARCHAR(32) NULL DEFAULT NULL,
  `money_available` INT NOT NULL,
  `password` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`email`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;
COMMIT;

-- -----------------------------------------------------
-- Table `paymybuddy`.`user_bank_account`
-- -----------------------------------------------------
START TRANSACTION;
CREATE TABLE IF NOT EXISTS `paymybuddy`.`user_bank_account` (
  `user_email` VARCHAR(128) NOT NULL,
  `bank_account_iban` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`user_email`, `bank_account_iban`),
  UNIQUE INDEX `UK_26id9xbjtpmqa9vipj6gtwxd7` (`bank_account_iban` ASC) VISIBLE,
  CONSTRAINT `FKa1rsepwfa341xq647kyf6f6k1`
    FOREIGN KEY (`user_email`)
    REFERENCES `paymybuddy`.`user` (`email`),
  CONSTRAINT `FKnfb93k7la9lhhc00gpw4n5fxv`
    FOREIGN KEY (`bank_account_iban`)
    REFERENCES `paymybuddy`.`bank_account` (`iban`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;
COMMIT;


-- -----------------------------------------------------
-- Table `paymybuddy`.`user_transaction`
-- -----------------------------------------------------
START TRANSACTION;
CREATE TABLE IF NOT EXISTS `paymybuddy`.`user_transaction` (
  `user_email` VARCHAR(128) NOT NULL,
  `transaction_user_email` VARCHAR(128) NOT NULL,
  PRIMARY KEY (`user_email`, `transaction_user_email`),
  UNIQUE INDEX `UK_boor870hnp16ufret0s6i1wuj` (`transaction_user_email` ASC) VISIBLE,
  CONSTRAINT `FKau86iypnjte32r8x8xf6jk070`
    FOREIGN KEY (`user_email`)
    REFERENCES `paymybuddy`.`user` (`email`),
  CONSTRAINT `FKfvhhej5f3735icepybc663jaa`
    FOREIGN KEY (`transaction_user_email`)
    REFERENCES `paymybuddy`.`transaction` (`user_email`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;
COMMIT;

START TRANSACTION;
INSERT INTO `paymybuddy`.`user`
(`email`,
`first_name`,
`last_name`,
`money_available`,
`password`)
VALUES
('emailbuddy',
'firstNamebuddy',
'lastNamebuddy',
'0',
'$2y$10$0.JBClw5A2IoueLiWfMQluCPesKq0ykyZEOzu7ZEv8ElHnvrxdP.i');
COMMIT;

START TRANSACTION;
INSERT INTO `paymybuddy`.`bank_account`
(`iban`,
`description`)
VALUES
('ibanbuddy',
'descriptionbuddy');
COMMIT;

START TRANSACTION;
INSERT INTO `paymybuddy`.`transaction`
(`user_email`,
`amount`,
`date`,
`description`,
`user_email_receiver`)
VALUES
('emailbuddy',
10,
'2001/01/01',
'descriptionbuddy',
'userEmailReceiver');
COMMIT;
