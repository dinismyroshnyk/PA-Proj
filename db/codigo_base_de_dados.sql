/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/03/2024 17:45:02                          */
/*==============================================================*/
DROP DATABASE IF EXISTS PA-PROJ-MAIN;
CREATE DATABASE PA-PROJ-MAIN;
USE PA-PROJ-MAIN;

DROP TABLE IF EXISTS NOTES;
DROP TABLE IF EXISTS LICENSES;
DROP TABLE IF EXISTS BOOKS;
DROP TABLE IF EXISTS REVIEWS;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS REVIEWS_LICENSES;
DROP TABLE IF EXISTS REVIEWS_USERS;

/*==============================================================*/
/* Table: NOTES                                                 */
/*==============================================================*/
CREATE TABLE NOTES
(
   NOTE_ID               INT NOT NULL AUTO_INCREMENT,
   REVIEW_ID             INT NOT NULL,
   CONTENT               TEXT NOT NULL,
   PAGE                  INT NOT NULL,
   PARAGRAPH             INT NOT NULL,
   DATE                  DATE NOT NULL,
   PRIMARY KEY (NOTE_ID)
);

/*==============================================================*/
/* Table: LICENSES                                              */
/*==============================================================*/
CREATE TABLE LICENSES
(
   LICENSE_ID            INT NOT NULL AUTO_INCREMENT,
   NUMBER                VARCHAR(20) NOT NULL,
   VALID_FROM            DATE NOT NULL,
   VALID_TO              DATE NOT NULL,
   USAGE_LIMIT           INT NOT NULL,
   COMMENTS              VARCHAR(100),
   PRIMARY KEY (LICENSE_ID)
);

/*==============================================================*/
/* Table: BOOKS                                                 */
/*==============================================================*/
CREATE TABLE BOOKS
(
   BOOK_ID               INT NOT NULL AUTO_INCREMENT,
   USER_ID               INT NOT NULL,
   TITLE                 VARCHAR(100) NOT NULL,
   SUBTITLE              VARCHAR(100),
   LITERARY_STYLE        VARCHAR(20) NOT NULL,
   PUBLICATION_TYPE      VARCHAR(20) NOT NULL,
   PAGE_NUMBER           INT NOT NULL,
   WORD_NUMBER           INT NOT NULL,
   ISDN                  VARCHAR(13) NOT NULL,
   EDITION               INT NOT NULL,
   SUBMISSION_DATE       DATE NOT NULL,
   APPROVAL_DATE         DATE,
   PRIMARY KEY (BOOK_ID)
);

/*==============================================================*/
/* Table: REVIEWS                                               */
/*==============================================================*/
CREATE TABLE REVIEWS
(
   REVIEW_ID             INT NOT NULL,
   BOOK_ID               INT NOT NULL,
   SUBMISSION_DATE       DATETIME NOT NULL,
   ELAPSED_TIME          TIME NOT NULL,
   SERIAL_NUMBER         VARCHAR(20) NOT NULL,
   COST                  DECIMAL(2,2),
   STATUS                ENUM('initiated', 'accepted', 'in_progress', 'completed', 'archived') NOT NULL,
   PRIMARY KEY (REVIEW_ID)
);

/*==============================================================*/
/* Table: REVIEWS LICENSES                                      */
/*==============================================================*/
CREATE TABLE REVIEWS_LICENSES
(
   REVIEW_ID             INT NOT NULL,
   LICENSE_ID            INT NOT NULL,
   PRIMARY KEY (REVIEW_ID, LICENSE_ID),
   FOREIGN KEY (REVIEW_ID) REFERENCES REVIEWS (REVIEW_ID),
   FOREIGN KEY (LICENSE_ID) REFERENCES LICENSES (LICENSE_ID)
);

