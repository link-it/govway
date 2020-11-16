.. _scenari_fruizione_rest_modipa:

Fruizione REST ModI PA
======================

Obiettivo
---------
Fruire di un servizio REST accessibile in accordo alla normativa prevista dal Modello di Interoperabilità.

Sintesi
-------
Mostriamo in questa sezione come procedere per l'integrazione di un applicativo con un servizio REST erogato nel rispetto della normativa italiana alla base dell'interoperabilità tra i sistemi della pubblica amministrazione. In particolare andiamo ad illustrare lo scenario, tra quelli prospettati nel Modello di Interoperabilità di AGID, che prevede le più ampie caratteristiche di sicurezza e affidabilità.
I requisiti di riferimento sono quelli descritti nella sezione 5.4.2 del Modello di Interoperabilità che, oltre a garantire la confidenzialità della comunicazione con autenticazione dell'interlocutore, prevedono supporto a garanzia dell'integrità del messaggio e non ripudiabilità dell'avvenuta trasmissione.

La figura seguente descrive graficamente questo scenario.

   .. figure:: ../_figure_scenari/FruizioneModIPA.png
    :scale: 80%
    :align: center
    :name: fruizione_modipa_fig

    Fruizione ModI PA

Le caratteristiche principali di questo scenario sono:

1. Un applicativo fruitore che dialoga con il servizio erogato in modalità ModI PA in accordo ad una API condivisa
2. La comunicazione diretta verso il dominio erogatore veicolata su un canale gestito con sicurezza canale di profilo "ID_AUTH_CHANNEL_02"
3. La confidenzialità e autenticità della comunicazione tra fruitore ed erogatore è garantita tramite sicurezza a livello messaggio con profilo "ID_AUTH_REST_02"
4. L'integrità del messaggio scambiato è garantita tramite sicurezza messaggio aggiuntiva di profilo "INTEGRITY_REST_01"
5. L'applicativo fruitore ottiene e conserva la conferma di ricezione del messaggio da parte dell'erogatore
6. Garanzia di opponibilità ai terzi e non ripudio delle trasmissioni


Esecuzione
----------
L'esecuzione dello scenario si basa sui seguenti elementi:

- una API "PetStore", basata su REST, profilo di interazione Bloccante e profili di sicurezza "ID_AUTH_CHANNEL_02" e "INTEGRITY_REST_01 con ID_AUTH_REST_02".
- Un'istanza Govway per la gestione del profilo ModI PA nel dominio del fruitore.
- un client che invoca la "POST /pet" con un messaggio di esempio diretto al Govway.

Per eseguire e verificare lo scenario si può utilizzare il progetto Postman a corredo con la request "6. Fruizione ModI PA", che è stato preconfigurato per il funzionamento con le caratteristiche descritte sopra.

Dopo aver eseguito la "Send" e verificato il corretto esito dell'operazione è possibile andare a verificare cosa è accaduto nelle diverse fasi dell'esecuzione andando a consultare le console govwayMonitor:

