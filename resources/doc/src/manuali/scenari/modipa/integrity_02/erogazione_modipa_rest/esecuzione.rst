.. _scenari_erogazione_rest_modipa_integrity_02_esecuzione:

Esecuzione
----------

.. note::

  Al fine di avere una consultazione immediata delle informazioni di interesse per lo scenario si consiglia di impostare, nella console 'govwayMonitor', nel menù in alto a destra il Profilo di Interoperabilità 'ModI'. Si suggerisce inoltre di selezionare il soggetto 'Ente' per visualizzare solamente le transazioni di interesse allo scenario e ignorare le transazioni "di servizio" necessarie ad implementare la controparte.

  .. figure:: ../../../_figure_scenari/modipa_profilo_monitor.png
   :scale: 80%
   :align: center
   :name: modipa_profilo_monitor_integrity_02_fig

   Profilo ModI della govwayMonitor

L'esecuzione dello scenario è del tutto analogo a quello descritto nello scenario :ref:`scenari_erogazione_rest_modipa_auth_pdnd_esecuzione` con la sola eccezione del pattern di sicurezza aggiuntivo utilizzato in questo scenario: "INTEGRITY_REST_02".

Per eseguire e verificare lo scenario si può utilizzare il progetto Postman a corredo con la request "Profilo ModI REST - IntegrityRest02+PDND - IN App3" che è stata preconfigurata per il funzionamento con le caratteristiche descritte sopra.

.. figure:: ../../../_figure_scenari/postman_integrity_02_rest_in_ok.png
 :scale: 70%
 :align: center
 :name: postman_integrity_02_rest_in_ok

 Pattern IntegrityRest02+PDND - Erogazione API REST, esecuzione da Postman


Dopo aver eseguito la "Send" e verificato il corretto esito dell'operazione è possibile andare a verificare cosa è accaduto, nel corso dell'elaborazione della richiesta, andando a consultare la console 'govwayMonitor'.

.. note::

  Le informazioni ottenute tramite le :ref:`modipa_passiPreliminari_api_pdnd` (chiavi pubbliche JWK e informazioni sui client) vengono salvate su cache locali. Al fine di forzare nuove invocazioni verso la "PDND simulata" è necessario attendere un minuto rispetto a precedenti invocazioni ed effettuare il reset delle cache locali di GovWay accedendo alla sezione :ref:`strumenti_runtime` della console di gestione '*govwayConsole*' e cliccando sul link '*Svuota tutte le Cache*'.


Le evidenze del processo di validazione relative al token PDND sono le medesime descritte nella scenario :ref:`scenari_erogazione_rest_modipa_auth_pdnd_esecuzione`. 


1. Dal dettaglio della richiesta si può visualizzare il messaggio che è stato inviato dal fruitore, come in :numref:`modipa_erogazione_messaggio_richiesta_integrity_pdnd_fig`. Come si nota, al payload JSON è associato un insieme di header HTTP tra i quali "Authorization" e "Agid-Jwt-Signature" che contengono rispettivamente il token di sicurezza che il fruitore ha ottenuto dalla PDND e il token di integrità. È inoltre presente l'header http "Digest" che contiene il valore per la verifica dell'integrità del payload.

.. figure:: ../../../_figure_scenari/modipa_erogazione_messaggio_richiesta_integrity.png
 :scale: 80%
 :align: center
 :name: modipa_erogazione_messaggio_richiesta_integrity_pdnd_fig


2. Grazie alle configurazioni presenti nell'erogazione, ed in particolare all'indicazione che il token ricevuto deve essere validato tramite Token Policy PDND, GovWay è in grado di validare i dati di sicurezza ricevuti (:numref:`modipa_pdnd_validazione_token_02`) e decodificare il token. 

.. figure:: ../../../_figure_scenari/modipa_pdnd_validazione_token.png
 :scale: 60%
 :align: center
 :name: modipa_pdnd_validazione_token_02

 Evidenza diagnostica della validazione del token

3. Vengono inoltre validati gli ulteriori header "Agid-Jwt-Signature" e "Digest" rispetto al pattern 'INTEGRITY_REST_02' indicato nella configurazione dell'API (:numref:`modipa_pdnd_integrity_token_1`). La validazione del token di integrità viene effettuata scaricando la chiave pubblica, corrispondente al kid presente nel token, tramite le :ref:`modipa_passiPreliminari_api_pdnd`. Nello storico delle transazioni è possibile vedere come GovWay durante la gestione della richiesta di erogazione scaturisca un'ulteriore chiamata verso la PDND per ottenere la chiave pubblica (:numref:`modipa_pdnd_integrity_token_2`). La chiave pubblica una volta prelevata dalla PDND verrà aggiunta in una cache locale e le successive richieste non provocheranno ulteriori chiamate verso la PDND.

.. figure:: ../../../_figure_scenari/modipa_pdnd_validazione_token_integrity_1.png
 :scale: 60%
 :align: center
 :name: modipa_pdnd_integrity_token_1

 Evidenza diagnostica della validazione del token di integrità

