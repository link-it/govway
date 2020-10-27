ALTER TABLE accordi ADD canale VARCHAR2(255);

CREATE INDEX index_accordi_2 ON accordi (canale);
