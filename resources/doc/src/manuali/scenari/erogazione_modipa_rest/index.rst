.. _scenari_erogazione_rest_modipa:

Erogazione REST ModI PA
=======================

Obiettivo
---------
Esporre un servizio REST accessibile in accordo alla normativa prevista dal Modello di Interoperabilità.

Sintesi
-------
Mostriamo in questa sezione come procedere per l'esposizione di un servizio REST da erogare nel rispetto della normativa italiana alla base dell'interoperabilità tra i sistemi della pubblica amministrazione. In particolare andiamo ad illustrare lo scenario, tra quelli prospettati nel Modello di Interoperabilità di AGID, che prevede le più ampie caratteristiche di sicurezza e affidabilità.
I requisiti di riferimento sono quelli descritti nella sezione 5.4.2 del Modello di Interoperabilità che, oltre a garantire la confidenzialità della comunicazione con autenticazione dell'interlocutore, prevedono supporto a garanzia dell'integrità del messaggio e non ripudiabilità dell'avvenuta trasmissione.

La figura seguente descrive graficamente questo scenario.

   .. figure:: ../_figure_scenari/ErogazioneModIPA.png
    :scale: 80%
    :align: center
    :name: erogazione_modipa_fig

    Erogazione ModI PA

Le caratteristiche principali di questo scenario sono:

1. Un applicativo eroga un servizio, rivolto a fruitori di domini esterni, in conformità al Modello di Interoperabilità AGID
2. La comunicazione con i domini esterni avviene su un canale gestito con sicurezza canale di profilo "ID_AUTH_CHANNEL_02"
3. La confidenzialità e autenticità della comunicazione tra il servizio erogato e ciascun fruitore è garantita tramite sicurezza a livello messaggio con profilo "ID_AUTH_REST_02"
4. L'integrità del messaggio scambiato è garantita tramite sicurezza messaggio aggiuntiva di profilo "INTEGRITY_REST_01"
5. Ciascun fruitore riceve conferma di ricezione del messaggio da parte dell'erogatore
6. Garanzia di opponibilità ai terzi e non ripudio delle trasmissioni con persistenza delle prove di trasmissione


Esecuzione
----------
L'esecuzione dello scenario si basa sui seguenti elementi:

- una API "PetStore", basata su REST, profilo di interazione Bloccante e profili di sicurezza "ID_AUTH_CHANNEL_02" e "INTEGRITY_REST_01 con ID_AUTH_REST_02".
- un'istanza Govway per la gestione del profilo ModI PA nel dominio dell'erogatore.
- un client del dominio esterno che invoca la "POST /pet" diretto all'erogazione esposta da Govway.
- il server PetStore di esempio che riceve le richieste inoltrate dal Govway e produce le relative risposte. Per questo scenario viene utilizzato il server disponibile on line all'indirizzo 'http://petstore.swagger.io/'.

Per eseguire e verificare lo scenario si può utilizzare il progetto Postman a corredo con la request "5. Erogazione ModI PA", che è stato preconfigurato per il funzionamento con le caratteristiche descritte sopra.

Dopo aver eseguito la "Send" e verificato il corretto esito dell'operazione è possibile andare a verificare cosa è accaduto, nel corso dell'elaborazione della richiesta, andando a consultare la console govwayMonitor:

1. Lo scambio del messaggio con il dominio fruitore (comunicazione interdominio) avviene in accordo al profilo "ID_AUTH_CHANNEL_02" e quindi con protocollo SSL e autenticazione client. Dal dettaglio della transazione si possono consultare i messaggi diagnostici dove è visibile la fase di autenticazione del client con i dati di validazione del certificato ricevuto (:numref:`modipa_ssl_auth_fig`).

   .. figure:: ../_figure_scenari/modipa_ssl_auth.png
    :scale: 80%
    :align: center
    :name: modipa_ssl_auth_fig

    Sicurezza canale "ID_AUTH_CHANNEL_02"

2. Dal dettaglio della richiesta si può visualizzare il messaggio che è stato inviato dal fruitore, come in :numref:`modipa_erogazione_messaggio_richiesta_fig`. Come si nota, al payload JSON è associato un insieme di header HTTP tra i quali "Authorization", che contiene il token di sicurezza, e "Digest" che contiene il valore per la verifica dell'integrità del payload.

   .. figure:: ../_figure_scenari/modipa_erogazione_messaggio_richiesta.png
    :scale: 80%
    :align: center
    :name: modipa_erogazione_messaggio_richiesta_fig

    Messaggio inviato dal fruitore