/*==============================================================*/
/* Table: USERS                                                 */
/*==============================================================*/
CREATE TABLE USERS
(
   USER_ID               INT NOT NULL AUTO_INCREMENT,
   NAME                  VARCHAR(100) NOT NULL,
   USERNAME              VARCHAR(20),
   PASSWORD              VARCHAR(60),
   SALT                  VARBINARY(16),
   STATUS                ENUM('active', 'inactive', 'pending-activatiON', 'pending-deletiON', 'DELETEd') NOT NULL,
   EMAIL                 VARCHAR(100),
   USER_TYPE             ENUM('manager', 'author', 'reviewer'),
   NIF                   VARCHAR(9),
   PHONE                 VARCHAR(9),
   ADDRESS               VARCHAR(100),
   LITERARY_STYLE        VARCHAR(20),
   START_DATE            DATE,
   SPECIALIZATION        VARCHAR(20),
   ACADEMIC_BACKGROUND   VARCHAR(100),
   PRIMARY KEY (USER_ID)
);

/*==============================================================*/
/* Table: REVIEWS_USERS                                         */
/*==============================================================*/
CREATE TABLE REVIEWS_USERS
(
   REVIEW_ID             INT NOT NULL,
   USER_ID               INT NOT NULL,
   STATUS                ENUM('accepted', 'rejected'),
   PRIMARY KEY (REVIEW_ID, USER_ID),
   FOREIGN KEY (REVIEW_ID) REFERENCES REVIEWS (REVIEW_ID),
   FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID)
);

ALTER TABLE NOTES ADD CONSTRAINT FK_REVIEWS_NOTES FOREIGN KEY (REVIEW_ID)
   REFERENCES REVIEWS (REVIEW_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE BOOKS ADD CONSTRAINT FK_USERS_BOOKS FOREIGN KEY (USER_ID)
   REFERENCES USERS (USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE REVIEWS ADD CONSTRAINT FK_BOOKS_REVIEWS FOREIGN KEY (BOOK_ID)
   REFERENCES BOOKS (BOOK_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

/*==============================================================*/
/* Enable event scheduler                                       */
/*==============================================================*/
SET GLOBAL EVENT_SCHEDULER = ON;

/*==============================================================*/
/* Event: FREE_USER_CREDENTIALS                                 */
/*==============================================================*/
CREATE EVENT FREE_USER_CREDENTIALS
ON SCHEDULE EVERY 2 MINUTE
STARTS CURRENT_DATE
DO
   UPDATE USERS
   SET USERNAME = NULL, PASSWORD = NULL, SALT = NULL,
         EMAIL = NULL, TIPO = NULL, CONTRIBUINTE = NULL,
         TELEFONE = NULL, MORADA = NULL, ESTILO_LITERARIO = NULL,
         DATA_INICIO = NULL, AREA_ESPECIALIZACAO = NULL,
         FORMACAO_ACADEMICA = NULL
   WHERE ESTADO = 'deleted';

/*==============================================================*/
/* Event: REMOVE_EXPIRED_LICENSES                               */
/*==============================================================*/
CREATE EVENT REMOVE_EXPIRED_LICENSES
ON SCHEDULE EVERY 2 MINUTE
STARTS CURRENT_DATE
DO
   DELETE FROM LICENSES
   WHERE DATA_FIM < CURRENT_DATE;

/*==============================================================*/
/* Event: UPDATE_ELAPSED_TIME_EVENT                             */
/*==============================================================*/
CREATE EVENT UPDATE_ELAPSED_TIME_EVENT
ON SCHEDULE EVERY 1 MINUTE
DO
   UPDATE REVIEWS
   SET TEMPO_DECORRIDO = SEC_TO_TIME(TIMESTAMPDIFF(SECOND, DATA_SUBMISSAO, NOW()))
   WHERE ESTADO in ('initiated', 'accepted', 'in_progress');

/*==============================================================*/
/* Trigger: UPDATE_ELAPSED_TIME_TRIGGER                         */
/*==============================================================*/
CREATE TRIGGER UPDATE_ELAPSED_TIME_TRIGGER
BEFORE UPDATE ON REVIEWS
FOR EACH ROW
BEGIN
   IF NEW.ESTADO IN ('initiated', 'accepted', 'in_progress', 'completed', 'archived')
   AND OLD.ESTADO != 'completed' THEN
      SET NEW.TEMPO_DECORRIDO = SEC_TO_TIME(TIMESTAMPDIFF(SECOND, NEW.DATA_SUBMISSAO, NOW()));
   END IF;
END;