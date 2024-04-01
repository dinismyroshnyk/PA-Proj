/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     06/03/2024 17:45:02                          */
/*==============================================================*/
drop database if exists projeto;
CREATE DATABASE projeto;
USE projeto;

drop table if exists ANOTACOES;

drop table if exists LICENCAS;

drop table if exists OBRAS;

drop table if exists REVISOES;

drop table if exists UTILIZADORES;

/*==============================================================*/
/* Table: ANOTACOES                                             */
/*==============================================================*/
create table ANOTACOES
(
   ID_ANOTACAO          int not null AUTO_INCREMENT,
   ID_REVISAO           int,
   DESCRICAO            text not null,
   PAGINA               int not null,
   PARAGRAFO            int not null,
   DATA                 date not null,
   primary key (ID_ANOTACAO)
);

/*==============================================================*/
/* Table: LICENCAS                                              */
/*==============================================================*/
create table LICENCAS
(
   ID_LICENCA           int not null AUTO_INCREMENT,
   NUMERO               varchar(20) not null,
   DATA_INICIO          date not null,
   DATA_FIM             date not null,
   N_DISPONIVEL         int not null,
   COMENTARIOS          varchar(100),
   primary key (ID_LICENCA)
);

/*==============================================================*/
/* Table: OBRAS                                                 */
/*==============================================================*/
create table OBRAS
(
   ID_OBRA              int not null AUTO_INCREMENT,
   ID_UTILIZADOR        int not null,
   TITULO               varchar(100) not null,
   SUBTITULO            varchar(100),
   ESTILO_LITERARIO     varchar(20) not null,
   TIPO_PUBLICACAO      varchar(20) not null,
   N_PAGINAS            int not null,
   N_PALAVRAS           int not null,
   CODIGO_ISBN          varchar(13) not null,
   N_EDICAO             int not null,
   DATA_SUBMISSAO       date not null,
   DATA_APROVACAO       date,
   primary key (ID_OBRA)
);

/*==============================================================*/
/* Table: REVISOES                                              */
/*==============================================================*/
create table REVISOES
(
   ID_REVISAO           int not null,
   ID_OBRA              int not null,
   DATA_SUBMISSAO       datetime not null,
   TEMPO_DECORRIDO      time not null,
   N_SERIE              varchar(20) not null,
   CUSTO                decimal(2,2),
   ESTADO               ENUM('initiated', 'accepted', 'in_progress', 'completed', 'archived') not null,
   primary key (ID_REVISAO)
);

/*==============================================================*/
/* Table: REVISOES_LICENCAS                                     */
/*==============================================================*/
create table REVISOES_LICENCAS
(
   ID_REVISAO            int not null,
   ID_LICENCA            int not null,
   primary key (ID_REVISAO, ID_LICENCA),
   foreign key (ID_REVISAO) references REVISOES (ID_REVISAO),
   foreign key (ID_LICENCA) references LICENCAS (ID_LICENCA)
);

/*==============================================================*/
/* Table: UTILIZADORES                                          */
/*==============================================================*/
create table UTILIZADORES
(
   ID_UTILIZADOR        int not null AUTO_INCREMENT,
   NOME                 varchar(100) not null,
   USERNAME             varchar(20),
   PASSWORD             varchar(60),
   SALT                 varbinary(16),
   ESTADO               ENUM('active', 'inactive', 'pending-activation', 'pending-deletion', 'deleted') not null,
   EMAIL                varchar(100),
   TIPO                 ENUM('manager', 'author', 'reviewer'),
   CONTRIBUINTE         varchar(9),
   TELEFONE             varchar(9),
   MORADA               varchar(100),
   ESTILO_LITERARIO     varchar(20),
   DATA_INICIO          date,
   AREA_ESPECIALIZACAO  varchar(20),
   FORMACAO_ACADEMICA   varchar(100),
   primary key (ID_UTILIZADOR)
);

/*==============================================================*/
/* Table: REVISOES_UTILIZADORES                                 */
/*==============================================================*/
create table REVISOES_UTILIZADORES
(
   ID_REVISAO            int not null,
   ID_UTILIZADOR         int not null,
   primary key (ID_REVISAO, ID_UTILIZADOR),
   foreign key (ID_REVISAO) references REVISOES (ID_REVISAO),
   foreign key (ID_UTILIZADOR) references UTILIZADORES (ID_UTILIZADOR)
);

alter table ANOTACOES add constraint FK_REVISOES_ANOTACOES foreign key (ID_REVISAO)
   references REVISOES (ID_REVISAO) on delete restrict on update restrict;

alter table OBRAS add constraint FK_UTILIZADORES_OBRAS foreign key (ID_UTILIZADOR)
   references UTILIZADORES (ID_UTILIZADOR) on delete restrict on update restrict;

alter table REVISOES add constraint FK_OBRAS_REVISOES foreign key (ID_OBRA)
   references OBRAS (ID_OBRA) on delete restrict on update restrict;

/*==============================================================*/
/* Enable event scheduler                                       */
/*==============================================================*/
SET GLOBAL event_scheduler = ON;

/*==============================================================*/
/* Event: FREE_USER_CREDENTIALS                                 */
/*==============================================================*/
CREATE EVENT FREE_USER_CREDENTIALS
ON SCHEDULE EVERY 2 MINUTE
STARTS CURRENT_DATE
DO
   UPDATE UTILIZADORES
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
   DELETE FROM LICENCAS
   WHERE DATA_FIM < CURRENT_DATE;

/*==============================================================*/
/* Event: UPDATE_ELAPSED_TIME_EVENT                             */
/*==============================================================*/
CREATE EVENT UPDATE_ELAPSED_TIME_EVENT
ON SCHEDULE EVERY 1 MINUTE
DO
   UPDATE REVISOES
   SET TEMPO_DECORRIDO = SEC_TO_TIME(TIMESTAMPDIFF(SECOND, DATA_SUBMISSAO, NOW()))
   WHERE ESTADO IN ('initiated', 'accepted', 'in_progress');

/*==============================================================*/
/* Trigger: UPDATE_ELAPSED_TIME_TRIGGER                         */
/*==============================================================*/
CREATE TRIGGER UPDATE_ELAPSED_TIME_TRIGGER
BEFORE UPDATE ON REVISOES
FOR EACH ROW
BEGIN
   IF NEW.ESTADO IN ('initiated', 'accepted', 'in_progress', 'completed', 'archived')
   AND OLD.ESTADO != 'completed' THEN
      SET NEW.TEMPO_DECORRIDO = SEC_TO_TIME(TIMESTAMPDIFF(SECOND, NEW.DATA_SUBMISSAO, NOW()));
   END IF;
END;