ALTER TABLE credenziale_mittente ADD ref_credenziale BIGINT;
CREATE INDEX CREDENZIALE_INTERNAL_REF ON credenziale_mittente (ref_credenziale);
