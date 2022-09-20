.. _mon_esito_transazione:

Esito di una Transazione
~~~~~~~~~~~~~~~~~~~~~~~~

La tabella che segue riporta tutti gli esiti associabili ad una transazione gestita daGovway, così come indicati nel file di properties del prodotto `esiti.properties <https://github.com/link-it/govway/blob/master/core/src/org/openspcoop2/protocol/utils/esiti.properties>`_:

 ================================================= ============================================= =========================================================================================================================================== ========= 
  ID                                                TITOLO                                        DESCRIZIONE                                                                                                                                CODICE   
 ================================================= ============================================= =========================================================================================================================================== ========= 
  OK                                                Ok                                            Transazione gestita con successo                                                                                                              0        
  ERRORE_PROTOCOLLO                                 Errore di Protocollo                          Transazione fallita a causa di un errore avvenuto durante la gestione del profilo di interoperabilità                                         1        
  ERRORE_APPLICATIVO                                Fault Applicativo                             La risposta applicativa contiene un fault applicativo                                                                                         2        
  ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE         Connessione Client Interrotta                 La connessione del Client che ha scaturito la richiesta non è più disponibile                                                                 3        
  ERRORE_PROCESSAMENTO_PDD_4XX                      Richiesta Malformata                          Errore causato da informazioni errate fornite dal client                                                                                      4        
  ERRORE_PROCESSAMENTO_PDD_5XX                      Errore Generico                               Errore causato da risorse non disponibili o problemi interni del gateway                                                                      5        
  AUTENTICAZIONE_FALLITA                            Autenticazione Fallita [I.M.]                 Autenticazione fallita (servizio di MessageBox dell'Integration Manager)                                                                      6        
  AUTORIZZAZIONE_FALLITA                            Autorizzazione Fallita [I.M.]                 Autorizzazione fallita (servizio di MessageBox dell'Integration Manager)                                                                      7        
  MESSAGGI_NON_PRESENTI                             Messaggi non presenti [I.M.]                  Messaggi non presenti (servizio di MessageBox dell'Integration Manager)                                                                       8        
  MESSAGGIO_NON_TROVATO                             Messaggio non trovato [I.M.]                  Messaggio non trovato (servizio di MessageBox dell'Integration Manager)                                                                       9        
  ERRORE_INVOCAZIONE                                Errore di Connessione                         Messaggio non inoltrabile al destinatario a causa di problemi di connessione                                                                  10       
  ERRORE_SERVER                                     Fault senza Informazioni Protocollo           SOAP Fault ritornato dall'Erogatore senza essere contenuto in un messaggio previsto dal Profilo di Interoperabilità                           11       
  OK_PRESENZA_ANOMALIE                              Ok (Presenza Anomalie)                        Transazione gestito con successo (sono stati emessi dei diagnostici che hanno rilevato delle anomalie)                                        12       
  CONTENUTO_RICHIESTA_NON_RICONOSCIUTO              Contenuto Richiesta Malformato                Il contenuto della richiesta non è processabile dal Gateway                                                                                   13       
  CONTENUTO_RISPOSTA_NON_RICONOSCIUTO               Contenuto Risposta Malformato                 Il contenuto della risposta non è processabile dal Gateway                                                                                    14       
  ERRORE_TOKEN                                      Gestione Token Fallita                        Sono emersi degli errori durante la gestione/validazione del token                                                                            15       
  ERRORE_AUTENTICAZIONE                             Autenticazione Fallita                        L'autenticazione del richiedente non è stata effettuata con successo                                                                          16       
  ERRORE_AUTORIZZAZIONE                             Autorizzazione Negata                         La richiesta non è stata autorizzata                                                                                                          17       
  CONTROLLO_TRAFFICO_POLICY_VIOLATA                 Violazione Rate Limiting                      Rilevata una violazione di una Policy di Rate Limiting                                                                                        18       
  CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY    Violazione Rate Limiting WarningOnly          Rilevata una violazione di una Policy di Rate Limiting (configurata in WarningOnly)                                                           19       
  CONTROLLO_TRAFFICO_MAX_THREADS                    Superamento Limite Richieste                  Rilevato il superamento del numero massimo di richieste simultanee consentite sul gateway                                                     20       
  CONTROLLO_TRAFFICO_MAX_THREADS_WARNING_ONLY       Superamento Limite Richieste WarningOnly      Superamento numero massimo di richieste simultanee consentite sul gateway (controllo configurato in WarningOnly)                              21       
  ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA              Sicurezza Messaggio Richiesta Fallita         La gestione della sicurezza messaggio sulla richiesta non è stata completata con successo                                                     22       
  ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA               Sicurezza Messaggio Risposta Fallita          La gestione della sicurezza messaggio sulla risposta non è stata completata con successo                                                      23       
  ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA               Gestione Allegati Richiesta Fallita           La gestione degli allegati sulla richiesta non è stata completata con successo                                                                24       
  ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA                Gestione Allegati Risposta Fallita            La gestione degli allegati sulla risposta non è stata completata con successo                                                                 25       
  ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA         Correlazione Applicativa Richiesta Fallita    La gestione della correlazione applicativa sulla richiesta non è stata completata con successo                                                26       
  ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA          Correlazione Applicativa Risposta Fallita     La gestione della correlazione applicativa sulla risposta non è stata completata con successo                                                 27       
  HTTP_3xx                                          Risposta HTTP 3xx                             La risposta applicativa contiene un codice di trasporto 3xx                                                                                   28       
  HTTP_4xx                                          Risposta HTTP 4xx                             La risposta applicativa contiene un codice di trasporto 4xx                                                                                   29       
  HTTP_5xx                                          Risposta HTTP 5xx                             La risposta applicativa contiene un codice di trasporto 5xx                                                                                   30       
  ERRORE_VALIDAZIONE_RICHIESTA                      Validazione Richiesta Fallita                 La validazione della richiesta non è stata completata con successo                                                                            31       
  ERRORE_VALIDAZIONE_RISPOSTA                       Validazione Risposta Fallita                  La validazione della risposta non è stata completata con successo                                                                             32       
  ERRORE_SOSPENSIONE                                API Sospesa                                   L'API invocata risulta sospesa                                                                                                                33       
  CORS_PREFLIGHT_REQUEST_VIA_GATEWAY                CORS Preflight Gestione Gateway               Richiesta OPTIONS CORS Preflight Request gestita dal Gateway                                                                                  34       
  CORS_PREFLIGHT_REQUEST_TRASPARENTE                CORS Preflight Gestione Applicativa           Richiesta OPTIONS CORS Preflight Request gestita dall'applicativo                                                                             35       
  ERRORE_TRASFORMAZIONE_RICHIESTA                   Trasformazione Richiesta Fallita              La trasformazione della richiesta non è stata completata con successo                                                                         36       
  ERRORE_TRASFORMAZIONE_RISPOSTA                    Trasformazione Risposta Fallita               La trasformazione della risposta non è stata completata con successo                                                                          37       
  CONSEGNA_MULTIPLA                                 Consegna Asincrona in Coda                    Tutte le consegne verso i connettori associati all'API sono ancora in attesa di essere consegnate                                             38       
  CONSEGNA_MULTIPLA_COMPLETATA                      Consegna Asincrona Completata                 La richiesta pervenuta è stata inoltrata correttamente a tutti i connettori associati all'API                                                 39       
  CONSEGNA_MULTIPLA_FALLITA                         Consegna Asincrona Fallita                    La richiesta pervenuta è stata inoltrata a tutti i connettori associati all'API; in alcuni casi la consegna non è andata a buon fine          40       
  TOKEN_NON_PRESENTE                                Token non Presente                            La richiesta non presenta un token                                                                                                            41       
  ERRORE_AUTENTICAZIONE_TOKEN                       Autenticazione Token Fallita                  Nel token ricevuto non sono presenti dei claim richiesti per l'accesso all'API invocata                                                       42       
  API_NON_INDIVIDUATA                               API non Individuata                           La richiesta non permette di individuare una API registrata sul Gateway                                                                       43       
  OPERAZIONE_NON_INDIVIDUATA                        Operazione non Individuata                    La richiesta non indirizza un'operazione esistente sull'API invocata                                                                          44       
  RICHIESTA_DUPLICATA                               Richiesta già elaborata                       La richiesta risulta essere già stata elaborata                                                                                               45       
  RISPOSTA_DUPLICATA                                Risposta già elaborata                        La risposta risulta essere già stata elaborata                                                                                                46       
  MESSAGE_BOX                                       Disponibile in MessageBox                     Messaggio gestibile tramite servizio IntegrationManager                                                                                       47       
  CONSEGNA_MULTIPLA_IN_CORSO                        Consegna Asincrona in Corso                   Alcune consegne verso i connettori associati all'API risultano ancora non completate                                                          48       
 ================================================= ============================================= =========================================================================================================================================== ========= 

Ciascun esito riportato nella tabella precedente è riconducibile ad una tra le seguenti casistiche:

 ========================== ======================================================================= 
  Esito Complessivo          Codici Corrispondenti                 
 ========================== ======================================================================= 
  Completata con Successo    0,12,38,48,39,47,2,28,19,21,8,34,35   
  Richiesta Scartata         16,41,42,15,43,44,13,4                
  Errore di Consegna         2,10,11,29,30,40
  Fault Applicativo          2
  Fallite                    Codici non compresi in 'Completata con Successo' e 'Fault Applicativo'
 ========================== ======================================================================= 

