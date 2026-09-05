DROP INDEX idx_allarmi_1 ON allarmi;
DROP INDEX idx_allarmi_param_1 ON allarmi_parametri;

EXEC sp_rename 'allarmi.idx_allarmi_2', 'idx_allarmi_1', 'INDEX';
