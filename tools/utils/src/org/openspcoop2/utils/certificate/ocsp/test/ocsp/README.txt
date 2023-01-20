Per rigenerare i certificati:
- ./clean.sh
- ./genera_catena.sh TEST test.esempio.it o=Esempio,c=it
- ./revoca.sh TEST test.esempio.it o=Esempio,c=it

Per ottenere i comandi per avviare i server:
- ./ocsp_responder.sh TEST
