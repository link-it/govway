ALTER TABLE credenziale_mittente ADD COLUMN ref_credenziale BIGINT;
CREATE INDEX CREDENZIALE_INTERNAL_REF ON credenziale_mittente (ref_credenziale);
