Conversione da SOAP a REST
**************************

Una particolare trasformazione del contenuto della richiesta è quella di convertire il formato da SOAP a REST. Questa funzionalità si ottiene abilitando la sezione "Trasformazione REST", presente nel caso di servizi SOAP. I dati da fornire per la configurazione sono (:numref:`trasf_Rest`):

- Path: path della risorsa cui deve fare riferimento il nuovo messaggio di richiesta REST-
- HTTP Method: il  metodo HTTP utilizzato.

   .. figure:: ../../_figure_console/TrasformazioniRest.png
    :scale: 100%
    :align: center
    :name: trasf_Rest

    Conversione da SOAP a REST