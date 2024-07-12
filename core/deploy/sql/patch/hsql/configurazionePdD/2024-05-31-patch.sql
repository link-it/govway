ALTER TABLE servizi_applicativi ADD COLUMN enc_passwordrisp VARCHAR(65535);
ALTER TABLE servizi_applicativi ADD COLUMN enc_passwordinv VARCHAR(65535);

ALTER TABLE sa_properties ADD COLUMN enc_value VARCHAR(65535);

ALTER TABLE connettori ADD COLUMN enc_password VARCHAR(65535);
ALTER TABLE connettori ADD COLUMN enc_proxy_password VARCHAR(65535);
ALTER TABLE connettori ADD COLUMN api_key VARCHAR(65535);
ALTER TABLE connettori ADD COLUMN api_key_header VARCHAR(255);
ALTER TABLE connettori ADD COLUMN app_id VARCHAR(65535);
ALTER TABLE connettori ADD COLUMN app_id_header VARCHAR(255);

ALTER TABLE connettori_custom ADD COLUMN enc_value VARCHAR(65535);
	
ALTER TABLE generic_property ADD COLUMN enc_value VARCHAR(65535);
	
ALTER TABLE pdd_sys_props ADD COLUMN enc_value VARCHAR(65535);
	
ALTER TABLE pa_security_request ADD COLUMN enc_value VARCHAR(65535);
ALTER TABLE pa_security_response ADD COLUMN enc_value VARCHAR(65535);
ALTER TABLE pd_security_request ADD COLUMN enc_value VARCHAR(65535);
ALTER TABLE pd_security_response ADD COLUMN enc_value VARCHAR(65535);
	
ALTER TABLE pd_auth_properties ADD COLUMN enc_value VARCHAR(65535);
ALTER TABLE pd_authz_properties ADD COLUMN enc_value VARCHAR(65535);
ALTER TABLE pd_authzc_properties ADD COLUMN enc_value VARCHAR(65535);
ALTER TABLE pd_properties ADD COLUMN enc_value VARCHAR(65535);

ALTER TABLE pa_auth_properties ADD COLUMN enc_value VARCHAR(65535);
ALTER TABLE pa_authz_properties ADD COLUMN enc_value VARCHAR(65535);
ALTER TABLE pa_authzc_properties ADD COLUMN enc_value VARCHAR(65535);
ALTER TABLE pa_properties ADD COLUMN enc_value VARCHAR(65535);
