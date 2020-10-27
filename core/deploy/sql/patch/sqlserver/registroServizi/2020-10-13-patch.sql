ALTER TABLE accordi ADD canale VARCHAR(20);

CREATE INDEX index_accordi_2 ON accordi (canale);
