.. _mon_esito_transazione:

Esito di una Transazione
~~~~~~~~~~~~~~~~~~~~~~~~

La tabella che segue riporta tutti gli esiti associabili ad una transazione gestita da Govway, così come indicati nel file di properties del prodotto `esiti.properties <https://github.com/link-it/govway/blob/master/core/src/org/openspcoop2/protocol/utils/esiti.properties>`_. Le informazioni definite all'interno del file di proprietà sono disponibili anche nelle tabelle 'transazioni_esiti' e 'transazioni_classe_esiti' dello schema di tracciamento.

.. table:: Esiti delle Transazioni
 :class: longtable
 :widths: 15 10 45 30
 :name: esitiTransazioneTab

 ============================================= ========= =========================================================================================================================================== ================================================= 
  TITOLO                                        CODICE    DESCRIZIONE                                                                                                                                 ID 
 ============================================= ========= =========================================================================================================================================== ================================================= 
  Ok                                            0         Transazione gestita con successo                                                                                                            OK
  Errore di Protocollo                          1         Transazione fallita a causa di un errore avvenuto durante la gestione del profilo di interoperabilità                                       ERRORE_PROTOCOLLO
  Fault Applicativo                             2         La risposta applicativa contiene un fault applicativo                                                                                       ERRORE_APPLICATIVO
  Connessione Client Interrotta                 3         La connessione del Client che ha scaturito la richiesta non è più disponibile                                                               ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE
  Richiesta Malformata                          4         Errore causato da informazioni errate fornite dal client                                                                                    ERRORE_PROCESSAMENTO_PDD_4XX
  Errore Generico                               5         Errore causato da risorse non disponibili o problemi interni del gateway                                                                    ERRORE_PROCESSAMENTO_PDD_5XX
  Autenticazione Fallita [I.M.]                 6         Autenticazione fallita (servizio di MessageBox dell'Integration Manager)                                                                    AUTENTICAZIONE_FALLITA
  Autorizzazione Fallita [I.M.]                 7         Autorizzazione fallita (servizio di MessageBox dell'Integration Manager)                                                                    AUTORIZZAZIONE_FALLITA
  Messaggi non presenti [I.M.]                  8         Messaggi non presenti (servizio di MessageBox dell'Integration Manager)                                                                     MESSAGGI_NON_PRESENTI
  Messaggio non trovato [I.M.]                  9         Messaggio non trovato (servizio di MessageBox dell'Integration Manager)                                                                     MESSAGGIO_NON_TROVATO
  Errore di Connessione                         10        Messaggio non inoltrabile al destinatario a causa di problemi di connessione                                                                ERRORE_INVOCAZIONE
  Fault senza Informazioni Protocollo           11        SOAP Fault ritornato dall'Erogatore senza essere contenuto in un messaggio previsto dal Profilo di Interoperabilità                         ERRORE_SERVER
  Ok (Presenza Anomalie)                        12        Transazione gestito con successo (sono stati emessi dei diagnostici che hanno rilevato delle anomalie)                                      OK_PRESENZA_ANOMALIE
  Contenuto Richiesta Malformato                13        Il contenuto della richiesta non è processabile dal Gateway                                                                                 CONTENUTO_RICHIESTA_NON_RICONOSCIUTO
  Contenuto Risposta Malformato                 14        Il contenuto della risposta non è processabile dal Gateway                                                                                  CONTENUTO_RISPOSTA_NON_RICONOSCIUTO
  Gestione Token Fallita                        15        Sono emersi degli errori durante la gestione/validazione del token                                                                          ERRORE_TOKEN
  Autenticazione Fallita                        16        L'autenticazione del richiedente non è stata effettuata con successo                                                                        ERRORE_AUTENTICAZIONE
  Autorizzazione Negata                         17        La richiesta non è stata autorizzata                                                                                                        ERRORE_AUTORIZZAZIONE
  Violazione Rate Limiting                      18        Rilevata una violazione di una Policy di Rate Limiting                                                                                      CONTROLLO_TRAFFICO_POLICY_VIOLATA
  Violazione Rate Limiting WarningOnly          19        Rilevata una violazione di una Policy di Rate Limiting (configurata in WarningOnly)                                                         CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY
  Superamento Limite Richieste                  20        Rilevato il superamento del numero massimo di richieste simultanee                                                                          CONTROLLO_TRAFFICO_MAX_THREADS
  Superamento Limite Richieste WarningOnly      21        Superamento numero massimo di richieste simultanee consentite sul gateway (controllo configurato in WarningOnly)                            CONTROLLO_TRAFFICO_MAX_THREADS_WARNING_ONLY
  Sicurezza Messaggio Richiesta Fallita         22        La gestione della sicurezza messaggio sulla richiesta non è stata completata con successo                                                   ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA
  Sicurezza Messaggio Risposta Fallita          23        La gestione della sicurezza messaggio sulla risposta non è stata completata con successo                                                    ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA
  Gestione Allegati Richiesta Fallita           24        La gestione degli allegati sulla richiesta non è stata completata con successo                                                              ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA
  Gestione Allegati Risposta Fallita            25        La gestione degli allegati sulla risposta non è stata completata con successo                                                               ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA
  Correlazione Applicativa Richiesta Fallita    26        La gestione della correlazione applicativa sulla richiesta non è stata completata con successo                                              ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA
  Correlazione Applicativa Risposta Fallita     27        La gestione della correlazione applicativa sulla risposta non è stata completata con successo                                               ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA
  Risposta HTTP 3xx                             28        La risposta applicativa contiene un codice di trasporto 3xx                                                                                 HTTP_3xx
  Risposta HTTP 4xx                             29        La risposta applicativa contiene un codice di trasporto 4xx                                                                                 HTTP_4xx
  Risposta HTTP 5xx                             30        La risposta applicativa contiene un codice di trasporto 5xx                                                                                 HTTP_5xx
  Validazione Richiesta Fallita                 31        La validazione della richiesta non è stata completata con successo                                                                          ERRORE_VALIDAZIONE_RICHIESTA
  Validazione Risposta Fallita                  32        La validazione della risposta non è stata completata con successo                                                                           ERRORE_VALIDAZIONE_RISPOSTA
  API Sospesa                                   33        L'API invocata risulta sospesa                                                                                                              ERRORE_SOSPENSIONE
  CORS Preflight Gestione Gateway               34        Richiesta OPTIONS CORS Preflight Request gestita dal Gateway                                                                                CORS_PREFLIGHT_REQUEST_VIA_GATEWAY
  CORS Preflight Gestione Applicativa           35        Richiesta OPTIONS CORS Preflight Request gestita dall'applicativo                                                                           CORS_PREFLIGHT_REQUEST_TRASPARENTE
  Trasformazione Richiesta Fallita              36        La trasformazione della richiesta non è stata completata con successo                                                                       ERRORE_TRASFORMAZIONE_RICHIESTA
  Trasformazione Risposta Fallita               37        La trasformazione della risposta non è stata completata con successo                                                                        ERRORE_TRASFORMAZIONE_RISPOSTA
  Consegna Asincrona in Coda                    38        Tutte le consegne verso i connettori associati all'API sono ancora in attesa di essere consegnate                                           CONSEGNA_MULTIPLA
  Consegna Asincrona Completata                 39        La richiesta pervenuta è stata inoltrata correttamente a tutti i connettori associati all'API                                               CONSEGNA_MULTIPLA_COMPLETATA
  Consegna Asincrona Fallita                    40        La richiesta pervenuta è stata inoltrata a tutti i connettori associati all'API; in alcuni casi la consegna non è andata a buon fine        CONSEGNA_MULTIPLA_FALLITA
  Token non Presente                            41        La richiesta non presenta un token                                                                                                          TOKEN_NON_PRESENTE
  Autenticazione Token Fallita                  42        Nel token ricevuto non sono presenti dei claim richiesti per l'accesso                                                                      ERRORE_AUTENTICAZIONE_TOKEN
  API non Individuata                           43        La richiesta non permette di individuare una API registrata sul Gateway                                                                     API_NON_INDIVIDUATA
  Operazione non Individuata                    44        La richiesta non indirizza un'operazione esistente sull'API invocata                                                                        OPERAZIONE_NON_INDIVIDUATA
  Richiesta già elaborata                       45        La richiesta risulta essere già stata elaborata                                                                                             RICHIESTA_DUPLICATA
  Risposta già elaborata                        46        La risposta risulta essere già stata elaborata                                                                                              RISPOSTA_DUPLICATA
  Disponibile in MessageBox                     47        Messaggio gestibile tramite servizio IntegrationManager                                                                                     MESSAGE_BOX
  Consegna Asincrona in Corso                   48        Alcune consegne verso i connettori associati all'API risultano ancora non completate                                                        CONSEGNA_MULTIPLA_IN_CORSO
 ============================================= ========= =========================================================================================================================================== ================================================= 

Ciascun esito riportato nella tabella precedente è riconducibile ad una tra le seguenti casistiche:

.. table:: Classi di Esiti delle Transazioni
 :class: longtable
 :widths: 50 50
 :name: classiEsitiTransazioneTab

 ================================= ======================================================================= 
 Esito Complessivo                 Codici Corrispondenti                 
 ================================= ======================================================================= 
 Completata con Successo           0,12,38,48,39,47,2,28,19,21,8,34,35   
 Fault Applicativo                 2
 Richiesta Scartata                16,41,42,15,43,44,13,4,33
 Errore di Consegna                2,10,11,29,30,40
 Autorizzazione Negata             17
 Policy Controllo Traffico Violate 18,20
 Errori Servizio I.M. MessageBox   6,7,8,9
 Errori Processamento Richiesta    13,22,24,26,31,36,45
 Errori Processamento Risposta     14,23,25,27,32,37,46
 Altri Codici di Errore            1,3,5
 ================================= ======================================================================= 

