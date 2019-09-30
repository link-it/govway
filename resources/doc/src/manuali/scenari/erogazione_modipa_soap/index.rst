.. _modipa_erogazione_soap:

Erogazione SOAP ModI PA
=======================

Obiettivo
---------
Esporre un servizio SOAP accessibile in accordo alla normativa prevista dal Modello di Interoperabilità 2018.

Sintesi
-------
Mostriamo in questa sezione come procedere per l'esposizione di un servizio SOAP da erogare nel rispetto della normativa italiana alla base dell'interoperabilità tra i sistemi della pubblica amministrazione. In particolare andiamo ad illustrare lo scenario, tra quelli prospettati nel Modello di Interoperabilità di AGID, che prevede le più ampie caratteristiche di sicurezza e affidabilità.
I requisiti di riferimento sono quelli descritti nella sezione 5.4.2 del Modello di Interoperabilità che, oltre a garantire la confidenzialità della comunicazione con autenticazione dell'interlocutore, prevedono supporto a garanzia dell'integrità del messaggio e non ripudiabilità dell'avvenuta trasmissione.

La figura seguente descrive graficamente questo scenario.

   .. figure:: ../_figure_scenari/ErogazioneModIPA_SOAP.png
    :scale: 80%
    :align: center
    :name: erogazione_modipa_soap_fig

    Erogazione SOAP ModI PA

Le caratteristiche principali di questo scenario sono:

1. Un applicativo eroga un servizio SOAP, rivolto a fruitori di domini esterni, in conformità al Modello di Interoperabilità AGID
2. La comunicazione con i domini esterni avviene su un canale gestito con sicurezza canale di profilo IDAC02
3. La confidenzialità e autenticità della comunicazione tra il servizio erogato e ciascun fruitore è garantita tramite sicurezza a livello messaggio con profilo IDAS02
4. L'integrità del messaggio scambiato è garantita tramite sicurezza messaggio aggiuntiva di profilo IDAS03
5. Ciascun fruitore riceve conferma di ricezione del messaggio da parte dell'erogatore
6. Garanzia di opponibilità ai terzi e non ripudio delle trasmissioni con persistenza delle prove di trasmissione

Esecuzione
----------
L'esecuzione dello scenario si basa sui seguenti elementi:

- una API di esempio (SOAPBlockingImpl), basata su SOAP, profilo di interazione Bloccante e profili di sicurezza IDAC02, IDAS02 e IDAS03.
- un'istanza Govway per la gestione del profilo ModI PA nel dominio dell'erogatore.
- un client del dominio esterno che invoca l'azione di esempio "MRequest".
- il server SOAPBlockingImpl di esempio che riceve le richieste inoltrate dal Govway e produce le relative risposte.

Per eseguire e verificare lo scenario si può utilizzare il progetto Postman a corredo con la request "7. Erogazione SOAP ModI PA", che è stato preconfigurato per il funzionamento con le caratteristiche descritte sopra.

Dopo aver eseguito la "Send" e verificato il corretto esito dell'operazione è possibile andare a verificare cosa è accaduto, nel corso dell'elaborazione della richiesta, andando a consultare la console govwayMonitor.

1. Per verificare l'utilizzo del canale SSL, in accordo al profilo IDAC02, si procede come già illustrato per :ref:`scenari_erogazione_rest_modipa`

2. Dal dettaglio della richiesta si può visualizzare il messaggio che è stato inviato dal fruitore, come in :numref:`modipa_erogazione_messaggio_richiesta_soap_fig`. Come si nota, il messaggio SOAP contiene nell'header WS-Security, sia il token di sicurezza (elemento "BinarySecurityToken"), sia il digest del payload (elemento "DigestValue"), prodotti dal fruitore con la relativa firma digitale (elemento "SignatureValue").

   .. figure:: ../_figure_scenari/modipa_erogazione_messaggio_richiesta_soap.png
    :scale: 80%
    :align: center
    :name: modipa_erogazione_messaggio_richiesta_soap_fig

    Messaggio inviato dal fruitore

3. Il messaggio ricevuto dal Govway viene quindi validato, sulla base dei profili di sicurezza previsti nello scambio, verificando in questo caso l'identità del fruitore, la validità temporale, la corrispondenza del digest relativo al payload. Solo in caso di superamento dell'intero processo di validazione, il messaggio viene inoltrato al servizio erogatore.
Le evidenze del processo di validazione sono visibili sulla govwayMonitor, andando a consultare la traccia del messaggio di richiesta (:numref:`modipa_traccia_richiesta_soap_fig`). Nella sezione "Sicurezza Messaggio" sono riportate le informazioni estratte dal token di sicurezza presente nell'header soap.

   .. figure:: ../_figure_scenari/modipa_traccia_richiesta_soap.png
    :scale: 80%
    :align: center
    :name: modipa_traccia_richiesta_soap_fig

    Traccia della richiesta elaborata dall'erogatore

4. Dopo l'inoltro al servizio erogatore, Govway riceve la risposta e la elabora producendo il relativo header ws-security da inserire nel messaggio di risposta. Sulla console govwayMonitor è possibile visualizzare il messaggio di risposta in uscita (analogamente a :numref:`modipa_erogazione_messaggio_richiesta_soap_fig`).


Conformità ai requisiti ModI PA
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
La verifica dei requisiti ModI PA per questo scenario non differisce da quanto già descritto in :ref:`modipa_conformita`.

Il processo di configurazione per questo scenario è del tutto analogo a quello descritto per lo scenario :ref:`scenari_erogazione_rest_modipa`. Nel seguito sono evidenziate le sole differenze.

Registrazione API
~~~~~~~~~~~~~~~~~
In fase di registrazione della relativa API, tenere presente che saranno selezionati i profili:

- IDAC02 per la sicurezza canale
- IDAS03 (IDAS02) per la sicurezza messaggio


Erogazione
~~~~~~~~~~
Si registra l'erogazione SOAP, relativa all'API precedentemente inserita, indicando i dati specifci nella sezione "ModI PA Richiesta" (:numref:`modipa_erogazione_richiesta_soap_fig`). In questo contesto vengono inseriti i dati necessari per validare le richieste in ingresso.

   .. figure:: ../_figure_scenari/modipa_erogazione_richiesta_soap.png
    :scale: 80%
    :align: center
    :name: modipa_erogazione_richiesta_soap_fig

    Configurazione richiesta dell'erogazione

La sezione "ModI PA Risposta" si utilizza per indicare i parametri per la produzione del token di sicurezza da inserire nel messaggio di risposta (:numref:`modipa_erogazione_risposta_soap_fig`).

   .. figure:: ../_figure_scenari/modipa_erogazione_risposta_soap.png
    :scale: 80%
    :align: center
    :name: modipa_erogazione_risposta_soap_fig

    Configurazione risposta dell'erogazione
