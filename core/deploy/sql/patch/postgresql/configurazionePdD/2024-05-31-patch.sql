ALTER TABLE servizi_applicativi ADD COLUMN enc_passwordrisp TEXT;
ALTER TABLE servizi_applicativi ADD COLUMN enc_passwordinv TEXT;

ALTER TABLE sa_properties ADD COLUMN enc_value TEXT;

ALTER TABLE connettori ADD COLUMN enc_password TEXT;
ALTER TABLE connettori ADD COLUMN enc_proxy_password TEXT;
ALTER TABLE connettori ADD COLUMN api_key TEXT;
ALTER TABLE connettori ADD COLUMN api_key_header VARCHAR(255);
ALTER TABLE connettori ADD COLUMN app_id TEXT;
ALTER TABLE connettori ADD COLUMN app_id_header VARCHAR(255);

ALTER TABLE connettori_custom ADD COLUMN enc_value TEXT;
	
ALTER TABLE generic_property ADD COLUMN enc_value TEXT;
	
ALTER TABLE pdd_sys_props ADD COLUMN enc_value TEXT;
	
ALTER TABLE pa_security_request ADD COLUMN enc_value TEXT;
ALTER TABLE pa_security_response ADD COLUMN enc_value TEXT;
ALTER TABLE pd_security_request ADD COLUMN enc_value TEXT;
ALTER TABLE pd_security_response ADD COLUMN enc_value TEXT;
	
ALTER TABLE pd_auth_properties ADD COLUMN enc_value TEXT;
ALTER TABLE pd_authz_properties ADD COLUMN enc_value TEXT;
ALTER TABLE pd_authzc_properties ADD COLUMN enc_value TEXT;
ALTER TABLE pd_properties ADD COLUMN enc_value TEXT;

ALTER TABLE pa_auth_properties ADD COLUMN enc_value TEXT;
ALTER TABLE pa_authz_properties ADD COLUMN enc_value TEXT;
ALTER TABLE pa_authzc_properties ADD COLUMN enc_value TEXT;
ALTER TABLE pa_properties ADD COLUMN enc_value TEXT;
