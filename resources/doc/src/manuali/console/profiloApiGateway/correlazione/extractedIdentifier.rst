.. _correlazione_extractedIdentifier:

Identificativo estratto nullo o stringa vuota
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

L'estrazione di un identificativo nullo o stringa vuota viene trattato come processo d'identificazione fallito. È possibile modificare questo comportamento registrando una delle seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione (i valori associabili alle proprietà sono 'true' o 'false'):

- *correlation.request.extractedIdentifierIsNull.abortTransaction*: (default:true) indicazione su come trattare un identificativo nullo estratto dalla richiesta;

- *correlation.response.extractedIdentifierIsNull.abortTransaction*: (default:true) indicazione su come trattare un identificativo nullo estratto dalla risposta;

- *correlation.request.extractedIdentifierIsEmpty.abortTransaction*: (default:true) indicazione su come trattare un identificativo 'stringa vuota' estratto dalla richiesta;

- *correlation.response.extractedIdentifierIsEmpty.abortTransaction*: (default:true) indicazione su come trattare un identificativo 'stringa vuota' estratto dalla risposta;
