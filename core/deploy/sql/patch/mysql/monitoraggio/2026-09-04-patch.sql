DROP INDEX idx_allarmi_1 ON allarmi;
DROP INDEX idx_allarmi_param_1 ON allarmi_parametri;

ALTER TABLE allarmi RENAME INDEX idx_allarmi_2 TO idx_allarmi_1;
