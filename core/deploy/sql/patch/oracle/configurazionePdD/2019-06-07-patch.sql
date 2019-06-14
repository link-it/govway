update generic_properties set tipo='validationToken' WHERE tipo='openspcoop';

ALTER TABLE connettori ADD token_policy VARCHAR2(255);
