Conversione da REST a SOAP
**************************

Una particolare trasformazione del contenuto della richiesta è quella di convertire il formato da REST a SOAP. Questa funzionalità si ottiene abilitando la sezione "Trasformazione SOAP", presente nel caso di servizi REST. I dati da fornire per la configurazione sono (:numref:`trasf_Soap`):

- Versione: selezione della versione del protocollo SOAP
- SOAP Action: indicazione della SOAP Action da utilizzare
- Imbustamento SOAP: se il messaggio ottenuto con le operazioni di trasformazione applicate non è in formato SOAP è possibile decidere di far generare al gateway gli elementi di imbustamento. Le opzioni possibili sono:

    - Disabilitato: nessun imbustamento.
    - Utilizza contenuto come SOAP Body: il contenuto attuale viene utilizzato come SOAP Body nel contesto dell'envelope creato.
    - Utilizza contenuto come Attachment: il contenuto attuale viene inserito come attachment relativo al messaggio SOAP generato. Se viene selezionata questa opzione dovranno essere forniti ulteriori dati, quali:

        - Content Type Attachment: è possibile specificare un Content-Type per l'attachment.
        - SOAP Body: stabilire quale deve essere il contenuto del SOAP Body. Per questo punto si procede analogamente a quanto già descritto per la trasformazione del contenuto principale della richiesta.

   .. figure:: ../../_figure_console/TrasformazioniSOAP.png
    :scale: 100%
    :align: center
    :name: trasf_Soap

    Conversione da REST a SOAP