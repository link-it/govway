.. _modipa_fruizione_soap:

Fruizione SOAP ModI PA
======================

Obiettivo
---------
Fruire di un servizio SOAP accessibile in accordo alla normativa prevista dal Modello di Interoperabilità.

Sintesi
-------
Mostriamo in questa sezione come procedere per l'integrazione di un applicativo con un servizio SOAP erogato nel rispetto della normativa italiana alla base dell'interoperabilità tra i sistemi della pubblica amministrazione. In particolare andiamo ad illustrare lo scenario, tra quelli prospettati nel Modello di Interoperabilità di AGID, che prevede le più ampie caratteristiche di sicurezza e affidabilità.
I requisiti di riferimento sono quelli descritti nella sezione 5.4.2 del Modello di Interoperabilità che, oltre a garantire la confidenzialità della comunicazione con autenticazione dell'interlocutore, prevedono supporto a garanzia dell'integrità del messaggio e non ripudiabilità dell'avvenuta trasmissione.

La figura seguente descrive graficamente questo scenario.

   .. figure:: ../_figure_scenari/FruizioneModIPA_SOAP.png
    :scale: 80%
    :align: center
    :name: fruizione_modipa_soap_fig

    Fruizione SOAP ModI PA

Le caratteristiche principali di questo scenario sono:

1. Un applicativo fruitore che dialoga con il servizio SOAP erogato in modalità ModI PA in accordo ad una API condivisa
2. La comunicazione diretta verso il dominio erogatore veicolata su un canale gestito con sicurezza canale di profilo "ID_AUTH_CHANNEL_02"
3. La confidenzialità e autenticità della comunicazione tra fruitore ed erogatore è garantita tramite sicurezza a livello messaggio con profilo IDAS02
4. L'integrità del messaggio scambiato è garantita tramite sicurezza messaggio aggiuntiva di profilo IDAS03
5. L'applicativo fruitore ottiene e conserva la conferma di ricezione del messaggio da parte dell'erogatore
6. Garanzia di opponibilità ai terzi e non ripudio delle trasmissioni

Esecuzione
----------
L'esecuzione dello scenario si basa sui seguenti elementi:

- una API di esempio (Credit Card Verification), basata su SOAP, profilo di interazione Bloccante e profili di sicurezza "ID_AUTH_CHANNEL_02", IDAS02 e IDAS03.
- un'istanza Govway per la gestione del profilo ModI PA nel dominio del fruitore.
- un client del dominio gestito che invoca l'azione di esempio "CheckCC" tramite Govway.

Per eseguire e verificare lo scenario si può utilizzare il progetto Postman a corredo con la request "8. Fruizione SOAP ModI PA", che è stato preconfigurato per il funzionamento con le caratteristiche descritte sopra.

Dopo aver eseguito la "Send" e verificato il corretto esito dell'operazione è possibile andare a verificare cosa è accaduto, nel corso dell'elaborazione della richiesta, andando a consultare la console govwayMonitor.

1. Il messaggio di richiesta inviato dal fruitore viene elaborato da Govway che, tramite la configurazione della firma digitale associata all'applicativo mittente, è in grado di produrre l'header WS-Security da inserire nella richiesta inviata all'erogatore. Da govwayMonitor si può visualizzare il messaggio di richiesta in uscita, analogo a quanto già visto in :numref:`modipa_erogazione_messaggio_richiesta_soap_fig`.

2. Per verificare l'utilizzo del canale SSL, in accordo al profilo "ID_AUTH_CHANNEL_02", si procede come già illustrato per :ref:`scenari_erogazione_rest_modipa`.

3. Govway riceve la risposta dell'erogatore, dalla quale estrae l'header WS-Security al fine di effettuare i relativi controlli di validità e conservare la traccia come conferma di ricezione da parte dell'erogatore. Consultando la traccia relativa alla trasmissione della risposta (:numref:`modipa_traccia_risposta_soap_fig`), sono visibili i dati di autenticazione dell'erogatore, i riferimenti temporali e l'identificativo del messaggio, nonché il digest del payload per la verifica di integrità.

   .. figure:: ../_figure_scenari/modipa_traccia_risposta_soap.png
    :scale: 80%
    :align: center
    :name: modipa_traccia_risposta_soap_fig

    Traccia della richiesta elaborata dall'erogatore

Conformità ai requisiti ModI PA
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
La verifica dei requisiti ModI PA per questo scenario non differisce da quanto già descritto in :ref:`modipa_conformita`.

Il processo di configurazione per questo scenario è del tutto analogo a quello descritto per lo scenario :ref:`scenari_fruizione_rest_modipa`. Nel seguito sono evidenziate le sole differenze.

Registrazione API
~~~~~~~~~~~~~~~~~
In fase di registrazione della relativa API, tenere presente che saranno selezionati i profili:

- "ID_AUTH_CHANNEL_02" per la sicurezza canale
- IDAS03 (IDAS02) per la sicurezza messaggio

Fruizione
~~~~~~~~~
Si registra la fruizione SOAP, relativa all'API precedentemente inserita, indicando i dati specifici nella sezione "ModI PA Richiesta" (:numref:`modipa_fruizione_richiesta_soap_fig`).

   .. figure:: ../_figure_scenari/modipa_fruizione_richiesta_soap.png
    :scale: 80%
    :align: center
    :name: modipa_fruizione_richiesta_soap_fig

    Configurazione richiesta della fruizione

La sezione "ModI PA Risposta" definisce i criteri per la validazione dei messaggi di risposta (:numref:`modipa_fruizione_risposta_soap_fig`).

   .. figure:: ../_figure_scenari/modipa_fruizione_risposta_soap.png
    :scale: 80%
    :align: center
    :name: modipa_fruizione_risposta_soap_fig

    Configurazione risposta della fruizione
