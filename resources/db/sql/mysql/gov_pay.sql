CREATE TABLE psp
(
	cod_psp VARCHAR(35) NOT NULL,
	ragione_sociale VARCHAR(70) NOT NULL,
	url_info VARCHAR(255),
	abilitato BOOLEAN NOT NULL,
	storno BOOLEAN NOT NULL,
	marca_bollo BOOLEAN NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_psp_1 UNIQUE (cod_psp),
	-- fk/pk keys constraints
	CONSTRAINT pk_psp PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_psp_1 ON psp (cod_psp);



CREATE TABLE canali
(
	cod_canale VARCHAR(35) NOT NULL,
	cod_intermediario VARCHAR(35) NOT NULL,
	tipo_versamento VARCHAR(4) NOT NULL,
	modello_pagamento INT NOT NULL,
	disponibilita LONGTEXT,
	descrizione LONGTEXT,
	condizioni VARCHAR(35),
	url_info VARCHAR(255),
	abilitato BOOLEAN NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_psp BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_canali_1 UNIQUE (id_psp,cod_canale,tipo_versamento),
	-- fk/pk keys constraints
	CONSTRAINT fk_can_id_psp FOREIGN KEY (id_psp) REFERENCES psp(id),
	CONSTRAINT pk_canali PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_canali_1 ON canali (id_psp,cod_canale,tipo_versamento);



CREATE TABLE intermediari
(
	cod_intermediario VARCHAR(35) NOT NULL,
	cod_connettore_pdd VARCHAR(35) NOT NULL,
	denominazione VARCHAR(255) NOT NULL,
	abilitato BOOLEAN NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_intermediari_1 UNIQUE (cod_intermediario),
	-- fk/pk keys constraints
	CONSTRAINT pk_intermediari PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_intermediari_1 ON intermediari (cod_intermediario);



CREATE TABLE stazioni
(
	cod_stazione VARCHAR(35) NOT NULL,
	password VARCHAR(35) NOT NULL,
	abilitato BOOLEAN NOT NULL,
	application_code INT NOT NULL,
	ndp_stato INT,
	ndp_operazione VARCHAR(256),
	ndp_descrizione VARCHAR(1024),
	ndp_data TIMESTAMP(3),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_intermediario BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_stazioni_1 UNIQUE (cod_stazione),
	-- fk/pk keys constraints
	CONSTRAINT fk_stz_id_intermediario FOREIGN KEY (id_intermediario) REFERENCES intermediari(id),
	CONSTRAINT pk_stazioni PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_stazioni_1 ON stazioni (cod_stazione);



CREATE TABLE applicazioni
(
	cod_applicazione VARCHAR(35) NOT NULL,
	abilitato BOOLEAN NOT NULL,
	principal VARCHAR(255) NOT NULL,
	firma_ricevuta VARCHAR(1) NOT NULL,
	cod_connettore_esito VARCHAR(255),
	cod_connettore_verifica VARCHAR(255),
	versione VARCHAR(10) NOT NULL DEFAULT '2.1',
	trusted BOOLEAN NOT NULL,
	cod_applicazione_iuv VARCHAR(3),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_applicazioni_1 UNIQUE (cod_applicazione),
	CONSTRAINT unique_applicazioni_2 UNIQUE (principal),
	-- fk/pk keys constraints
	CONSTRAINT pk_applicazioni PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index/oracle/gov_pay.sql
CREATE INDEX index_applicazioni_1 ON applicazioni (cod_applicazione);
CREATE INDEX index_applicazioni_2 ON applicazioni (principal);



CREATE TABLE domini
(
	cod_dominio VARCHAR(35) NOT NULL,
	gln VARCHAR(35) NOT NULL,
	abilitato BOOLEAN NOT NULL,
	ragione_sociale VARCHAR(70) NOT NULL,
	xml_conti_accredito MEDIUMBLOB NOT NULL,
	xml_tabella_controparti MEDIUMBLOB NOT NULL,
	riuso_iuv BOOLEAN NOT NULL,
	custom_iuv BOOLEAN NOT NULL,
	aux_digit INT NOT NULL DEFAULT 0,
	iuv_prefix VARCHAR(255),
	iuv_prefix_strict BOOLEAN NOT NULL DEFAULT false,
	segregation_code INT,
	ndp_stato INT,
	ndp_operazione VARCHAR(256),
	ndp_descrizione VARCHAR(1024),
	ndp_data TIMESTAMP(3),
	logo MEDIUMBLOB,
	cbill VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_stazione BIGINT NOT NULL,
	id_applicazione_default BIGINT,
	-- unique constraints
	CONSTRAINT unique_domini_1 UNIQUE (cod_dominio),
	-- fk/pk keys constraints
	CONSTRAINT fk_dom_id_stazione FOREIGN KEY (id_stazione) REFERENCES stazioni(id),
	CONSTRAINT fk_dom_id_applicazione_default FOREIGN KEY (id_applicazione_default) REFERENCES applicazioni(id),
	CONSTRAINT pk_domini PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_domini_1 ON domini (cod_dominio);



CREATE TABLE uo
(
	cod_uo VARCHAR(35) NOT NULL,
	abilitato BOOLEAN NOT NULL,
	uo_codice_identificativo VARCHAR(35),
	uo_denominazione VARCHAR(70),
	uo_indirizzo VARCHAR(70),
	uo_civico VARCHAR(16),
	uo_cap VARCHAR(16),
	uo_localita VARCHAR(35),
	uo_provincia VARCHAR(35),
	uo_nazione VARCHAR(2),
	uo_area VARCHAR(255),
	uo_url_sito_web VARCHAR(255),
	uo_email VARCHAR(255),
	uo_pec VARCHAR(255),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_dominio BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_uo_1 UNIQUE (cod_uo,id_dominio),
	-- fk/pk keys constraints
	CONSTRAINT fk_uo_id_dominio FOREIGN KEY (id_dominio) REFERENCES domini(id),
	CONSTRAINT pk_uo PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_uo_1 ON uo (cod_uo,id_dominio);



CREATE TABLE operatori
(
	principal VARCHAR(255) NOT NULL,
	nome VARCHAR(35) NOT NULL,
	profilo VARCHAR(1024) NOT NULL,
	abilitato BOOLEAN NOT NULL DEFAULT true,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_operatori_1 UNIQUE (principal),
	-- fk/pk keys constraints
	CONSTRAINT pk_operatori PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_operatori_1 ON operatori (principal);



CREATE TABLE connettori
(
	cod_connettore VARCHAR(255) NOT NULL,
	cod_proprieta VARCHAR(255) NOT NULL,
	valore VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_connettori_1 UNIQUE (cod_connettore,cod_proprieta),
	-- fk/pk keys constraints
	CONSTRAINT pk_connettori PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_connettori_1 ON connettori (cod_connettore,cod_proprieta);



CREATE TABLE portali
(
	cod_portale VARCHAR(35) NOT NULL,
	default_callback_url VARCHAR(512) NOT NULL,
	principal VARCHAR(255) NOT NULL,
	versione VARCHAR(10) NOT NULL DEFAULT '2.1',
	trusted BOOLEAN NOT NULL,
	abilitato BOOLEAN NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_portali_1 UNIQUE (cod_portale),
	CONSTRAINT unique_portali_2 UNIQUE (principal),
	-- fk/pk keys constraints
	CONSTRAINT pk_portali PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_portali_1 ON portali (cod_portale);
CREATE INDEX index_portali_2 ON portali (principal);



CREATE TABLE iban_accredito
(
	cod_iban VARCHAR(255) NOT NULL,
	id_seller_bank VARCHAR(255),
	id_negozio VARCHAR(255),
	bic_accredito VARCHAR(255),
	iban_appoggio VARCHAR(255),
	bic_appoggio VARCHAR(255),
	postale BOOLEAN NOT NULL,
	attivato BOOLEAN NOT NULL,
	abilitato BOOLEAN NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_dominio BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_iban_accredito_1 UNIQUE (cod_iban,id_dominio),
	-- fk/pk keys constraints
	CONSTRAINT fk_iba_id_dominio FOREIGN KEY (id_dominio) REFERENCES domini(id),
	CONSTRAINT pk_iban_accredito PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_iban_accredito_1 ON iban_accredito (cod_iban,id_dominio);



CREATE TABLE tipi_tributo
(
	cod_tributo VARCHAR(255) NOT NULL,
	descrizione VARCHAR(255),
	tipo_contabilita VARCHAR(1),
	cod_contabilita VARCHAR(255),
	cod_tributo_iuv VARCHAR(4),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_tipi_tributo_1 UNIQUE (cod_tributo),
	-- fk/pk keys constraints
	CONSTRAINT pk_tipi_tributo PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_tipi_tributo_1 ON tipi_tributo (cod_tributo);



CREATE TABLE tributi
(
	abilitato BOOLEAN NOT NULL,
	tipo_contabilita VARCHAR(1),
	codice_contabilita VARCHAR(255),
	cod_tributo_iuv VARCHAR(4),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_dominio BIGINT NOT NULL,
	id_iban_accredito BIGINT,
	id_tipo_tributo BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_tributi_1 UNIQUE (id_dominio,id_tipo_tributo),
	-- fk/pk keys constraints
	CONSTRAINT fk_trb_id_dominio FOREIGN KEY (id_dominio) REFERENCES domini(id),
	CONSTRAINT fk_trb_id_iban_accredito FOREIGN KEY (id_iban_accredito) REFERENCES iban_accredito(id),
	CONSTRAINT fk_trb_id_tipo_tributo FOREIGN KEY (id_tipo_tributo) REFERENCES tipi_tributo(id),
	CONSTRAINT pk_tributi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_tributi_1 ON tributi (id_dominio,id_tipo_tributo);


CREATE TABLE ruoli
(
	cod_ruolo VARCHAR(35) NOT NULL,
	descrizione VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_ruoli_1 UNIQUE (cod_ruolo),
	-- fk/pk keys constraints
	CONSTRAINT pk_ruoli PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE UNIQUE INDEX index_ruoli_1 ON ruoli (cod_ruolo);



CREATE TABLE acl
(
	cod_tipo VARCHAR(1) NOT NULL,
	diritti INT,
	cod_servizio VARCHAR(35) NOT NULL,
	amministratore BOOLEAN NOT NULL DEFAULT false,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_applicazione BIGINT,
	id_portale BIGINT,
	id_operatore BIGINT,
	id_ruolo BIGINT,
	id_dominio BIGINT,
	id_tipo_tributo BIGINT,
	-- fk/pk keys constraints
	CONSTRAINT fk_acl_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id),
	CONSTRAINT fk_acl_id_portale FOREIGN KEY (id_portale) REFERENCES portali(id),
	CONSTRAINT fk_acl_id_operatore FOREIGN KEY (id_operatore) REFERENCES operatori(id),
	CONSTRAINT fk_acl_id_ruolo FOREIGN KEY (id_ruolo) REFERENCES ruoli(id),
	CONSTRAINT fk_acl_id_dominio FOREIGN KEY (id_dominio) REFERENCES domini(id),
	CONSTRAINT fk_acl_id_tipo_tributo FOREIGN KEY (id_tipo_tributo) REFERENCES tipi_tributo(id),
	CONSTRAINT pk_acl PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;




CREATE TABLE versamenti
(
	cod_versamento_ente VARCHAR(35) NOT NULL,
	importo_totale DOUBLE NOT NULL,
	stato_versamento VARCHAR(35) NOT NULL,
	descrizione_stato VARCHAR(255),
	-- Indica se, decorsa la dataScadenza, deve essere aggiornato da remoto o essere considerato scaduto
	aggiornabile BOOLEAN NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_creazione TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
    -- Per versioni successive alla 5.7, rimuovere dalla sql_mode NO_ZERO_DATE 
	data_scadenza TIMESTAMP(3), 
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ora_ultimo_aggiornamento TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	causale_versamento VARCHAR(1024),
	debitore_identificativo VARCHAR(35) NOT NULL,
	debitore_anagrafica VARCHAR(70) NOT NULL,
	debitore_indirizzo VARCHAR(70),
	debitore_civico VARCHAR(16),
	debitore_cap VARCHAR(16),
	debitore_localita VARCHAR(35),
	debitore_provincia VARCHAR(35),
	debitore_nazione VARCHAR(2),
	debitore_email VARCHAR(256),
	debitore_telefono VARCHAR(35),
	debitore_cellulare VARCHAR(35),
	debitore_fax VARCHAR(35),
	cod_lotto VARCHAR(35),
	cod_versamento_lotto VARCHAR(35),
	cod_anno_tributario VARCHAR(35),
	cod_bundlekey VARCHAR(256),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_uo BIGINT NOT NULL,
	id_applicazione BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_versamenti_1 UNIQUE (cod_versamento_ente,id_applicazione),
	-- fk/pk keys constraints
	CONSTRAINT fk_vrs_id_uo FOREIGN KEY (id_uo) REFERENCES uo(id),
	CONSTRAINT fk_vrs_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id),
	CONSTRAINT pk_versamenti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_versamenti_1 ON versamenti (cod_versamento_ente,id_applicazione);



CREATE TABLE singoli_versamenti
(
	cod_singolo_versamento_ente VARCHAR(70) NOT NULL,
	stato_singolo_versamento VARCHAR(35) NOT NULL,
	importo_singolo_versamento DOUBLE NOT NULL,
	anno_riferimento INT,
	-- MARCA BOLLO Valori possibili:\n01: Imposta di bollo
	tipo_bollo VARCHAR(2),
	-- MARCA BOLLO: Digest in Base64 del documento da bollare
	hash_documento VARCHAR(70),
	-- MARCA BOLLO: Sigla automobilistica della provincia di residenza
	provincia_residenza VARCHAR(2),
	tipo_contabilita VARCHAR(1),
	codice_contabilita VARCHAR(255),
	note VARCHAR(512),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_versamento BIGINT NOT NULL,
	id_tributo BIGINT,
	id_iban_accredito BIGINT,
	-- unique constraints
	CONSTRAINT unique_singoli_versamenti_1 UNIQUE (id_versamento,cod_singolo_versamento_ente),
	-- fk/pk keys constraints
	CONSTRAINT fk_sng_id_versamento FOREIGN KEY (id_versamento) REFERENCES versamenti(id),
	CONSTRAINT fk_sng_id_tributo FOREIGN KEY (id_tributo) REFERENCES tributi(id),
	CONSTRAINT fk_sng_id_iban_accredito FOREIGN KEY (id_iban_accredito) REFERENCES iban_accredito(id),
	CONSTRAINT pk_singoli_versamenti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_singoli_versamenti_1 ON singoli_versamenti (id_versamento,cod_singolo_versamento_ente);



CREATE TABLE rpt
(
	cod_carrello VARCHAR(35),
	iuv VARCHAR(35) NOT NULL,
	ccp VARCHAR(35) NOT NULL,
	cod_dominio VARCHAR(35) NOT NULL,
	-- Identificativo dell'RPT utilizzato come riferimento nell'RT
	cod_msg_richiesta VARCHAR(35) NOT NULL,
	-- Data di creazione dell'RPT
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_msg_richiesta TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	-- Stato RPT secondo la codifica AgID
	stato VARCHAR(35) NOT NULL,
	descrizione_stato LONGTEXT,
	cod_sessione VARCHAR(255),
	cod_sessione_portale VARCHAR(255),
	-- Indirizzo del portale psp a cui redirigere il cittadino per eseguire il pagamento
	psp_redirect_url VARCHAR(512),
	xml_rpt MEDIUMBLOB NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_aggiornamento_stato TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	-- Indirizzo di ritorno al portale dell'ente al termine del pagamento
	callback_url LONGTEXT,
	modello_pagamento VARCHAR(16) NOT NULL,
	cod_msg_ricevuta VARCHAR(35),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	-- Per versioni successive alla 5.7, rimuovere dalla sql_mode NO_ZERO_DATE 
	data_msg_ricevuta TIMESTAMP(3),
	firma_ricevuta VARCHAR(1) NOT NULL,
	-- Esito del pagamento:\n0: Eseguito\n1: Non eseguito\n2: Parzialmente eseguito\n3: Decorrenza\n4: Decorrenza Parziale
	cod_esito_pagamento INT,
	importo_totale_pagato DOUBLE,
	xml_rt MEDIUMBLOB,
	cod_stazione VARCHAR(35) NOT NULL,
	cod_transazione_rpt VARCHAR(36),
	cod_transazione_rt VARCHAR(36),
	stato_conservazione VARCHAR(35),
	descrizione_stato_cons VARCHAR(512),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_conservazione TIMESTAMP(3) DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_versamento BIGINT NOT NULL,
	id_canale BIGINT NOT NULL,
	id_portale BIGINT,
	-- unique constraints
	CONSTRAINT unique_rpt_1 UNIQUE (cod_msg_richiesta),
	CONSTRAINT unique_rpt_2 UNIQUE (iuv,ccp,cod_dominio),
	-- fk/pk keys constraints
	CONSTRAINT fk_rpt_id_versamento FOREIGN KEY (id_versamento) REFERENCES versamenti(id),
	CONSTRAINT fk_rpt_id_canale FOREIGN KEY (id_canale) REFERENCES canali(id),
	CONSTRAINT fk_rpt_id_portale FOREIGN KEY (id_portale) REFERENCES portali(id),
	CONSTRAINT pk_rpt PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_rpt_1 ON rpt (cod_msg_richiesta);
CREATE INDEX index_rpt_2 ON rpt (iuv,ccp,cod_dominio);
CREATE INDEX index_rpt_3 ON rpt (stato);
CREATE INDEX index_rpt_4 ON rpt (id_versamento);


CREATE TABLE rr
(
	cod_dominio VARCHAR(35) NOT NULL,
	iuv VARCHAR(35) NOT NULL,
	ccp VARCHAR(35) NOT NULL,
	cod_msg_revoca VARCHAR(35) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_msg_revoca TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
    -- Per versioni successive alla 5.7, rimuovere dalla sql_mode NO_ZERO_DATE 
	data_msg_esito TIMESTAMP(3),
	stato VARCHAR(35) NOT NULL,
	descrizione_stato VARCHAR(512),
	importo_totale_richiesto DOUBLE NOT NULL,
	cod_msg_esito VARCHAR(35),
	importo_totale_revocato DOUBLE,
	xml_rr MEDIUMBLOB NOT NULL,
	xml_er MEDIUMBLOB,
	cod_transazione_rr VARCHAR(36),
	cod_transazione_er VARCHAR(36),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_rpt BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_rr_1 UNIQUE (cod_msg_revoca),
	-- fk/pk keys constraints
	CONSTRAINT fk_rr_id_rpt FOREIGN KEY (id_rpt) REFERENCES rpt(id),
	CONSTRAINT pk_rr PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_rr_1 ON rr (cod_msg_revoca);



CREATE TABLE notifiche
(
	tipo_esito VARCHAR(16) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_creazione TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	stato VARCHAR(16) NOT NULL,
	descrizione_stato VARCHAR(255),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_aggiornamento_stato TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	-- DATETIME invece che TIMESTAMP(3) per supportare la data di default 31-12-9999
	data_prossima_spedizione DATETIME NOT NULL,
	tentativi_spedizione BIGINT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_applicazione BIGINT NOT NULL,
	id_rpt BIGINT,
	id_rr BIGINT,
	-- fk/pk keys constraints
	CONSTRAINT fk_ntf_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id),
	CONSTRAINT fk_ntf_id_rpt FOREIGN KEY (id_rpt) REFERENCES rpt(id),
	CONSTRAINT fk_ntf_id_rr FOREIGN KEY (id_rr) REFERENCES rr(id),
	CONSTRAINT pk_notifiche PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;




CREATE TABLE iuv
(
	prg BIGINT NOT NULL,
	iuv VARCHAR(35) NOT NULL,
	application_code INT NOT NULL,
	data_generazione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	tipo_iuv VARCHAR(1) NOT NULL,
	cod_versamento_ente VARCHAR(35),
	aux_digit INT NOT NULL DEFAULT 0,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_applicazione BIGINT NOT NULL,
	id_dominio BIGINT NOT NULL,
	-- unique constraints
	CONSTRAINT unique_iuv_1 UNIQUE (id_dominio,iuv),
	-- fk/pk keys constraints
	CONSTRAINT fk_iuv_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id),
	CONSTRAINT fk_iuv_id_dominio FOREIGN KEY (id_dominio) REFERENCES domini(id),
	CONSTRAINT pk_iuv PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_iuv_1 ON iuv (id_dominio,iuv);
CREATE INDEX index_iuv_2 ON iuv (cod_versamento_ente,tipo_iuv,id_applicazione);



CREATE TABLE fr
(
	cod_psp VARCHAR(35) NOT NULL,
	cod_dominio VARCHAR(35) NOT NULL,
	cod_flusso VARCHAR(35) NOT NULL,
	stato VARCHAR(35) NOT NULL,
	descrizione_stato LONGTEXT,
	iur VARCHAR(35) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
    -- Per versioni successive alla 5.7, rimuovere dalla sql_mode NO_ZERO_DATE 
	data_ora_flusso TIMESTAMP(3),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
    -- Per versioni successive alla 5.7, rimuovere dalla sql_mode NO_ZERO_DATE 
	data_regolamento TIMESTAMP(3),
	data_acquisizione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	numero_pagamenti BIGINT,
	importo_totale_pagamenti DOUBLE,
	cod_bic_riversamento VARCHAR(35),
	xml MEDIUMBLOB NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_fr_1 UNIQUE (cod_flusso),
	-- fk/pk keys constraints
	CONSTRAINT pk_fr PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_fr_1 ON fr (cod_flusso);



CREATE TABLE incassi
(
	trn VARCHAR(35) NOT NULL,
	cod_dominio VARCHAR(35) NOT NULL,
	causale VARCHAR(512) NOT NULL,
	importo DOUBLE NOT NULL,
	data_valuta TIMESTAMP DEFAULT CURRENT_TIMESTAMP(3),
	data_contabile TIMESTAMP DEFAULT  CURRENT_TIMESTAMP(3),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ora_incasso TIMESTAMP(3) NOT NULL DEFAULT  CURRENT_TIMESTAMP(3),
	nome_dispositivo VARCHAR(512),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_applicazione BIGINT,
	id_operatore BIGINT,
	-- fk/pk keys constraints
	CONSTRAINT fk_inc_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id),
	CONSTRAINT fk_inc_id_operatore FOREIGN KEY (id_operatore) REFERENCES operatori(id),
	CONSTRAINT pk_incassi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;




CREATE TABLE pagamenti
(
	cod_dominio VARCHAR(35) NOT NULL,
	iuv VARCHAR(35) NOT NULL,
	indice_dati INT NOT NULL,
	importo_pagato DOUBLE NOT NULL DEFAULT 1,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_acquisizione TIMESTAMP(3) NOT NULL DEFAULT 0,
	iur VARCHAR(35) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_pagamento TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	iban_accredito VARCHAR(255),
	commissioni_psp DOUBLE,
	-- Valori possibili:\nES: Esito originario\nBD: Marca da Bollo
	tipo_allegato VARCHAR(2),
	allegato MEDIUMBLOB,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_acquisizione_revoca TIMESTAMP(3) DEFAULT 0,
	causale_revoca VARCHAR(140),
	dati_revoca VARCHAR(140),
	importo_revocato DOUBLE,
	esito_revoca VARCHAR(140),
	dati_esito_revoca VARCHAR(140),
	stato VARCHAR(35),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_rpt BIGINT,
	id_singolo_versamento BIGINT,
	id_rr BIGINT,
	id_incasso BIGINT,
	-- unique constraints
	CONSTRAINT unique_pagamenti_1 UNIQUE (cod_dominio,iuv,iur,indice_dati),
	-- fk/pk keys constraints
	CONSTRAINT fk_pag_id_rpt FOREIGN KEY (id_rpt) REFERENCES rpt(id),
	CONSTRAINT fk_pag_id_singolo_versamento FOREIGN KEY (id_singolo_versamento) REFERENCES singoli_versamenti(id),
	CONSTRAINT fk_pag_id_rr FOREIGN KEY (id_rr) REFERENCES rr(id),
	CONSTRAINT fk_pag_id_incasso FOREIGN KEY (id_incasso) REFERENCES incassi(id),
	CONSTRAINT pk_pagamenti PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE UNIQUE INDEX index_pagamenti_1 ON pagamenti (cod_dominio,iuv,iur,indice_dati);



CREATE TABLE rendicontazioni
(
	iuv VARCHAR(35) NOT NULL,
	iur VARCHAR(35) NOT NULL,
	indice_dati INT,
	importo_pagato DOUBLE,
	esito INT,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) DEFAULT 0,
	stato VARCHAR(35) NOT NULL,
	anomalie LONGTEXT,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_fr BIGINT NOT NULL,
	id_pagamento BIGINT,
	-- fk/pk keys constraints
	CONSTRAINT fk_rnd_id_fr FOREIGN KEY (id_fr) REFERENCES fr(id),
	CONSTRAINT fk_rnd_id_pagamento FOREIGN KEY (id_pagamento) REFERENCES pagamenti(id),
	CONSTRAINT pk_rendicontazioni PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;




CREATE TABLE eventi
(
	cod_dominio VARCHAR(35),
	iuv VARCHAR(35),
	ccp VARCHAR(35),
	cod_psp VARCHAR(35),
	tipo_versamento VARCHAR(10),
	componente VARCHAR(4),
	categoria_evento VARCHAR(1),
	tipo_evento VARCHAR(35),
	sottotipo_evento VARCHAR(35),
	erogatore VARCHAR(35),
	fruitore VARCHAR(35),
	cod_stazione VARCHAR(35),
	cod_canale VARCHAR(35),
	parametri_1 VARCHAR(512),
	parametri_2 VARCHAR(512),
	esito VARCHAR(35),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
    -- Per versioni successive alla 5.7, rimuovere dalla sql_mode NO_ZERO_DATE 
	data_1 TIMESTAMP(3),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
    -- Per versioni successive alla 5.7, rimuovere dalla sql_mode NO_ZERO_DATE 
	data_2 TIMESTAMP(3),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_eventi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;




CREATE TABLE batch
(
	cod_batch VARCHAR(255) NOT NULL,
	nodo INT,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	inizio TIMESTAMP(3),
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	aggiornamento TIMESTAMP(3),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- unique constraints
	CONSTRAINT unique_batch_1 UNIQUE (cod_batch),
	-- fk/pk keys constraints
	CONSTRAINT pk_batch PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_batch_1 ON batch (cod_batch);


CREATE TABLE tracciati
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_caricamento TIMESTAMP(3) NOT NULL DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ultimo_aggiornamento TIMESTAMP(3) NOT NULL DEFAULT 0,
	stato VARCHAR(255) NOT NULL,
	linea_elaborazione BIGINT NOT NULL,
	descrizione_stato VARCHAR(1024),
	num_linee_totali BIGINT NOT NULL,
	num_operazioni_ok BIGINT NOT NULL,
	num_operazioni_ko BIGINT NOT NULL,
	nome_file VARCHAR(255) NOT NULL,
	raw_data_richiesta MEDIUMBLOB NOT NULL,
	raw_data_risposta MEDIUMBLOB,
	tipo_tracciato VARCHAR(255) NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_operatore BIGINT,
	id_applicazione BIGINT,
	-- fk/pk keys constraints
	CONSTRAINT fk_trc_id_operatore FOREIGN KEY (id_operatore) REFERENCES operatori(id),
	CONSTRAINT fk_trc_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id),
	CONSTRAINT pk_tracciati PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;




CREATE TABLE operazioni
(
	tipo_operazione VARCHAR(255) NOT NULL,
	linea_elaborazione BIGINT NOT NULL,
	stato VARCHAR(255) NOT NULL,
	dati_richiesta MEDIUMBLOB NOT NULL,
	dati_risposta MEDIUMBLOB,
	dettaglio_esito VARCHAR(255),
	cod_versamento_ente VARCHAR(255),
	cod_dominio VARCHAR(35),
	iuv VARCHAR(35),
	trn VARCHAR(35),
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_tracciato BIGINT NOT NULL,
	id_applicazione BIGINT,
	-- fk/pk keys constraints
	CONSTRAINT fk_ope_id_tracciato FOREIGN KEY (id_tracciato) REFERENCES tracciati(id),
	CONSTRAINT fk_ope_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id),
	CONSTRAINT pk_operazioni PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;




CREATE TABLE gp_audit
(
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data TIMESTAMP(3) NOT NULL DEFAULT 0,
	id_oggetto BIGINT NOT NULL,
	tipo_oggetto VARCHAR(255) NOT NULL,
	oggetto LONGTEXT NOT NULL,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	id_operatore BIGINT NOT NULL,
	-- fk/pk keys constraints
	CONSTRAINT fk_aud_id_operatore FOREIGN KEY (id_operatore) REFERENCES operatori(id),
	CONSTRAINT pk_gp_audit PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;




CREATE TABLE avvisi
(
	cod_dominio VARCHAR(35) NOT NULL,
	iuv VARCHAR(35) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_creazione TIMESTAMP(3) NOT NULL DEFAULT 0,
	stato VARCHAR(255) NOT NULL,
	pdf MEDIUMBLOB,
	-- fk/pk columns
	id BIGINT AUTO_INCREMENT,
	-- fk/pk keys constraints
	CONSTRAINT pk_avvisi PRIMARY KEY (id)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

-- index
CREATE INDEX index_avvisi_1 ON avvisi (cod_dominio,iuv);
CREATE INDEX index_avvisi_2 ON avvisi (stato);



CREATE TABLE ID_MESSAGGIO_RELATIVO
(
	COUNTER BIGINT NOT NULL,
	PROTOCOLLO VARCHAR(255) NOT NULL,
	INFO_ASSOCIATA VARCHAR(255) NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	ora_registrazione TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	-- fk/pk columns
	-- fk/pk keys constraints
	CONSTRAINT pk_ID_MESSAGGIO_RELATIVO PRIMARY KEY (PROTOCOLLO,INFO_ASSOCIATA)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;


CREATE TABLE sonde
(
	nome VARCHAR(35) NOT NULL,
	classe VARCHAR(255) NOT NULL,
	soglia_warn BIGINT NOT NULL,
	soglia_error BIGINT NOT NULL,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ok TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_warn TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_error TIMESTAMP(3) DEFAULT 0,
	-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
	data_ultimo_check TIMESTAMP(3) DEFAULT 0,
	dati_check LONGTEXT,
	stato_ultimo_check INT,
	-- fk/pk columns
	-- fk/pk keys constraints
	CONSTRAINT pk_sonde PRIMARY KEY (nome)
)ENGINE INNODB CHARACTER SET latin1 COLLATE latin1_general_cs;