3. Grazie alle configurazioni presenti nell'erogazione, ed in particolare alla relazione di trust stabilita con il fruitore, Govway è in grado di validare i dati di sicurezza ricevuti andando a decodificare il token e a verificare il digest del messaggio. Nella fase di validazione del token si può notare come la sezione header (:numref:`modipa_jwtio_header_fig`) riporti l'identità del fruitore e il suo certificato X.509, mentre la sezione payload (:numref:`modipa_jwtio_payload_fig`) contenga i riferimenti temporali (iat, nbf, exp) e le componenti firmate del messaggio (tra cui il digest).

   .. figure:: ../_figure_scenari/modipa_jwtio_header.png
    :scale: 80%
    :align: center
    :name: modipa_jwtio_header_fig

    Sezione "Header" del Token di sicurezza

   .. figure:: ../_figure_scenari/modipa_jwtio_payload.png
    :scale: 80%
    :align: center
    :name: modipa_jwtio_payload_fig

    Sezione "Payload" del Token di sicurezza

4. Il messaggio ricevuto dal Govway viene quindi validato, sulla base dei profili di sicurezza previsti nello scambio, verificando in questo caso l'identità del fruitore, la validità temporale, la corrispondenza del digest relativo al payload. Solo in caso di superamento dell'intero processo di validazione, il messaggio viene inoltrato al servizio erogatore.
Le evidenze del processo di validazione sono visibili sulla govwayMonitor, andando a consultare la traccia del messaggio di richiesta (:numref:`modipa_traccia_richiesta_fig`). Nella sezione "Sicurezza Messaggio" sono riportate le informazioni estratte dal token di sicurezza presente nel messaggio.

   .. figure:: ../_figure_scenari/modipa_traccia_richiesta.png
    :scale: 80%
    :align: center
    :name: modipa_traccia_richiesta_fig

    Traccia della richiesta elaborata dall'erogatore

5. Dopo l'inoltro al servizio erogatore, Govway riceve la risposta e la elabora producendo il relativo token di sicurezza utilizzando le impostazioni di firma fornite nell'ambito dell'erogazione relativamente all'elaborazione della risposta. Sulla console govwayMonitor è possibile visualizzare il messaggio di risposta in uscita, dove si rileva la presenza del token prodotto nell'header HTTP "Authorization" (analogamente a :numref:`modipa_erogazione_messaggio_richiesta_fig`).

.. _modipa_conformita:

Conformità ai requisiti ModI PA
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
I requisiti iniziali, legati alla comunicazione basata su uno scenario ModI PA, sono verificati dalle seguenti evidenze:

1. La trasmissione è basata sul profilo "ID_AUTH_CHANNEL_02", riguardo la sicurezza canale, come evidenziato nei messaggi diagnostici dalla presenza degli elementi dell'handshake SSL e relativi dati dei certificati scambiati (:numref:`modipa_ssl_auth_fig`).

2. La sicurezza messaggio applicata è quella dei profili "ID_AUTH_REST_02" e "INTEGRITY_REST_01", come ampiamente mostrato nelle tracce dei messaggi di richiesta e risposta, dove sono presenti i certificati degli applicativi e le firme dei payload (e le relative validazioni).

3. La conferma di ricezione da parte dell'erogatore è costituita dalla risposta ottenuta dal fruitore, sul profilo di interazione bloccante, con il token di sicurezza e la firma del payload applicati sul messaggio di risposta.

4. Il non ripudio della trasmissione da parte del fruitore è garantito tramite la conservazione del messaggio ottenuto, comprensivo di riferimenti temporali, digest del payload, identità del mittente, il tutto garantito dalla firma digitale.

5. L'opponibilità verso i terzi è garantita dal mantenimento nell'archivio delle evidenze tracciate, citate ai punti precedenti, con la possibilità, offerta dalla console govwayMonitor, di effettuare successive ricerche per la consultazione delle stesse.


