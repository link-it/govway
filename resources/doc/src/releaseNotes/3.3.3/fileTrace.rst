Miglioramenti alla funzionalità di Tracciatura su File
------------------------------------------------------

Aggiunte ulteriori informazioni, inerenti le comunicazioni gestite dal gateway, che possono essere riversate nei file di log associati ai topic di file trace:

- resultClass, resultClassOk, resultClassKo, resultClassFault: classe a cui appartiene l’esito della transazione tra OK, KO e FAULT;
- errorDetail: dettaglio dell’errore avvenuto durante la gestione della transazione;
- requester: rappresenta il richiedente della richiesta e assumerà la prima informazione valorizzata, trovata nella richiesta, tra tokenUsername, tokenSubject[@tokenIssuer], application, principal e tokenClientId;
- ipRequester: rappresenta l’indirizzo ip del richiedente e viene valorizzato con il forwardedIP se presente, o altrimenti con il clientIP;
- principalAuthType: tipo di autenticazione (basic/ssl/principal) con cui l’applicativo è stato autenticato;
- diagnostics e errorDiagnostics: consentono di accedere ai diagnostici emessi da GovWay durante la gestione della richiesta;
- senderId, providerId, apiId, apiInterfaceId, profileLabel: consentono di ottenere delle informazioni già accessibili in precedenza con un nuovo formato conforme al profilo di interoperabilità utilizzato.
