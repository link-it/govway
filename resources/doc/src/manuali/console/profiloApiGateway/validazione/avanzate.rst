.. _configSpecificaValidazioneOpzioniAvanzate:

Opzioni Avanzate
~~~~~~~~~~~~~~~~~~~~~~~

È possibile modificare l'engine di validazione registrando le seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *validation.buffer.enabled*: consente di abilitare o disabilitare il buffer che preserva i dati letti dallo stream. Se l'opzione viene disabilitata, il contenuto inoltrato al backend verrà ottenuto serializzando l'oggetto costruito in seguito alla lettura dello stream (es. serializzazione dell'elemento DOM in xml). I valori associabili alle proprietà sono 'true' o 'false'.
