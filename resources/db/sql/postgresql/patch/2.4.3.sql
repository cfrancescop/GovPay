--GP-550
ALTER TABLE pagamenti ADD COLUMN indice_dati INT NOT NULL DEFAULT 1;
ALTER TABLE pagamenti DROP CONSTRAINT IF EXISTS unique_pagamenti_1;
ALTER TABLE pagamenti ADD CONSTRAINT unique_pagamenti_1 UNIQUE (cod_dominio,iuv,iur,indice_dati);

ALTER TABLE canali DROP CONSTRAINT IF EXISTS fk_canali_1;
ALTER TABLE stazioni DROP CONSTRAINT IF EXISTS fk_stazioni_1;
ALTER TABLE domini DROP CONSTRAINT IF EXISTS fk_domini_1;
ALTER TABLE domini DROP CONSTRAINT IF EXISTS fk_domini_2;
ALTER TABLE uo DROP CONSTRAINT IF EXISTS fk_uo_1;
ALTER TABLE iban_accredito DROP CONSTRAINT IF EXISTS fk_iban_accredito_1;
ALTER TABLE tributi DROP CONSTRAINT IF EXISTS fk_tributi_1;
ALTER TABLE tributi DROP CONSTRAINT IF EXISTS fk_tributi_2;
ALTER TABLE tributi DROP CONSTRAINT IF EXISTS fk_tributi_3;
ALTER TABLE acl DROP CONSTRAINT IF EXISTS fk_acl_1;
ALTER TABLE acl DROP CONSTRAINT IF EXISTS fk_acl_2;
ALTER TABLE acl DROP CONSTRAINT IF EXISTS fk_acl_3;
ALTER TABLE acl DROP CONSTRAINT IF EXISTS fk_acl_4;
ALTER TABLE acl DROP CONSTRAINT IF EXISTS fk_acl_5;
ALTER TABLE acl DROP CONSTRAINT IF EXISTS fk_acl_6;
ALTER TABLE rr DROP CONSTRAINT IF EXISTS fk_rr_1;
ALTER TABLE incassi DROP CONSTRAINT IF EXISTS fk_incassi_1;
ALTER TABLE pagamenti DROP CONSTRAINT IF EXISTS fk_pagamenti_1;
ALTER TABLE pagamenti DROP CONSTRAINT IF EXISTS fk_pagamenti_2;
ALTER TABLE pagamenti DROP CONSTRAINT IF EXISTS fk_pagamenti_3;
ALTER TABLE pagamenti DROP CONSTRAINT IF EXISTS fk_pagamenti_4;
ALTER TABLE rendicontazioni DROP CONSTRAINT IF EXISTS fk_rendicontazioni_1;
ALTER TABLE rendicontazioni DROP CONSTRAINT IF EXISTS fk_rendicontazioni_2;
ALTER TABLE tracciati DROP CONSTRAINT IF EXISTS fk_tracciati_1;
ALTER TABLE tracciati DROP CONSTRAINT IF EXISTS fk_tracciati_2;
ALTER TABLE operazioni DROP CONSTRAINT IF EXISTS fk_operazioni_1;
ALTER TABLE operazioni DROP CONSTRAINT IF EXISTS fk_operazioni_2;
ALTER TABLE gp_audit DROP CONSTRAINT IF EXISTS fk_gp_audit_1;

