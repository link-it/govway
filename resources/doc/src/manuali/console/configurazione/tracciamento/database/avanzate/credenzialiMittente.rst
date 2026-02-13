.. _tracciamentoTransazioniDBCredenzialiMittente:

Indicizzazione Credenziali Mittente
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il gateway indicizza informazioni sul mittente di ogni richiesta (credenziali di autenticazione, claim del token, indirizzo IP del client, ecc.) nella tabella *credenziale_mittente* e memorizza i riferimenti nella tabella *transazioni*. Questo meccanismo consente ricerche efficienti dalla console di monitoraggio (:ref:`mon_intro`) e dalle relative API (:ref:`apiRest`).

.. _tracciamentoTransazioniDBCredenzialiMittenteAbilitazione:

Abilitazione per tipo di credenziale
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

È possibile disabilitare l'indicizzazione per uno o più tipi di credenziale, evitando la creazione di entry nella tabella *credenziale_mittente* e la valorizzazione della colonna corrispondente nella tabella *transazioni*. Disabilitare i tipi non utilizzati consente di risparmiare risorse database e migliorare le prestazioni.

.. note::
    Disabilitando l'indicizzazione di un tipo di credenziale, non sarà più possibile effettuare ricerche basate su quel criterio tramite la console di monitoraggio (:ref:`mon_intro`) o tramite le relative API (:ref:`apiRest`). Ad esempio, disabilitando il tipo *token_clientId*, le transazioni non saranno più ricercabili per client id del token.

La configurazione globale avviene nel file '/etc/govway/govway_local.properties'. Di default tutti i tipi sono abilitati.

   ::

      # Principal (credenziale di autenticazione trasporto)
      org.openspcoop2.pdd.transazioni.credenzialiMittente.trasporto.enabled=true

      # Token: issuer
      org.openspcoop2.pdd.transazioni.credenzialiMittente.token_issuer.enabled=true

      # Token: client id
      org.openspcoop2.pdd.transazioni.credenzialiMittente.token_clientId.enabled=true

      # Token: subject
      org.openspcoop2.pdd.transazioni.credenzialiMittente.token_subject.enabled=true

      # Token: username
      org.openspcoop2.pdd.transazioni.credenzialiMittente.token_username.enabled=true

      # Token: e-mail
      org.openspcoop2.pdd.transazioni.credenzialiMittente.token_eMail.enabled=true

      # Indirizzo IP del client
      org.openspcoop2.pdd.transazioni.credenzialiMittente.client_address.enabled=true

      # Eventi
      org.openspcoop2.pdd.transazioni.credenzialiMittente.eventi.enabled=true

      # Tag
      org.openspcoop2.pdd.transazioni.credenzialiMittente.gruppi.enabled=true

      # API
      org.openspcoop2.pdd.transazioni.credenzialiMittente.api.enabled=true

La tabella seguente riassume i tipi di credenziale configurabili e la colonna della tabella *transazioni* interessata dalla disabilitazione.

.. table:: Tipi di credenziale
   :widths: 30 30 40

   ============================  =============================  ==============================
   Tipo                          Proprietà                      Colonna *transazioni*
   ============================  =============================  ==============================
   Principal                     trasporto                      trasporto_mittente
   Token: issuer                 token_issuer                   token_issuer
   Token: client id              token_clientId                 token_client_id
   Token: subject                token_subject                  token_subject
   Token: username               token_username                 token_username
   Token: e-mail                 token_eMail                    token_mail
   Indirizzo IP del client       client_address                 client_address
   Eventi                        eventi                         eventi_gestione
   Tag                           gruppi                         gruppi
   API                           api                            uri_accordo_servizio
   ============================  =============================  ==============================

.. _tracciamentoTransazioniDBCredenzialiMittenteOverride:

Ridefinizione per singola erogazione o fruizione
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

La configurazione globale può essere ridefinita sulla singola erogazione o fruizione utilizzando le :ref:`configProprieta`. Il nome della proprietà è composto dal prefisso *trace.index.* seguito dal nome del tipo di credenziale indicato nella colonna *Proprietà* della tabella precedente. I valori ammessi sono *true* o *false*.

Di seguito l'elenco completo delle proprietà configurabili sulla singola erogazione o fruizione:

   ::

      trace.index.trasporto=true/false
      trace.index.token_issuer=true/false
      trace.index.token_clientId=true/false
      trace.index.token_subject=true/false
      trace.index.token_username=true/false
      trace.index.token_eMail=true/false
      trace.index.client_address=true/false
      trace.index.eventi=true/false
      trace.index.gruppi=true/false
      trace.index.api=true/false

