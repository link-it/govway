
INSERT INTO audit_appender (name,class) VALUES ('log4jAppender','org.openspcoop2.web.lib.audit.appender.AuditLog4JAppender');
INSERT INTO audit_appender_prop (name,value,id_audit_appender) VALUES ('fileConfigurazione','audit.log4j2.properties',(select id from audit_appender where name='log4jAppender'));
INSERT INTO audit_appender_prop (name,value,id_audit_appender) VALUES ('nomeFileLoaderInstance','audit_local.log4j2.properties',(select id from audit_appender where name='log4jAppender'));
INSERT INTO audit_appender_prop (name,value,id_audit_appender) VALUES ('nomeProprietaLoaderInstance','OPENSPCOOP2_AUDIT_LOG_PROPERTIES',(select id from audit_appender where name='log4jAppender'));
INSERT INTO audit_appender_prop (name,value,id_audit_appender) VALUES ('category','audit',(select id from audit_appender where name='log4jAppender'));
INSERT INTO audit_appender_prop (name,value,id_audit_appender) VALUES ('xml','true',(select id from audit_appender where name='log4jAppender'));
