Esempio SOAP: scambi di dati asincroni PDND (ModI)
=================================================

L'esempio fornisce i server e il client per provare, tramite GovWay, le quattro fasi di uno scambio
di dati asincrono PDND: start_interaction, callback_invocation, get_resource e confirmation.

Componenti:
- ServerEService   (porta 9301): applicativo erogatore dell'e-service; riceve start/get_resource/confirmation
                                 e invoca la callback (automaticamente o manualmente);
- ServerCallback   (porta 9302): applicativo fruitore che implementa l'API di callback;
- Client                       : applicativo fruitore che invoca l'e-service;
- CallbackInvoker              : invocazione manuale della callback (target runCallback).


1. INTERFACCE (src/schemi)
--------------------------

- soap-pdnd-async-eservice-compatta.wsdl : e-service con le sole operazioni dello scambio asincrono
      startInteraction  -> start_interaction
      getResource       -> get_resource
      confirmation      -> confirmation
- soap-pdnd-async-eservice-estesa.wsdl   : come la compatta più operazioni sincrone (getCatalogItem, updateCatalogItem, health)
- soap-pdnd-async-callback-compatta.wsdl : API di callback con la sola azione callbackInvocation (callback_invocation)
- soap-pdnd-async-callback-estesa.wsdl   : API di callback con azioni aggiuntive (notifyError, health)


2. CONFIGURAZIONE DI GOVWAY
---------------------------

Token Policy di negoziazione PDND: valorizzare "URL Scambi Asincroni" (default .../token.oauth2.async).

API dell'e-service (wsdl eservice):
- ModI -> Sicurezza Messaggio: "Generazione Token" = "Authorization PDND";
  "Scambio Asincrono" abilitato, Ruolo "Erogazione dati", tempi, "Conferma recupero risposta", "Numero massimo risultati";
- per ogni azione (ModI -> Interazione) indicare la "Fase Asincrona" come da elenco al punto 1
  (le fasi vanno definite nello stesso servizio/port type).

API di callback (wsdl callback):
- ModI -> Sicurezza Messaggio: "Authorization PDND", "Scambio Asincrono" abilitato, Ruolo "Callback",
  "API Erogazione Dati" = API dell'e-service;
- la fase callback_invocation è implicita se il servizio possiede un'unica azione (versione compatta),
  altrimenti va associata all'azione callbackInvocation.

Erogazioni e fruizioni:
- soggetto erogatore: erogazione dell'e-service con connettore http://localhost:9301/
                      fruizione dell'API di callback verso il soggetto fruitore, con connettore
                      ${context:pdndAsyncUrlCallback} (URL di callback ricevuta nello start) oppure statico;
                      ${context:pdndAsyncUrlCallbackOriginal} riporta la URL esattamente come comunicata dal fruitore
                      (la prima, per le API REST, elimina l'eventuale path della risorsa già presente, che viene accodato dal connettore);
- soggetto fruitore : fruizione dell'e-service verso il soggetto erogatore (usata dal Client);
                      erogazione dell'API di callback con connettore http://localhost:9302/
                      (la sua URL di invocazione è, per default, la URL di callback inviata alla PDND).

Le opzioni specifiche (URL di callback fornita dal client, invio purposeId, numero di entità, codici HTTP
di esito positivo, verifica URL di callback, codifica header) si trovano nella sezione "ModI - Scambi Asincroni"
della fruizione/erogazione.


3. PREPARAZIONE
---------------

   ant build
   cp Server.properties.template Server.properties
   cp Client.properties.template Client.properties

Adeguare le URL in Server.properties ('eservice.callback.url': fruizione dell'API di callback) e in
Client.properties ('eservice.url': fruizione dell'e-service), sostituendo 'Erogatore'/'Fruitore' con i
nomi dei soggetti e, se necessario, abilitando l'autenticazione BASIC verso GovWay (username/password).

4. ESECUZIONE DEL FLUSSO
------------------------

Avviare i due server, ciascuno in un terminale dedicato (restano in esecuzione):

   ant runServerEService      # e-service (erogatore), porta 9301
   ant runServerCallback      # API di callback (fruitore), porta 9302

Fase 1 - start_interaction (client -> fruizione dell'e-service):

   ant runClient -Doperation=start

   Se nella fruizione dell'e-service "URL di Callback" = "Fornita dal client", la URL viene inviata dal client
   valorizzando in Client.properties 'start.urlCallback' (e, se diversi dal default, 'start.urlCallback.modalita',
   'start.urlCallback.nome', 'start.urlCallback.codifica', coerenti con la configurazione della fruizione).

   La risposta riporta l'header 'GovWay-Conversation-ID': è l'interactionId emesso dalla PDND,
   da utilizzare in tutte le fasi successive (di seguito <ID>).

Fase 2 - callback_invocation (erogatore -> fruizione dell'API di callback):

   a) automatica: con 'eservice.callback.enabled=true' (Server.properties) il server dell'e-service
      invoca la callback dopo 'eservice.callback.delaySeconds' secondi dalla start_interaction;
      l'esito viene stampato nel terminale del server dell'e-service ([callback] ...).

   b) manuale: impostare 'eservice.callback.enabled=false', riavviare il server dell'e-service e,
      dopo la start_interaction, invocare:

         ant runCallback -DconversationId=<ID> [-DentityNumber=<n>]

      (entityNumber di default 10; non deve superare il "Numero massimo risultati" dell'API).
      L'invocazione manuale utilizza le stesse proprietà 'eservice.callback.*' di quella automatica
      e può essere usata anche per provare i casi di errore (es. callback ripetuta -> 409,
      tempo massimo di risposta scaduto -> 400, interactionId inesistente -> 400).

   La ricezione della callback è visibile nel terminale del server dell'API di callback.

Fase 3 - get_resource (ripetibile, es. scaricamento a blocchi):

   ant runClient -Doperation=getResource -DconversationId=<ID>

   (offset/limit in Client.properties: getResource.offset, getResource.limit)

Fase 4 - confirmation (solo se nell'API è abilitata "Conferma recupero risposta"):

   ant runClient -Doperation=confirmation -DconversationId=<ID>

   Dopo la conferma ulteriori get_resource/confirmation ricevono 409.

Operazioni sincrone (solo versione "estesa" dell'e-service, nessuna fase associata):

   ant runClient -Doperation=catalog      # getCatalogItem (catalog.code in Client.properties)
   ant runClient -Doperation=health

Lo stato delle interazioni è consultabile in console: Configurazione -> Cache PDND -> Interazioni Asincrone
(una riga per il ruolo Fruitore e una per il ruolo Erogatore se i due soggetti sono gestiti dallo stesso GovWay).
