ALTER TABLE config_protocolli ADD url_pd_rest VARCHAR2(255);
ALTER TABLE config_protocolli ADD url_pa_rest VARCHAR2(255);
ALTER TABLE config_protocolli ADD url_pd_soap VARCHAR2(255);
ALTER TABLE config_protocolli ADD url_pa_soap VARCHAR2(255);

UPDATE config_protocolli set url_pd_rest=url_pd;
UPDATE config_protocolli set url_pa_rest=url_pa;
UPDATE config_protocolli set url_pd_soap=url_pd;
UPDATE config_protocolli set url_pa_soap=url_pa;
