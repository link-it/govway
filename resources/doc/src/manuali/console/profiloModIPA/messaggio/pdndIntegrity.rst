.. _modipa_pdnd_integrity:

ID_AUTH_REST_01 (PDND) + INTEGRITY_SOAP_01 / INTEGRITY_REST_01
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il token di autenticazione ID_AUTH_REST_01 descritto nella sezione ':ref:`modipa_pdnd`' è utilizzabile in combinazione con il token di integrità descritto nella sezione ':ref:`modipa_idar03`' sia su API di tipo REST che di tipo SOAP.

Per attuare la configurazione su API di tipo REST deve essere utilizzato un pattern di sicurezza 'INTEGRITY_REST_01' dove la voce 'Header HTTP del Token' deve essere valorizzata solamente con l'header 'Agid-JWT-Signature'.

Nessuna particolare indicazione è invece necessaria per attuare la configurazione su API di tipo SOAP dove è sufficiente utilizzare il pattern di sicurezza 'INTEGRITY_SOAP_01'.

Su entrambi gli scenari l'autenticazione dell'applicativo chiamante avverà tramite il token ID_AUTH_REST_01 generato dalla PDND e veicolato su header HTTP 'Authorization Bearer'. Invece la gestione dell'integrità del messaggio avverrà secondo le modalità descritte nella sezione ':ref:`modipa_idar03`'.























