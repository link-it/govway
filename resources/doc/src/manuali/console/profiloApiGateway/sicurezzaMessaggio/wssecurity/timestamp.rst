.. _sicurezzaLivelloMessaggioWSSecurityTimestamp:

WS-Security Timestamp
~~~~~~~~~~~~~~~~~~~~~

Lo schema gestisce l'elemento Timestamp dell'header WS-Security, che indica la data di creazione e la data di scadenza del messaggio. Gli stessi campi compaiono nella sezione *Timestamp* degli schemi di firma, cifratura e SAML quando viene abilitata l'azione *Timestamp*.

Per i campi *Actor*, *MustUnderstand* e *Ripulisci Messaggio dalle Informazioni Sicurezza* si rimanda alla sezione :ref:`sicurezzaLivelloMessaggioWSSecurityComuni`.

**Ricezione (Receiver)**

Viene verificata la presenza e la validità temporale dell'elemento Timestamp: il messaggio viene rifiutato se il Timestamp risulta scaduto (:numref:`wssReceiverTimestampFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSReceiverTimestamp.png
    :scale: 70%
    :align: center
    :name: wssReceiverTimestampFig

    Ricezione: WS-Security Timestamp

- *Future Time to Live (seconds)* (default: 60): tolleranza, in secondi, con cui viene accettata una data di creazione nel futuro, per compensare differenze di orologio tra i sistemi.

**Spedizione (Sender)**

Viene aggiunto all'header WS-Security un elemento Timestamp (:numref:`wssSenderTimestampFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderTimestamp.png
    :scale: 70%
    :align: center
    :name: wssSenderTimestampFig

    Spedizione: WS-Security Timestamp

- *Time to Live (seconds)* (default: 300): differenza, in secondi, tra la data di creazione e la data di scadenza.
