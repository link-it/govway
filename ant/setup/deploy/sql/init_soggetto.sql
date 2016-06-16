INSERT INTO connettori (endpointtype,nome_connettore,debug,custom) VALUES 
			('disabilitato','CNT_@TIPO_SOGGETTO@_@NOME_SOGGETTO@',0,0);
INSERT INTO soggetti (nome_soggetto,tipo_soggetto,descrizione,identificativo_porta,is_router,id_connettore,superuser,server,privato,profilo,codice_ipa) VALUES
	    ('@NOME_SOGGETTO@','@TIPO_SOGGETTO@',null,'@IDPORTA_SOGGETTO@',0,
	     (select id from connettori where nome_connettore='CNT_@TIPO_SOGGETTO@_@NOME_SOGGETTO@'),
	     '@PDDCONSOLE_USERNAME@','PdD@NOME_SOGGETTO@',0,'@VERSIONE_PROTOCOLLO@','@CODICE_IPA@');

