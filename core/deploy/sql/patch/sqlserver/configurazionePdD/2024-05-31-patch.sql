ALTER TABLE servizi_applicativi ADD enc_passwordrisp VARCHAR(max);
ALTER TABLE servizi_applicativi ADD enc_passwordinv VARCHAR(max);

ALTER TABLE sa_properties ADD enc_value VARCHAR(max);

ALTER TABLE connettori ADD enc_password VARCHAR(max);
ALTER TABLE connettori ADD enc_proxy_password VARCHAR(max);
ALTER TABLE connettori ADD api_key VARCHAR(max);
ALTER TABLE connettori ADD api_key_header VARCHAR(255);
ALTER TABLE connettori ADD app_id VARCHAR(max);
ALTER TABLE connettori ADD app_id_header VARCHAR(255);

ALTER TABLE connettori_custom ADD enc_value VARCHAR(max);
	
ALTER TABLE generic_property ADD enc_value VARCHAR(max);
	
ALTER TABLE pdd_sys_props ADD enc_value VARCHAR(max);
	
ALTER TABLE pa_security_request ADD enc_value VARCHAR(max);
ALTER TABLE pa_security_response ADD enc_value VARCHAR(max);
ALTER TABLE pd_security_request ADD enc_value VARCHAR(max);
ALTER TABLE pd_security_response ADD enc_value VARCHAR(max);
	
ALTER TABLE pd_auth_properties ADD enc_value VARCHAR(max);
ALTER TABLE pd_authz_properties ADD enc_value VARCHAR(max);
ALTER TABLE pd_authzc_properties ADD enc_value VARCHAR(max);
ALTER TABLE pd_properties ADD enc_value VARCHAR(max);

ALTER TABLE pa_auth_properties ADD enc_value VARCHAR(max);
ALTER TABLE pa_authz_properties ADD enc_value VARCHAR(max);
ALTER TABLE pa_authzc_properties ADD enc_value VARCHAR(max);
ALTER TABLE pa_properties ADD enc_value VARCHAR(max);
