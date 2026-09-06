DROP INDEX idx_allarmi_1 ON allarmi;
ALTER TABLE allarmi ALTER COLUMN filtro_porta VARCHAR(1024);
CREATE INDEX idx_allarmi_1 ON allarmi (filtro_ruolo,filtro_porta);
