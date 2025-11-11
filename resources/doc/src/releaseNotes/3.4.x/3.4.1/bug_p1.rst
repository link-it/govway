.. _3.4.1.1_bug:

Bug Fix 3.4.1.p1
------------------

Sono stati risolti i seguenti bug:

- Risolta anomalia che impediva l’utilizzo della keyword 'securityToken' nelle trasformazioni di risposta per API configurate esclusivamente con la negoziazione token, senza pattern di sicurezza aggiuntivi.

- Aggiornata la gestione delle govlet SUAP: l’indicazione del soggetto nella sezione audience e nel connettore dell’erogazione esterna è ora impostata dinamicamente in base al soggetto specificato durante il caricamento della govlet (rimosso il valore cablato "ENTE").

Per la console di gestione sono stati risolti i seguenti bug:

- Risolto un problema che impediva la corretta visualizzazione delle schede di dettaglio quando la descrizione dell'elemento conteneva contenuti HTML. Ora il campo descrizione viene correttamente gestito tramite escape HTML in fase di lettura.

- Risolte segnalazioni 'A form label must be associated with a control.' di SonarQube sulla pagina jsp 'edit-page.jsp'.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- (https://github.com/link-it/govway/issues/261) È stata corretta un’anomalia che impediva il corretto funzionamento della console. Il problema era dovuto alla presenza di un doppio carattere “//” all’interno dei riferimenti di alcune risorse,che causava il malfunzionamento dell’applicazione nel caso in cui l’ingress posto davanti alla console eseguisse l’operazione di merge slash.	Tale comportamento provocava errori JavaScript che rendevano la webapp inutilizzabile, impedendo l’accesso alla schermata di login.

Per le API di configurazione sono stati risolti i seguenti bug:

- corretto il comportamento della risorsa '/api-config/v1/soggetti/{nome}' che non restituiva risultati per i soggetti privi di autenticazione configurata;

Sono state risolte le seguenti anomalie presenti nell'installer:

- Aggiornati script SQL per compatibilità con MySQL 8.0+ (rimosso sql_mode 'NO_AUTO_CREATE_USER').

- Corretta la generazione dello script SQL 'GovWayTracciamento.sql' che, nella modalità di installazione avanzata con database dedicato al tracciamento, non includeva la creazione della tabella 'OP2_SEMAPHORE'.