.. figure:: ../../../_figure_scenari/modipa_pdnd_validazione_token_integrity_2.png
 :scale: 60%
 :align: center
 :name: modipa_pdnd_integrity_token_2

 Evidenza diagnostica della chiamata verso la PDND per ottenere la chiave pubblica

4. Analizzando il token di integrità "Agid-Jwt-Signature" ricevuto nella sezione header (:numref:`modipa_jwtio_header_integrity02_fig`) si può notare che non viene riportata l'identità del fruitore tramite certificato X.509 come avveniva per il pattern INTEGRITY_REST_01 descritto nella scenario :ref:`scenari_modi_integrity` ma bensì tramite il claim 'kid' che corrisponde all'identificativo della chiave pubblica registrata sulla PDND. L'identificativo 'kid' verrà utilizzato da GovWay per richiedere la chiave pubblica tramite le :ref:`modipa_passiPreliminari_api_pdnd` (:numref:`modipa_jwtio_header_integrity02_kid_fig`). Nella sezione payload (:numref:`modipa_jwtio_payload_integrity02_fig`) sono invece presenti gli header http firmati (tra cui il valore dell'header 'Digest') che servono a garantire l'integrità della richiesta, insieme ai riferimenti temporali (iat, nbf, exp) e all'audience (aud).

.. figure:: ../../../_figure_scenari/modipa_jwtio_header_integrity02.png
 :scale: 80%
 :align: center
 :name: modipa_jwtio_header_integrity02_fig

 Sezione "Header" del Token 'Agid-Jwt-Signature' con pattern 'INTEGRITY_REST_02'

.. figure:: ../../../_figure_scenari/modipa_jwtio_header_integrity02_kid.png
 :scale: 80%
 :align: center
 :name: modipa_jwtio_header_integrity02_kid_fig

 Dettaglio della url di invocazione utilizzata da GovWay per prelevare la chiave pubblica dalla PDND

.. figure:: ../../../_figure_scenari/modipa_jwtio_payload_integrity02.png
 :scale: 80%
 :align: center
 :name: modipa_jwtio_payload_integrity02_fig

 Sezione "Payload" del Token 'Agid-Jwt-Signature' con pattern 'INTEGRITY_REST_02'

5. Vengono inoltre recuperate e associate alla traccia maggiori informazioni sull'organizzazione afferente al 'client-id' presente nel token, sempre attraverso le :ref:`modipa_passiPreliminari_api_pdnd` (:numref:`modipa_jwtio_header_integrity02_clientInfo1_fig`). Nello storico delle transazioni è possibile vedere come GovWay durante la gestione della richiesta di erogazione scaturisca due ulteriori chiamate verso la PDND per ottenere maggiori informazioni sul client e sull'organizzazione (:numref:`modipa_jwtio_header_integrity02_clientInfo2_fig`). Le informazioni recuperate dalla PDND verranno aggiunte in una cache locale e le successive richieste non provocheranno ulteriori chiamate verso la PDND.

.. figure:: ../../../_figure_scenari/modipa_jwtio_header_integrity02_clientInfo1.png
 :scale: 80%
 :align: center
 :name: modipa_jwtio_header_integrity02_clientInfo1_fig

 Informazioni recuperate dalla PDND sull'organizzazione associata al 'client-id'

.. figure:: ../../../_figure_scenari/modipa_jwtio_header_integrity02_clientInfo2.png
 :scale: 80%
 :align: center
 :name: modipa_jwtio_header_integrity02_clientInfo2_fig

 Evidenza diagnostica delle chiamate verso la PDND per ottenere maggiori informazioni sul 'client-id'

6. Le evidenze del processo di validazione relativo al pattern "INTEGRITY_REST_02" sono visibili sulla govwayMonitor, andando a consultare la traccia del messaggio di richiesta (:numref:`modipa_traccia_richiesta_integrity02_fig`). Nella sezione "Sicurezza Messaggio" sono riportate le informazioni estratte dai token di sicurezza presenti, tra cui si può notare il digest e gli header http firmati.

.. figure:: ../../../_figure_scenari/modipa_traccia_richiesta_integrity02.png
 :scale: 80%
 :align: center
 :name: modipa_traccia_richiesta_integrity02_fig

 Traccia della richiesta elaborata dall'erogatore, con pattern 'INTEGRITY_REST_02'




**Conformità ai requisiti ModI**

I requisiti iniziali, legati alla comunicazione basata su uno scenario ModI, sono verificati dalle seguenti evidenze:

1. la sicurezza messaggio applicata è quella dei pattern "ID_AUTH_REST_01 via PDND" + "INTEGRITY_REST_02" come ampiamente mostrato precedentemente dove sono stati mostrati i token validati e i criteri autorizzativi;

2. la validazione del token di integrità viene effettuata scaricando la chiave pubblica, corrispondente al kid presente nel token, tramite le :ref:`modipa_passiPreliminari_api_pdnd`;

3. l'identificazione del fruitore avviene rispetto al claim 'client_id' presente all'interno del token e ulteriori informazioni sull'organizzazione afferente vengono ottenute invocando le :ref:`modipa_passiPreliminari_api_pdnd`.
