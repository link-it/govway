update generic_properties set tipo='validationToken' WHERE tipo='openspcoop';

ALTER TABLE connettori ADD COLUMN token_policy VARCHAR(255);