ALTER TABLE canali ADD CONSTRAINT fk_can_id_psp FOREIGN KEY (id_psp) REFERENCES psp(id);
ALTER TABLE stazioni ADD CONSTRAINT fk_stz_id_intermediario FOREIGN KEY (id_intermediario) REFERENCES intermediari(id);
ALTER TABLE domini ADD CONSTRAINT fk_dom_id_stazione FOREIGN KEY (id_stazione) REFERENCES stazioni(id);
ALTER TABLE domini ADD CONSTRAINT fk_dom_id_applicazione_default FOREIGN KEY (id_applicazione_default) REFERENCES applicazioni(id);
ALTER TABLE uo ADD CONSTRAINT fk_uo_id_dominio FOREIGN KEY (id_dominio) REFERENCES domini(id);
ALTER TABLE iban_accredito ADD CONSTRAINT fk_iba_id_dominio FOREIGN KEY (id_dominio) REFERENCES domini(id);
ALTER TABLE tributi ADD CONSTRAINT fk_trb_id_dominio FOREIGN KEY (id_dominio) REFERENCES domini(id);
ALTER TABLE tributi ADD CONSTRAINT fk_trb_id_iban_accredito FOREIGN KEY (id_iban_accredito) REFERENCES iban_accredito(id);
ALTER TABLE tributi ADD CONSTRAINT fk_trb_id_tipo_tributo FOREIGN KEY (id_tipo_tributo) REFERENCES tipi_tributo(id);
ALTER TABLE acl ADD CONSTRAINT fk_acl_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id);
ALTER TABLE acl ADD CONSTRAINT fk_acl_id_portale FOREIGN KEY (id_portale) REFERENCES portali(id);
ALTER TABLE acl ADD CONSTRAINT fk_acl_id_operatore FOREIGN KEY (id_operatore) REFERENCES operatori(id);
ALTER TABLE acl ADD CONSTRAINT fk_acl_id_ruolo FOREIGN KEY (id_ruolo) REFERENCES ruoli(id);
ALTER TABLE acl ADD CONSTRAINT fk_acl_id_dominio FOREIGN KEY (id_dominio) REFERENCES domini(id);
ALTER TABLE acl ADD CONSTRAINT fk_acl_id_tipo_tributo FOREIGN KEY (id_tipo_tributo) REFERENCES tipi_tributo(id);
ALTER TABLE rr ADD CONSTRAINT fk_rr_id_rpt FOREIGN KEY (id_rpt) REFERENCES rpt(id);
ALTER TABLE incassi ADD CONSTRAINT fk_inc_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id);
ALTER TABLE pagamenti ADD CONSTRAINT fk_pag_id_rpt FOREIGN KEY (id_rpt) REFERENCES rpt(id);
ALTER TABLE pagamenti ADD CONSTRAINT fk_pag_id_singolo_versamento FOREIGN KEY (id_singolo_versamento) REFERENCES singoli_versamenti(id);
ALTER TABLE pagamenti ADD CONSTRAINT fk_pag_id_rr FOREIGN KEY (id_rr) REFERENCES rr(id);
ALTER TABLE pagamenti ADD CONSTRAINT fk_pag_id_incasso FOREIGN KEY (id_incasso) REFERENCES incassi(id);
ALTER TABLE rendicontazioni ADD CONSTRAINT fk_rnd_id_fr FOREIGN KEY (id_fr) REFERENCES fr(id);
ALTER TABLE rendicontazioni ADD CONSTRAINT fk_rnd_id_pagamento FOREIGN KEY (id_pagamento) REFERENCES pagamenti(id);
ALTER TABLE tracciati ADD CONSTRAINT fk_trc_id_operatore FOREIGN KEY (id_operatore) REFERENCES operatori(id);
ALTER TABLE tracciati ADD CONSTRAINT fk_trc_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id);
ALTER TABLE operazioni ADD CONSTRAINT fk_ope_id_tracciato FOREIGN KEY (id_tracciato) REFERENCES tracciati(id);
ALTER TABLE operazioni ADD CONSTRAINT fk_ope_id_applicazione FOREIGN KEY (id_applicazione) REFERENCES applicazioni(id);
ALTER TABLE gp_audit ADD CONSTRAINT fk_aud_id_operatore FOREIGN KEY (id_operatore) REFERENCES operatori(id);

-- index
CREATE INDEX index_rpt_stato ON rpt (stato);
CREATE INDEX index_rpt_id_versamento ON rpt (id_versamento);

ALTER TABLE rendicontazioni ADD COLUMN indice_dati INT;
