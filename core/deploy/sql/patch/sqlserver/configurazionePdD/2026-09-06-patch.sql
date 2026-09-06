ALTER TABLE porte_delegate DROP CONSTRAINT unique_porte_delegate_1;
ALTER TABLE porte_delegate ALTER COLUMN nome_porta VARCHAR(1024) NOT NULL;
ALTER TABLE porte_delegate ADD CONSTRAINT unique_porte_delegate_1 UNIQUE (nome_porta);

ALTER TABLE porte_applicative DROP CONSTRAINT unique_porte_applicative_1;
ALTER TABLE porte_applicative ALTER COLUMN nome_porta VARCHAR(1024) NOT NULL;
ALTER TABLE porte_applicative ADD CONSTRAINT unique_porte_applicative_1 UNIQUE (nome_porta);

ALTER TABLE servizi_applicativi DROP CONSTRAINT unique_servizi_applicativi_1;
ALTER TABLE servizi_applicativi ALTER COLUMN nome VARCHAR(1024) NOT NULL;
ALTER TABLE servizi_applicativi ADD CONSTRAINT unique_servizi_applicativi_1 UNIQUE (nome,id_soggetto);

DROP INDEX idx_cong_att_policy_1 ON ct_active_policy;
ALTER TABLE ct_active_policy ALTER COLUMN filtro_porta VARCHAR(1024);
CREATE INDEX idx_cong_att_policy_1 ON ct_active_policy (filtro_ruolo,filtro_porta);

-- ALTER TABLE connettori DROP CONSTRAINT unique_connettori_1;
-- ALTER TABLE connettori ALTER COLUMN nome_connettore VARCHAR(1024) NOT NULL;
-- ALTER TABLE connettori ADD CONSTRAINT unique_connettori_1 UNIQUE (nome_connettore);
