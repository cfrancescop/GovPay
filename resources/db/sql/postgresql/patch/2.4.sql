--GP-523

CREATE SEQUENCE seq_tracciati start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE tracciati
(
       data_caricamento TIMESTAMP NOT NULL,
       data_ultimo_aggiornamento TIMESTAMP NOT NULL,
       stato VARCHAR(255) NOT NULL,
       linea_elaborazione BIGINT NOT NULL,
       descrizione_stato VARCHAR(1024),
       num_linee_totali BIGINT NOT NULL,
       num_operazioni_ok BIGINT NOT NULL,
       num_operazioni_ko BIGINT NOT NULL,
       nome_file VARCHAR(255) NOT NULL,
       raw_data_richiesta BYTEA NOT NULL,
       raw_data_risposta BYTEA,
       -- fk/pk columns
       id BIGINT DEFAULT nextval('seq_tracciati') NOT NULL,
       id_operatore BIGINT,
       id_applicazione BIGINT,
       -- check constraints
       CONSTRAINT chk_tracciati_1 CHECK (stato IN ('ANNULLATO','NUOVO','IN_CARICAMENTO','CARICAMENTO_OK','CARICAMENTO_KO')),
       -- fk/pk keys constraints
       CONSTRAINT fk_tracciati_1 FOREIGN KEY (id_operatore) REFERENCES operatori(id),
       CONSTRAINT fk_tracciati_2 FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id),
       CONSTRAINT pk_tracciati PRIMARY KEY (id)
);




CREATE SEQUENCE seq_operazioni start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE operazioni
(
       tipo_operazione VARCHAR(255) NOT NULL,
       linea_elaborazione BIGINT NOT NULL,
       stato VARCHAR(255) NOT NULL,
       dati_richiesta BYTEA NOT NULL,
       dati_risposta BYTEA,
       dettaglio_esito VARCHAR(255),
       cod_versamento_ente VARCHAR(255),
       -- fk/pk columns
       id BIGINT DEFAULT nextval('seq_operazioni') NOT NULL,
       id_tracciato BIGINT NOT NULL,
       id_applicazione BIGINT,
       -- check constraints
       CONSTRAINT chk_operazioni_1 CHECK (stato IN ('NON_VALIDO','ESEGUITO_OK','ESEGUITO_KO')),
       -- fk/pk keys constraints
       CONSTRAINT fk_operazioni_1 FOREIGN KEY (id_tracciato) REFERENCES tracciati(id),
       CONSTRAINT fk_operazioni_2 FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id),
       CONSTRAINT pk_operazioni PRIMARY KEY (id)
);

--GP-524

CREATE SEQUENCE seq_gp_audit start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE gp_audit
(
       data TIMESTAMP NOT NULL,
       id_oggetto BIGINT NOT NULL,
       tipo_oggetto VARCHAR(255) NOT NULL,
       oggetto TEXT NOT NULL,
       -- fk/pk columns
       id BIGINT DEFAULT nextval('seq_gp_audit') NOT NULL,
       id_operatore BIGINT NOT NULL,
       -- fk/pk keys constraints
       CONSTRAINT fk_gp_audit_1 FOREIGN KEY (id_operatore) REFERENCES operatori(id),
       CONSTRAINT pk_gp_audit PRIMARY KEY (id)
);

--GP-526

CREATE SEQUENCE seq_ruoli start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

CREATE TABLE ruoli
(
       cod_ruolo VARCHAR(35) NOT NULL,
       descrizione VARCHAR(255) NOT NULL,
       -- fk/pk columns
       id BIGINT DEFAULT nextval('seq_ruoli') NOT NULL,
       -- unique constraints
       CONSTRAINT unique_ruoli_1 UNIQUE (cod_ruolo),
       -- fk/pk keys constraints
       CONSTRAINT pk_ruoli PRIMARY KEY (id)
);

ALTER TABLE acl ADD COLUMN diritti INT;
ALTER TABLE acl ALTER COLUMN cod_servizio TYPE VARCHAR(35);

ALTER TABLE acl ADD COLUMN id_ruolo BIGINT;
ALTER TABLE acl ADD CONSTRAINT fk_acl_6 FOREIGN KEY (id_ruolo) REFERENCES ruoli(id);
ALTER TABLE domini ADD COLUMN logo BYTEA;

ALTER TABLE operatori ALTER COLUMN profilo TYPE VARCHAR(1024);
ALTER TABLE acl ADD COLUMN amministratore BOOLEAN NOT NULL DEFAULT false;

UPDATE operatori SET profilo = 'Amministratore' where profilo = 'A';
UPDATE operatori SET profilo = 'Operatore' where profilo = 'E';

INSERT INTO ruoli (cod_ruolo,descrizione) VALUES ('Amministratore', 'Ruolo Amministratore');
INSERT INTO ruoli (cod_ruolo,descrizione) VALUES ('Operatore', 'Ruolo Operatore');

INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'A_PPA', 2, id from ruoli where cod_ruolo ='Amministratore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'A_CON', 2, id from ruoli where cod_ruolo ='Amministratore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'A_APP', 2, id from ruoli where cod_ruolo ='Amministratore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'A_USR', 2, id from ruoli where cod_ruolo ='Amministratore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo,amministratore) select 'D', 'G_PAG', 2, id, true from ruoli where cod_ruolo ='Amministratore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'G_RND', 2, id from ruoli where cod_ruolo ='Amministratore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'GDE', 2, id from ruoli where cod_ruolo ='Amministratore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'MAN', 2, id from ruoli where cod_ruolo ='Amministratore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'STAT', 2, id from ruoli where cod_ruolo ='Amministratore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'G_PAG', 1, id from ruoli where cod_ruolo ='Operatore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'G_RND', 1, id from ruoli where cod_ruolo ='Operatore';
INSERT INTO acl (cod_tipo,cod_servizio,diritti,id_ruolo) select 'D', 'GDE', 1, id from ruoli where cod_ruolo ='Operatore';

insert into sonde(nome, classe, soglia_warn, soglia_error) values ('caricamento-tracciati', 'org.openspcoop2.utils.sonde.impl.SondaBatch', 3600000, 21600000);
insert into sonde(nome, classe, soglia_warn, soglia_error) values ('check-tracciati', 'org.openspcoop2.utils.sonde.impl.SondaCoda', 1, 1);
insert into sonde(nome, classe, soglia_warn, soglia_error) values ('cons-req', 'org.openspcoop2.utils.sonde.impl.SondaBatch', 86400000, 172800000);
insert into sonde(nome, classe, soglia_warn, soglia_error) values ('cons-esito', 'org.openspcoop2.utils.sonde.impl.SondaBatch', 86400000, 172800000);

--GP-525
ALTER TABLE rpt ADD COLUMN stato_conservazione VARCHAR(35);
ALTER TABLE rpt ADD COLUMN descrizione_stato_cons VARCHAR(512);
ALTER TABLE rpt ADD COLUMN data_conservazione TIMESTAMP;
