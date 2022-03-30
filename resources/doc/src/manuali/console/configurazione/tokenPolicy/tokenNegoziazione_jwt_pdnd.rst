.. _tokenNegoziazionePolicy_pdnd:

Signed JWT (PDND)
-----------------

La modalità di negoziazione basata su uno scambio di un JWT firmato con l'authorization server, descritta nella sezione ':ref:`tokenNegoziazionePolicy_jwt`', è stata selezionata come modalità di negoziazione dei token sulla Piattaforma Digitale Nazionale Dati (PDND).

Il protocollo di negoziazione, oltre ai parametri standard previsti dal rfc 7523 (https://datatracker.ietf.org/doc/html/rfc7523#section-2.2), prevede l'inserimento di alcuni parametri aggiuntivi all'interno del payload del JWT firmato: 

- *purposeId*: rappresente l’identificativo della finalità dell’accordo di adesione recuperabile dalla piattaforma PDND;

- *sessionInfo*: informazioni di sessione che non vengono gestite sulla piattaforma PDND ma consentono di essere inviate al momento della negoziazione per poi essere riportate all'interno dell'access token generato dalla PDND.

Entrambi i parametri sono configurabili attivando la modalità PDND successivamente alla selezione del tipo 'Signed JWT' come modalità di negoziazione (:numref:`tokenPDNDFig`) e (:numref:`tokenPDNDFig2`).


   .. figure:: ../../_figure_console/TokenPolicy-negoziazione-pdnd.png
    :scale: 100%
    :align: center
    :name: tokenPDNDFig

    Modalità di negoziazione 'Signed JWT' via PDND


   .. figure:: ../../_figure_console/TokenPolicy-negoziazione-pdnd2.png
    :scale: 100%
    :align: center
    :name: tokenPDNDFig2

    Modalità di negoziazione 'Signed JWT' via PDND: purposeId e sessionInfo

Infine anche nella sezione 'Dati Richiesta', nel caso venga attivata la modalità *PDND*, viene aggiunto un ulteriore parametro richiesto dal protocollo PDND: 'client_id'. Se non viene definito alcun valore per il parametro verrà utilizzato il medesimo valore associato al Client ID definito nel payload del JWT.