1. Il messaggio di richiesta inviato dal fruitore viene elaborato da Govway che, tramite la configurazione della firma digitale associata all'applicativo mittente, è in grado di produrre il token di sicurezza da inviare con la richiesta all'erogatore. Da govwayMonitor si può visualizzare il messaggio di richiesta in uscita che è il medesimo di quello in entrata con la differenza che è stato aggiunto il token di sicurezza tra gli header HTTP (:numref:`modipa_fruizione_messaggio_richiesta_fig`).

   .. figure:: ../_figure_scenari/modipa_fruizione_messaggio_richiesta.png
    :scale: 80%
    :align: center
    :name: modipa_fruizione_messaggio_richiesta_fig

    Messaggio di richiesta in uscita (con token di sicurezza inserito nell'header HTTP)

2. Col processo di validazione del token di sicurezza, Govway estrae le informazioni in esso contenute. L'header e il payload del token sono identici a quelli visualizzati nello scenario di erogazione REST, relativamente al messaggio in uscita (:numref:`modipa_jwtio_header_fig` e :numref:`modipa_jwtio_payload_fig`).

3. Lo scambio del messaggio con il dominio erogatore (comunicazione interdominio) avviene in accordo al profilo "ID_AUTH_CHANNEL_02" e quindi con protocollo SSL e autenticazione client. Dal dettaglio della transazione si possono consultare i messaggi diagnostici dove è visibile la fase di apertura della connessione SSL (:numref:`modipa_ssl_auth_fruitore_fig`).

   .. figure:: ../_figure_scenari/modipa_ssl_auth_fruitore.png
    :scale: 80%
    :align: center
    :name: modipa_ssl_auth_fruitore_fig

    Sicurezza canale "ID_AUTH_CHANNEL_02" sulla fruizione

4. Govway riceve la risposta dell'erogatore, dalla quale estrae il token di sicurezza al fine di effettuare i relativi controlli di validità e conservare la traccia come conferma di ricezione da parte dell'erogatore. Consultando la traccia relativa alla trasmissione della risposta (:numref:`modipa_traccia_risposta_fig`), sono visibili i dati di autenticazione dell'erogatore, i riferimenti temporali e l'identificativo del messaggio, nonché il digest del payload per la verifica di integrità.

   .. figure:: ../_figure_scenari/modipa_traccia_risposta.png
    :scale: 80%
    :align: center
    :name: modipa_traccia_risposta_fig

    Traccia della richiesta elaborata dall'erogatore


Conformità ai requisiti ModI PA
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
I requisiti iniziali, legati alla comunicazione basata su uno scenario ModI PA, sono verificati dalle seguenti evidenze:

1. La trasmissione è basata sul profilo "ID_AUTH_CHANNEL_02", riguardo la sicurezza canale, come evidenziato nei messaggi diangostici dalla presenza degli elementi dell'handshake SSL e relativi dati dei certificati scambiati (:numref:`modipa_ssl_auth_fruitore_fig`).

2. La sicurezza messaggio applicata è quella dei profili "ID_AUTH_REST_02" e "INTEGRITY_REST_01", come ampiamente mostrato nelle tracce dei messaggi di richiesta e risposta, dove sono presenti i certificati degli applicativi e le firme dei payload (e le relative validazioni).

3. La conferma di ricezione da parte dell'erogatore è costituita dalla risposta ottenuta dal fruitore, sul profilo di interazione bloccante, con il token di sicurezza e la firma del payload applicati sul messaggio di risposta.

4. Il non ripudio della trasmissione da parte del fruitore è garantito tramite la conservazione del messaggio ottenuto, comprensivo di riferimenti temporali, digest del payload, identità del mittente, il tutto garantito dalla firma digitale.

5. L'opponibilità verso i terzi è garantita dal mantenimento nell'archivio delle evidenze traciate, citate ai punti precedenti, con la possibilità, offerta dalla console govwayMonitor, di effettuare successive ricerche per la consultazione delle stesse.


Configurazione
--------------
Per la configurazione dello scenario descritto è necessario intervenire sulla govwayConsole (lato fruitore ed erogatore in base all'ambito di propria competenza). Per operare con la govwayConsole in modo conforme a quanto previsto dalla specifica del Modello di Interoperabilità 2018 si deve attivare, nella testata dell'interfaccia, il Profilo di Interoperabilità "ModI PA" (:numref:`modipa_profilo_f_fig`).

   .. figure:: ../_figure_scenari/modipa_profilo.png
    :scale: 80%
    :align: center
    :name: modipa_profilo_f_fig

    Profilo ModI PA della govwayConsole

Salvataggio Messaggi
~~~~~~~~~~~~~~~~~~~~
Per far gestire a Govway la peristenza dei messaggi scambiati, come prova di trasmissione per l'opponibilità ai terzi, è necessario intervenire sulla configurazione della funzionalità di tracciamento (vedi :ref:`modipa_config_tracciamento`).

Si procede quindi con i passi di configurazione del servizio.

Registrazione API
~~~~~~~~~~~~~~~~~
Si registra l'API "PetStore", fornendo il relativo descrittore OpenAPI 3, selezionando i profili "ID_AUTH_CHANNEL_02" (sicurezza canale) e "INTEGRITY_REST_01 con ID_AUTH_REST_02" (sicurezza messaggio) nella sezione "ModI PA" (vedi :ref:`modipa_api_profili`).


Applicativo
~~~~~~~~~~~
Si configura l'applicativo mittente indicando, nella sezione ModI PA, i parametri del keystore necessari affinché Govway possa produrre il token di sicurezza firmando per conto dell'applicativo (:numref:`modipa_applicativo_fruitore_fig`).

   .. figure:: ../_figure_scenari/modipa_applicativo_fruitore.png
    :scale: 80%
    :align: center
    :name: modipa_applicativo_fruitore_fig

    Configurazione applicativo fruitore


Fruizione
~~~~~~~~~
Si registra la fruizione "PetStore", relativa all'API precedentemente inserita, indicando i dati specifici nella sezione "ModI PA Richiesta" (:numref:`modipa_fruizione_richiesta_fig`). In particolare è possibile specificare quali header HTTP si vuole firmare, oltre al payload, e quale scadenza per il token impostare.

   .. figure:: ../_figure_scenari/modipa_fruizione_richiesta.png
    :scale: 80%
    :align: center
    :name: modipa_fruizione_richiesta_fig

    Configurazione richiesta della fruizione

La sezione "ModI PA Risposta" definisce i criteri per la validazione dei messaggi di risposta, come la posizione del token di sicurezza e il truststore per l'autenticazione dell'erogatore (:numref:`modipa_fruizione_risposta_fig`).

   .. figure:: ../_figure_scenari/modipa_fruizione_risposta.png
    :scale: 80%
    :align: center
    :name: modipa_fruizione_risposta_fig

    Configurazione risposta della fruizione


