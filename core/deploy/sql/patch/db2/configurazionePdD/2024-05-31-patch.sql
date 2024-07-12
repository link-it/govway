ALTER TABLE servizi_applicativi ADD enc_passwordrisp CLOB;
ALTER TABLE servizi_applicativi ADD enc_passwordinv CLOB;

ALTER TABLE sa_properties ADD enc_value CLOB;

ALTER TABLE connettori ADD enc_password CLOB;
ALTER TABLE connettori ADD enc_proxy_password CLOB;
ALTER TABLE connettori ADD api_key CLOB;
ALTER TABLE connettori ADD api_key_header VARCHAR(255);
ALTER TABLE connettori ADD app_id CLOB;
ALTER TABLE connettori ADD app_id_header VARCHAR(255);

ALTER TABLE connettori_custom ADD enc_value CLOB;
	
ALTER TABLE generic_property ADD enc_value CLOB;
	
ALTER TABLE pdd_sys_props ADD enc_value CLOB;
	
ALTER TABLE pa_security_request ADD enc_value CLOB;
ALTER TABLE pa_security_response ADD enc_value CLOB;
ALTER TABLE pd_security_request ADD enc_value CLOB;
ALTER TABLE pd_security_response ADD enc_value CLOB;
	
ALTER TABLE pd_auth_properties ADD enc_value CLOB;
ALTER TABLE pd_authz_properties ADD enc_value CLOB;
ALTER TABLE pd_authzc_properties ADD enc_value CLOB;
ALTER TABLE pd_properties ADD enc_value CLOB;

ALTER TABLE pa_auth_properties ADD enc_value CLOB;
ALTER TABLE pa_authz_properties ADD enc_value CLOB;
ALTER TABLE pa_authzc_properties ADD enc_value CLOB;
ALTER TABLE pa_properties ADD enc_value CLOB;