Se una proprietà non è definita sulla singola erogazione o fruizione, viene utilizzato il valore globale configurato in *govway_local.properties*.

.. _tracciamentoTransazioniDBCredenzialiMittenteModI:

Indicizzazione credenziali dal token ModI Authorization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Per le API REST con profilo ModI e opzione "Generazione Token: Authorization ModI" (token generato dal fruitore anziché ottenuto dalla PDND), il gateway può indicizzare i claim *issuer*, *subject* e *client_id* estratti dal token JWT presente nell'header Authorization. Le informazioni vengono memorizzate nelle stesse colonne utilizzate per i token gestiti tramite token policy di validazione (*token_issuer*, *token_client_id*, *token_subject*).

La configurazione globale avviene nel file '/etc/govway/govway_local.properties'. Di default l'indicizzazione è abilitata.

   ::

      # Token ModI Authorization: issuer
      org.openspcoop2.pdd.transazioni.credenzialiMittente.modi.token_issuer.enabled=true

      # Token ModI Authorization: client id
      org.openspcoop2.pdd.transazioni.credenzialiMittente.modi.token_clientId.enabled=true

      # Token ModI Authorization: subject
      org.openspcoop2.pdd.transazioni.credenzialiMittente.modi.token_subject.enabled=true

La configurazione globale può essere ridefinita sulla singola erogazione o fruizione utilizzando le :ref:`configProprieta`:

   ::

      trace.index.modi.token_issuer=true/false
      trace.index.modi.token_clientId=true/false
      trace.index.modi.token_subject=true/false

.. _tracciamentoTransazioniDBCredenzialiMittenteUpdateAfterSeconds:

Aggiornamento temporale delle credenziali
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Le credenziali salvate nella tabella *credenziale_mittente* contengono un timestamp che indica il momento della creazione. Il gateway aggiorna tale data quando accede ad una credenziale la cui data di creazione è più vecchia della soglia configurata. Questo meccanismo consente di individuare le credenziali non più utilizzate.

   ::

      # Soglia in secondi per l'aggiornamento della data di ultima consultazione.
      # Default: 3600 (1 ora)
      org.openspcoop2.pdd.transazioni.credenzialiMittente.updateAfterSeconds=3600

.. _tracciamentoTransazioniDBCredenzialiMittenteSanitizePort:

Sanitizzazione porta indirizzo IP
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

L'indirizzo IP del client viene determinato esaminando gli header HTTP della richiesta nel seguente ordine di priorità; viene utilizzato il valore presente nel primo header trovato:

1. *X-Forwarded-For*: de facto standard
2. *Forwarded-For*: variante senza il prefisso 'X-'
3. *X-Forwarded*: non standard, utilizzato da alcuni proxy
4. *Forwarded*: standard RFC 7239
5. *X-Client-IP*: non standard, utilizzato da Amazon EC2, Heroku
6. *Client-IP*: non standard, utilizzato da alcuni proxy e load balancer
7. *X-Cluster-Client-IP*: non standard, utilizzato da Rackspace LB, Zeus Web Server
8. *Cluster-Client-IP*: variante senza il prefisso 'X-'

Se nessun header è presente, viene utilizzato l'indirizzo IP della connessione socket.

Alcuni di questi header possono contenere l'indicazione della porta (es. *192.168.1.1:8080* per IPv4, *[2001:db8::1]:8080* per IPv6). In particolare l'header *Forwarded* definito dalla RFC 7239 utilizza il formato *for=<indirizzo>* (es. *for=192.168.1.1:8080*, *for="[2001:db8::1]:8080"*). La presenza della porta causa la creazione di entry distinte nella tabella *credenziale_mittente* per lo stesso indirizzo IP, moltiplicando inutilmente i record.

Abilitando la seguente opzione, il gateway rimuove l'informazione sulla porta dall'indirizzo di trasporto prima di salvarlo nella tabella delle credenziali. La sanitizzazione gestisce correttamente tutti i formati: indirizzi IPv4 con porta, indirizzi IPv6 racchiusi tra parentesi quadre con porta e il formato *for=* dell'header *Forwarded* (RFC 7239). Nel caso di header contenenti indirizzi multipli separati da virgola, la sanitizzazione viene applicata a ciascun indirizzo.

   ::

      # Abilita la sanitizzazione della porta dall'indirizzo di trasporto.
      # Default: true
      org.openspcoop2.pdd.transazioni.tracciamentoDB.transportClientAddress.sanitizePort=true
