ALTER TABLE pa_sa_properties DROP INDEX uniq_pa_sa_props_1;
ALTER TABLE pa_sa_properties ADD CONSTRAINT uniq_pa_sa_props_1 UNIQUE (id_porta,nome);

ALTER TABLE pa_behaviour_props DROP INDEX uniq_pa_behaviour_props_1;
ALTER TABLE pa_behaviour_props ADD CONSTRAINT uniq_pa_behaviour_props_1 UNIQUE (id_porta,nome);

ALTER TABLE pa_auth_properties DROP INDEX uniq_pa_auth_props_1;
ALTER TABLE pa_auth_properties ADD CONSTRAINT uniq_pa_auth_props_1 UNIQUE (id_porta,nome);

ALTER TABLE pa_authz_properties DROP INDEX uniq_pa_authz_props_1;
ALTER TABLE pa_authz_properties ADD CONSTRAINT uniq_pa_authz_props_1 UNIQUE (id_porta,nome);

ALTER TABLE pa_authzc_properties DROP INDEX uniq_pa_authzc_props_1;
ALTER TABLE pa_authzc_properties ADD CONSTRAINT uniq_pa_authzc_props_1 UNIQUE (id_porta,nome);

ALTER TABLE pa_properties DROP INDEX uniq_pa_properties_1;
ALTER TABLE pa_properties ADD CONSTRAINT uniq_pa_properties_1 UNIQUE (id_porta,nome);

ALTER TABLE pd_auth_properties DROP INDEX uniq_pd_auth_props_1;
ALTER TABLE pd_auth_properties ADD CONSTRAINT uniq_pd_auth_props_1 UNIQUE (id_porta,nome);

ALTER TABLE pd_authz_properties DROP INDEX uniq_pd_authz_props_1;
ALTER TABLE pd_authz_properties ADD CONSTRAINT uniq_pd_authz_props_1 UNIQUE (id_porta,nome);

ALTER TABLE pd_authzc_properties DROP INDEX uniq_pd_authzc_props_1;
ALTER TABLE pd_authzc_properties ADD CONSTRAINT uniq_pd_authzc_props_1 UNIQUE (id_porta,nome);

ALTER TABLE pd_properties DROP INDEX uniq_pd_properties_1;
ALTER TABLE pd_properties ADD CONSTRAINT uniq_pd_properties_1 UNIQUE (id_porta,nome);