Configurazione
--------------
Per la configurazione dello scenario descritto è necessario intervenire sulla govwayConsole (lato fruitore ed erogatore in base all'ambito di propria competenza). Per operare con la govwayConsole in modo conforme a quanto previsto dalla specifica del Modello di Interoperabilità 2018 si deve attivare, nella testata dell'interfaccia, il Profilo di Interoperabilità "ModI PA" (:numref:`modipa_profilo_fig`).

   .. figure:: ../_figure_scenari/modipa_profilo.png
    :scale: 80%
    :align: center
    :name: modipa_profilo_fig

    Profilo ModI PA della govwayConsole

.. _modipa_config_tracciamento:


Salvataggio Messaggi
~~~~~~~~~~~~~~~~~~~~
Per far gestire a Govway la persistenza dei messaggi scambiati, come prova di trasmissione per l'opponibilità ai terzi, è necessario intervenire sulla configurazione della funzionalità di tracciamento (sezione del menu "Configurazione > Tracciamento", abilitando la "Registrazione Messaggi" e prevendedo la persistenza quanto meno delle comunicazioni scambiate tra i due gateway (:numref:`modipa_tracciamento_richiesta_fig` e :numref:`modipa_tracciamento_risposta_fig`).

   .. figure:: ../_figure_scenari/modipa_tracciamento_richiesta.png
    :scale: 80%
    :align: center
    :name: modipa_tracciamento_richiesta_fig

    Abilitazione del salvataggio delle richieste in uscita

   .. figure:: ../_figure_scenari/modipa_tracciamento_risposta.png
    :scale: 80%
    :align: center
    :name: modipa_tracciamento_risposta_fig

    Abilitazione del salvataggio delle risposte in ingresso

Si procede quindi con i passi di configurazione del servizio.

.. _modipa_api_profili:

Registrazione API
~~~~~~~~~~~~~~~~~
Si registra l'API "PetStore", fornendo il relativo descrittore OpenAPI 3, selezionando i profili "ID_AUTH_CHANNEL_02" (sicurezza canale) e "INTEGRITY_REST_01 con ID_AUTH_REST_02" (sicurezza messaggio) nella sezione "ModI PA" (:numref:`modipa_profili_api_fig`).

   .. figure:: ../_figure_scenari/modipa_profili_api.png
    :scale: 80%
    :align: center
    :name: modipa_profili_api_fig

    Profilo ModI PA della govwayConsole


Applicativo Esterno
~~~~~~~~~~~~~~~~~~~
È opzionalmente possibile registrare l'applicativo esterno che corrisponde al fruitore del servizio. Questa scelta può essere fatta in base al tipo di autorizzazione che si è impostata sui fruitori. Vediamo i seguenti casi:

- Se il truststore utilizzato da Govway per l'autenticazione dei fruitori (sicurezza messaggio) contiene i singoli certificati degli applicativi autorizzati, questo passo può anche essere omesso. La gestione del truststore è sufficiente a stabilire i singoli fruitori autorizzati.
- Se il truststore contiene la CA emittente dei certificati utilizzati dai fruitori, l'autorizzazione puntuale non è possibile a meno di non procedere con la registrazione puntuale degli applicativi fornendo i singoli certificati necessari per l'identificazione (:numref:`modipa_applicativo_esterno_fig`).

   .. figure:: ../_figure_scenari/modipa_applicativo_esterno.png
    :scale: 80%
    :align: center
    :name: modipa_applicativo_esterno_fig

    Configurazione applicativo esterno (fruitore)


Erogazione
~~~~~~~~~~
Si registra l'erogazione "PetStore", relativa all'API precedentemente inserita, indicando i dati specifci nella sezione "ModI PA Richiesta" (:numref:`modipa_erogazione_richiesta_fig`). In questo contesto vengono inseriti i dati necessari per validare le richieste in ingresso.

   .. figure:: ../_figure_scenari/modipa_erogazione_richiesta.png
    :scale: 80%
    :align: center
    :name: modipa_erogazione_richiesta_fig

    Configurazione richiesta dell'erogazione

La sezione "ModI PA Risposta" si utilizza per indicare i parametri per la produzione del token di sicurezza da inserire nel messaggio di risposta (:numref:`modipa_erogazione_risposta_fig`).

   .. figure:: ../_figure_scenari/modipa_erogazione_risposta.png
    :scale: 80%
    :align: center
    :name: modipa_erogazione_risposta_fig

    Configurazione risposta dell'erogazione

Se si è scelto di registrare gli applicativi esterni, fruitori del servizio, è possibile intervenire sulla configurazione del "Controllo degli Accessi" per l'erogazione, in modo da specificare i singoli applicativi fruitori autorizzati ad effettuare richieste al servizio erogato (:numref:`modipa_auth_applicativi_fig`).

   .. figure:: ../_figure_scenari/modipa_auth_applicativi.png
    :scale: 80%
    :align: center
    :name: modipa_auth_applicativi_fig

    Controllo accessi con autorizzazione degli applicativi esterni


