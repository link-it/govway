Miglioramenti alla funzionalità dei Connettori
----------------------------------------------

È adesso possibile specificare il metodo di autenticazione 'Api Key' consentendo di inviare al backend una chiave di identificazione veicolata in un header http come descritto nella specifica 'OAS3 API Keys' (https://swagger.io/docs/specification/authentication/api-keys/).

Viene supportata anche la modalità 'App ID' che prevede oltre all'ApiKey un identificatore dell'applicazione, modalità denominata 'Multiple API Keys' nella specifica 'OAS3 API Keys'.

La configurazione permette anche di personalizzare il nome degli header http, rispetto a quanto indicato nella specifica 'OAS3 API Keys'.

Inoltre è stata aggiunta la possibilità di abilitare o disabilitare la funzionalità di 'encoded word' per i valori degli header HTTP, oltre alla possibilità di personalizzarne gli aspetti di codifica per singole erogazioni o fruizioni di API attraverso la definizione di proprietà specifiche.

