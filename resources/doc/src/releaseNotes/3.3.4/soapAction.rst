Miglioramenti alla funzionalità di Identificazione dell'Azione
---------------------------------------------------------------

La modalità di identificazione dell'azione 'SOAPAction' è stata rivista:

- viene prima ricercata un'azione, all'interno dell'interfaccia dell'API invocata, contenente un soap binding con la SOAPAction presente nella richiesta (nuova feature);

- se la prima ricerca non va a buon fine viene verificato se il valore presente nella SOAPAction della richiesta corrisponde al nome di un'azione registrata sull'API (precedente comportamento).
