ALTER TABLE accordi ADD COLUMN canale VARCHAR(255);

CREATE INDEX index_accordi_2 ON accordi (canale);
