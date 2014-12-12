
INSERT INTO audit_appender (name,class) VALUES ('dbAppender','org.openspcoop2.web.lib.audit.appender.AuditDBAppender');
INSERT INTO audit_appender_prop (name,value,id_audit_appender) VALUES ('datasource','org.openspcoop2.dataSource.audit',(select id from audit_appender where name='dbAppender'));
INSERT INTO audit_appender_prop (name,value,id_audit_appender) VALUES ('tipoDatabase','INDICARE_IL_TIPO_DI_DATABASE',(select id from audit_appender where name='dbAppender'));
