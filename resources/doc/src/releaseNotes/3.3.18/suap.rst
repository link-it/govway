Miglioramenti al supporto per l'integrazione con i servizi SUAP
-----------------------------------------------------------------

Il plugin che consente di gestire gli errori previsti dalla `Specifica Tecnica DPR-160 <https://github.com/AgID/specifiche-tecniche-DPR-160-2010/blob/approved02/specifiche_navigabili/08_e-service%20del%20SSU/08_06/08_06.md/>`_ è stato rivisto nei seguenti aspetti:

- la gestione di una richiesta che presentava un header HTTP Authorization valorizzato con il solo JWT, senza il prefisso Bearer, veniva gestita da GovWay restituendo erroneamente la risposta "PDND token not found", mentre la testsuite SUAP si attendeva il messaggio "Invalid PDND token". L'errore è stato allineato alla risposta prevista, in conformità alle specifiche SUAP.

- la richiesta generata dalla suite di test BB verso la risorsa /instance/{cui_uuid}/document/{resource_id} non presenta volutamente l'header 'If-Match' definito come obbligatorio nell'interfaccia OpenAPI. In tali casi, GovWay restituiva erroneamente la risposta "ERROR_400_001 / incorrect request input", mentre la testsuite SUAP si attendeva il messaggio "ERROR_428_001 / hash not found".	L'errore è stato allineato alla risposta prevista dal caso di errore ERROR_428_001.

Sono infine state realizzate GovLet che facilitano la configurazione delle erogazioni previste dalla certificazione SUAP.

