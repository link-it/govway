Utilizzare lo script  **genera_catena.sh**; avviarlo senza parametri per vedere l'help.


Viene inizializzata la Certification Authority e vengono creati i seguenti certificati:

- cn=CA TEST,o=Esempio,c=it (certificato Root CA)
- cn=OCSP TEST,o=Esempio,c=it (Certificato signer OCSP)
- cn=TEST.esempio.it,o=Esempio,c=it (Certificato server)
- cn=Client-TEST.esempio.it,o=Esempio,c=it (certificato client)


Per revocare un certificato tra quelli emessi dalla CA usare lo script  **revoca.sh**; avviarlo senza parametri per vedere l'help.


Infine per avviare il responder OCSP correttamente usare lo scrit **ocsp_responder.sh**; avviarlo senza parametri per vedere l'help.
Lo script visualizza il comando da usare per avviare un responder OCSP utilizzando la CA creata
